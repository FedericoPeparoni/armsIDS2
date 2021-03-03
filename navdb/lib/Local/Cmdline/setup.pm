# vim:ts=2:sts=2:sw=2:et
package Local::Cmdline::setup;

use warnings;
use strict;
use Carp;
use English;
use Cwd qw(abs_path);
use File::Spec::Functions;
use File::Basename;
use Local::ShortestPath;
use Getopt::Long qw(:config gnu_getopt no_auto_abbrev no_ignore_case);
use Local::ShellQuote qw(:all);
use Local::Utils qw(:all);
use Local::Versions qw(:all);
use Local::Project qw(:all);

my $AERO_DATA_FILE  = undef;

my @CLEANUP_ON_ERROR;
my $LOAD_AERO_DATA = 1;

# Create database user accounts ("create_users.sql")
sub do_create_db_users() {
  my $script = "create_users.sql";
  log_debug "making sure database user roles exist";
  my $cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-f", native_path ($script)) . " >/dev/null");
  shell ($cmd);
  $? == 0 or die "failed to create or update database roles\n";
}

# Grant permissions on DB objects to DB user accounts ("update_permissions.sql")
sub do_update_permissions() {
  log_debug "updating permissions on database objects";
  my $cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-v", "navdb_name=$DB_NAME", "-f", native_path ("update_permissions.sql")) . " >/dev/null");
  shell ($cmd);
  $? == 0 or die "failed to update database permissions\n";
}

# Find 7z.exe on Windows
sub find_7z() {
  
  # Look for 7z in the normal places in $ENV{PATH}
  my $command = $PROJECT_CONFIG->get ("SEVENZ_PATH") || "7z";
  my $path = which ($command);
  if ($path) {
    log_info "found 7z: \`$path'";
    return $path;
  }

  # On Windows search C:\Program Files\7z or similar
  if ($OSNAME eq 'cygwin' or $OSNAME eq 'msys') {
    log_debug "searching for 7z in common Windows directories";
    my @paths;
    my $root_prefix = $OSNAME eq 'cygwin' ? "/cygdrive" : "";
    my @drive_list = qw(c d);
    for my $drive (@drive_list) {
      push @paths, "$root_prefix/$drive/Program Files/7-Zip/7z.exe",
                   "$root_prefix/$drive/Program Files (x86)/7-Zip/7z.exe";
    }
    for $path (@paths) {
      my $real_path = which ($path);
      if ($real_path) {
        log_debug "found 7z: \`$path'";
        return $real_path;
      };
    }
  };
  # not found
  return undef;
}

# Create the database ("create_db.sql") if it doesn't already exist. Returns true
# if database was created, false if it already exists.
sub do_create_db() {

  my $aero_data_file;
  my ($gunzip_path, $bunzip2_path, $unxz_path, $sevenz_path);

  # check whether the database exists
  db_exists and do {
    log_debug "database \`$DB_NAME' already exists";
    return undef;
  };

  # if we want to load initial data after creation, then find the load file
  if ($LOAD_AERO_DATA) {
    # if AERO_DATA_FILE is specified, use it
    if (defined $AERO_DATA_FILE) {
      $aero_data_file = abs_path ($AERO_DATA_FILE);
      defined $aero_data_file or die "$AERO_DATA_FILE: $!\n";
    }
    # if we are installed look for any .sql or .sql.{gz,bz2,xz} files in /usr/share/navdb/...
    elsif (defined $INSTALLED) {
      my @files = glob ("$SQL_DIR/data/*.sql $SQL_DIR/data/*.sql.{gz,bz2,xz}");
      if (scalar (@files) <= 0) {
        log_error "no SQL files found in $SQL_DIR/data";
        log_error "please specify a file name to load or use \`--nodata' to create an empty database";
        log_error "type \`$PROGNAME --help' for more information";
        die "unable to create database\n";
      }
      if (scalar (@files) > 1) {
        log_error "multiple SQL files found in $SQL_DIR/data";
        log_error "please specify a file name to load or use \`--nodata' to create an empty database";
        log_error "type \`$PROGNAME --help' for more information";
        die "unable to create database\n";
      };
      $aero_data_file = $files[0];
    }
    # Otherwise, we are running from the (uninistalled) source tree:
    # - parse out the data file name from package.conf
    # - find it in source tree
    # - download it if necessary
    else {
      # parse out data file url from package.conf
      my $package_conf_dir = catfile ($CONF_DIR, "..");
      my $package_conf_cmd = "unset AERO_DATA_URL ; . $package_conf_dir/package.conf ; echo \$AERO_DATA_URL";
      log_shell_cmd $package_conf_cmd;
      my $data_file_url = trim (`$package_conf_cmd`);
      $data_file_url or confess "unable to determine load data file URL";
      # run the download script
      my $download_cmd = "cd " . shell_quote ("$SQL_DIR/data") . " && ./download.sh";
      shell ($download_cmd);
      $? == 0 or die "failed to download $data_file_url\n";
      # find the file:
      # - if an uncompressed version exists, use it
      # - otherwise use the compressed version
      my $basename_compressed = "$SQL_DIR/data/" . basename ($data_file_url);
      my $basename = $basename_compressed; $basename =~ s/\.(?:bz2|gz|xz)$//g;
      if (-f $basename) {
        $aero_data_file = $basename;
      }
      else {
        $aero_data_file = $basename_compressed;
        if (not -f $aero_data_file) {
          log_error "$aero_data_file: file doesn't exist or is unreadable\n";
          log_error "please specify a file name to load or use \`--nodata' to create an empty database";
          log_error "type \`$PROGNAME --help' for more information";
          die "unable to create database\n";
        };
      };
    };
    # at this point file must exist
    if (not defined $aero_data_file) {
      confess "internal error: SQL load file name not defined";
    };
    stat $aero_data_file or die "$aero_data_file $!\n";
    if (not -f _ and not -p _) {
      die "$aero_data_file not a regular file\n";
    };
    log_debug "load file: $aero_data_file";

    # make sure the uncompress program exists
    if ($aero_data_file =~ /\.xz$/) {
      $unxz_path = which ("unxz") or $sevenz_path = find_7z or die "\`unxz' or \`7z' not found in $ENV{PATH}\n";
    }
    elsif ($aero_data_file =~ /\.gz$/) {
      $gunzip_path = which ("gunzip") or $sevenz_path = find_7z or die "\`gunzip' or \`7z' not found in $ENV{PATH}\n";
    }
    elsif ($aero_data_file =~ /\.bz2$/) {
      $bunzip2_path = which ("bunzip2") or $sevenz_path = find_7z or die "\`gunzip' or \`7z' not found in $ENV{PATH}\n";
    }
  };

  # create the db
  log_notice "creating database \`$DB_NAME' data model version \`" . $SQL_CONFIG->get ("navdb_version") . "'";
  log_notice "creating database";
  shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-v", "navdb_locale=" . navdb_locale(), "-v", "navdb_name=$DB_NAME", "-f", native_path ("create_db.sql")) . " >/dev/null"));
  $? == 0 or die "failed to create database \`$DB_NAME'\n";
  push @CLEANUP_ON_ERROR, \&do_destroy_db;

  # create extensions
  log_notice "creating extensions";
  shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-f", native_path ("create_extensions.sql"), "--dbname=$DB_NAME") . " >/dev/null"));
  $? == 0 or die "failed to create extensions\n";

  # create schemas and tables
  log_notice "creating schema objects";
  shell (chdir_cmd ($SQL_DIR, psql_owner_script ("create_schemas.sql") . " >/dev/null"));
  $? == 0 or die "failed to create schema objects\n";

  # set permissions for all DB objects
  do_update_permissions;

  # load navdb schema
  if ($LOAD_AERO_DATA) {
    # download data files if necessary
    if (-f "$SQL_DIR/data/download.sh") {
      my $cd_cmd = "cd " . shell_quote ("$SQL_DIR/data");
      my $dl_cmd = "sh ./download.sh";
      my $cmd = "$cd_cmd && $dl_cmd";
      shell $cmd;
      $? == 0 or die "failed to download data files\n";
    };
    log_notice "";
    # execute load_aero_tables.sql; that script expects the load file on its STDIN:
    #   either pipe original compressed file through unxz or similar to psql's STDIN
    #     -- OR --
    #   redirect psql's STDIN to the original uncompressed file
    my $load_cmd = chdir_cmd ($SQL_DIR, psql_owner_script ("load_aero_tables.sql") . " >/dev/null");
    if ($aero_data_file =~ /\.xz$/) {
      if ($unxz_path) {
        $load_cmd = shell_quote ($unxz_path, "-ck", $aero_data_file) . " | $load_cmd";
      }
      else {
        $load_cmd = shell_quote ($sevenz_path, "e", "-so", native_path ($aero_data_file)) . " | $load_cmd";
      }
    }
    elsif ($aero_data_file =~ /\.gz$/) {
      if ($gunzip_path) {
        $load_cmd = shell_quote ($gunzip_path, "-c", $aero_data_file) . " | $load_cmd";
      }
      else {
        $load_cmd = shell_quote ($sevenz_path, "e", "-so", native_path ($aero_data_file)) . " | $load_cmd";
      }
    }
    elsif ($aero_data_file =~ /\.bz2$/) {
      if ($bunzip2_path) {
        $load_cmd = shell_quote ($bunzip2_path, "-ck", $aero_data_file) . " | $load_cmd";
      }
      else {
        $load_cmd = shell_quote ($sevenz_path, "e", "-so", native_path ($aero_data_file)) . " | $load_cmd";
      }
    }
    else {
      $load_cmd = $load_cmd . " <" . shell_quote ($aero_data_file);
    };
    # run this command as user "navdb"
    log_notice "loading initial feature data from $aero_data_file";
    shell ($load_cmd);
    $? == 0 or die "failed to load initial feature data\n";
  };

  return 1;

}

# Destroy the database
sub do_destroy_db() {
  log_notice "terminating existing client connections";
  kill_db_sessions;
  log_notice "destroying database \`$DB_NAME'";
  shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-c", "drop database $DB_NAME;") . " >/dev/null"));
  $? == 0 or die "failed to drop database\n";
}

# Return true if the given schema/view exists
sub db_view_exists($$) {
  my ($schema, $view) = @_;
  my $cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-c", "select count(*) from information_schema.views where lower(table_schema) = '$schema' and lower(table_name) = '$view'"));
  log_shell_cmd $cmd;
  my $out = trim (`$cmd`);
  if ($out =~ /^\d+$/ and $out >= 1) {
    return 1;
  };
  return undef;
}

# Determine current data model version from "navdb_meta.dm_version".
sub do_get_current_dm_version() {
  my $cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-c", "select dm_version from navdb_common.navdb_admin limit 1"));
  log_shell_cmd $cmd;
  my $version = `$cmd`;

  $? == 0 or die "unable to determine data model version\n";
  $version =~ s/^\s+//go;
  $version =~ s/\s+$//go;
  #
  # IDS Italy's version of NAVDB diverged in 2017 by introducing conflicting data model versions:
  # 
  #   8.3.7:  added new views "v_runways_by_airport" and "v_services_by_airport" and modified views
  #           "v_runway" and "v_airport"
  #   8.3.8:  re-created "v_airport" during upgrade (no structural changes)
  #   8.3.9:  looks the same as v8.3.7 in the main branch from Canada, but the upgrade script is botched
  #           as it re-creates all views except the ones introduced in 8.3.7 above.
  #   
  # The only structural changes in these updates are the views in (Italian) version 8.3.7.
  # These changes have been integrated in this project's main creation scripts. But we also
  # want the upgrades to work seamlessly by re-numbering the divergent/conflicting version numbers
  # in case we are executing the upgrade on top of a divergent Italian database. So, if we detect the
  # Italian schma, we pretend that it is version "8.3.6.idsrome.1" -- exactly between 8.3.6
  # (the last common ancestor) and 8.3.7; then run upgrade scripts normally.
  #
  do {
    my $italian_schema_detected;
    # These Italian versions contain the view "v_services_by_airport", but Canadian versions don't
    if (($version eq "8.3.7" or $version eq "8.3.8") and db_view_exists ("navdb", "v_services_by_airport")) {
      $italian_schema_detected = 1;
    }
    # The version 8.3.9 doesn't exist in the Canadian branch at all
    elsif ($version eq "8.3.9") {
      $italian_schema_detected = 1;
    };
    # Pretend that we are at version 8.3.6.idsrome.1
    if ($italian_schema_detected) {
      return "8.3.6.idsrome.1"
    };
  };
  return $version;
}

# Usage: @list = do_find_upgrade_scripts ($FROM_VERSION, $TO_VERSION)
# Find the sequence of scripts for upgrading the data model from one version to another.
# Returns a list of hashes; each hash is:
#   {
#      from => $FROM_VERSION,
#      to => $TO_VERSION,
#      script => $SCRIPT_PATH // full path to upgrade script
#   }
#
# The upgrade scripts are in ./sql/upgrade/*_ugrade_${FROM}_to_${TO}/upgrade.sql.
# 
sub do_find_upgrade_scripts ($$) {
  local $_;
  my ($from, $to) = @_;
  my @scripts;

  # find all upgrade directories in ./sql/upgrade
  #my $graph = Local::Graph::Directed->new;
  my $script_map = {};
  my $script_graph = [];
  #my $version_re = qr([0-9][0-9a-zA-Z.-]*);
  my $version_re = qr(.*);
  my $upgrade_dir = catfile ($SQL_DIR, "upgrade");
  opendir DH, $upgrade_dir or die "$upgrade_dir: $!\n";
  while (defined ($_ = readdir DH)) {
    my $dir = "$upgrade_dir/$_";
    -d $dir or next;
    /^(?:[0-9_]+?_)?upgrade_($version_re)_to_($version_re)$/o and do {
      my ($script_from, $script_to) = ($1, $2);
      versioncmp ($script_from, $script_to) == 0 and next;
      my $script_path = catfile ($dir, "upgrade.sql");
      stat $script_path or die "$dir: no \`upgrade.sql' file found\n";
      -f _ or die "$script_path: not a regular file\n";

      # build a graph of connected from/to version pairs
      push @$script_graph, [ $script_from, $script_to ];
      #$graph->add_edge ($script_from, $script_to);

      # also save script file names for each pair
      $script_map->{$script_from}->{$script_to} = $script_path;
    };
  };
  closedir DH or die "closedir(): $!\n";

  # Find and return the shortest path that connects the versions we are looking for
  #my @path = $graph->SP_Dijkstra ($from, $to);
  my @path = shortest_path ($from, $to, $script_graph);
  die "no scripts found for upgrading version $from to $to\n" if $#path < 1;
  while ($#path > 0) {
    my $script = $script_map->{$path[0]}->{$path[1]};
    push @scripts, { from => $path[0], to => $path[1], script => $script };
    shift @path;
  };
  return @scripts;
}

# Upgrade the database; returns true if database was upgraded and false
# if it's already up to date.
sub do_upgrade_db() {
  my $old_version = do_get_current_dm_version;
  my $sql_config = $SQL_CONFIG->filename;
  my $new_version = $SQL_CONFIG->get("navdb_version") or confess "$sql_config: navdb_version not defined!";

  # same version, bail out
  if (versioncmp ($old_version, $new_version) == 0) {
    log_notice "data model in database \`$DB_NAME' is already up-to-date at version $old_version";
    do_update_permissions;
    return undef;
  };

  # find upgrade scripts
  my @scripts = do_find_upgrade_scripts ($old_version, $new_version);
  log_info "found upgrade path:";
  for (@scripts) {
    log_info '  ', $_->{from}, ' -> ', $_->{to};
  };

  # apply upgrade scripts in order
  for (@scripts) {
    my $script_dir = dirname ($_->{script});
    my $script_file = basename ($_->{script});
    log_notice "upgrading data model version ", $_->{from}, ' to ', $_->{to};
    shell (chdir_cmd ($script_dir, psql_owner_script ($script_file, "--dbname=$DB_NAME") . " >/dev/null"));
    $? == 0 or die "upgrade failed\n";

    # make sure each script updates navdb_meta.dm_version to its target version
    if (versioncmp (do_get_current_dm_version, $_->{to}) != 0) {
      die "upgrade script didn't update data model version!\n";
    };
  };

  # grant permissions to any new tables
  do_update_permissions;
  
  return 1;
}

##############################################
# top-level actions
##############################################

# Create or upgrade the database
sub action_setup() {
  do_create_db_users;
  do_create_db or do_upgrade_db;
}

# Print out the installed version from the live database as well as
# the version in creation scripts.
sub action_print_dm_version() {
  if (db_exists) {
    my $old_version = do_get_current_dm_version;
    if ($old_version) {
      print "Installed: ", do_get_current_dm_version, "\n";
    };
  };
  print "Available: ", scalar ($SQL_CONFIG->get ("navdb_version")), "\n";
}

# Print out the upgrade path
sub action_print_upgrade_path() {
  if (db_exists) {
    my $old_version = do_get_current_dm_version;
    if ($old_version) {
      my $new_version = $SQL_CONFIG->get ("navdb_version");
      if (versioncmp ($old_version, $new_version) == 0) {
        log_notice "data model is already up-to-date at version $old_version";
        return undef;
      };
      my @scripts = do_find_upgrade_scripts ($old_version, $new_version);
      for (@scripts) {
        log_notice $_->{from}, ' -> ', $_->{to};
      };
      return;
    };
  };
  die "database \`$DB_NAME' does not exist\n";
}


# Print out a help message and exit
sub help() {
  print <<_END
Usage: $PROGNAME $SUBCOMMAND [OPTIONS...]
Create or upgrade NavDB database
 -h,--help                print this help and exit
 -v,--verbose             be verbose (additive)
 -c,--config=FILE         read configuration from FILE
                          (default: \`$DFLT_PROJECT_CONFIG_FILE')
 -d,--dbname=DBNAME       override DB name from config file
    --nodata              don't load initial data when creating the database
 -F,--load-file=FILE      use this SQL file with initial data
 -M,--data-model-version  print data model version and exit
 -P,--upgrade-path        print upgrade path and exit
_END
  ;
  exit 0;
}

# Process command line
sub init() {
  my ($action, $setup_found, $devel_found);
  my $set_action = sub {
    $action and warn "multiple actions specified on command line\n";
    $action = shift;
  };
  do {
    local $SIG{__WARN__} = \&cmdline_error;
    GetOptions (
      'h|help'    => \&help,
      'v|verbose' => sub { ++$LOG_LEVEL },
      'c|config=s' => \$PROJECT_CONFIG_FILE,
      'd|dbname=s' => \$DB_NAME,
        'setup'   => sub { &$set_action (\&action_setup) ; $setup_found = 1 ; },
        'nodata'  => sub { $LOAD_AERO_DATA = undef },
      'F|load-file=s' => \$AERO_DATA_FILE,
      'S|devel'   => \$devel_found,
      'M|data-model-version' => sub { &$set_action (\&action_print_dm_version) },
      'P|upgrade-path' => sub { &$set_action (\&action_print_upgrade_path) },
    );
    $#ARGV >= 0 and warn "too many arguments\n";
    $action or $action = \&action_setup;
  };
  $devel_found and log_warning "ignoring deprecated \`--devel' option\n";
  $setup_found and log_warning "ignoring deprecated \`--setup' option\n";
  return $action;
}

# Convert signals to exceptions
sub handle_signal() {
  my $signame = shift;
  $SIG{TERM} = $SIG{INT} = $SIG{QUIT} = $SIG{HUP} = $SIG{PIPE} = 'IGNORE';
  die "terminated by SIG{$signame}\n";
};

# main
sub main() {
  eval {
    my $action = init;
    project_init;
    $SIG{TERM} = $SIG{INT} = $SIG{QUIT} = $SIG{HUP} = $SIG{PIPE} = \&handle_signal;
    &$action;
    1;
  } or do {
    my $err = $@;
    wait;
    for (@CLEANUP_ON_ERROR) {
      eval {
        &{$_};
      };
      $@ and log_error $@;
    };
    log_error $err;
    exit 1;
  }
};

1;

