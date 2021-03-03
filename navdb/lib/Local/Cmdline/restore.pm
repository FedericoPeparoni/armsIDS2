# vim:ts=2:sts=2:sw=2:et
package Local::Cmdline::restore;

use warnings;
use strict;
use English;
use IO::File;
use Getopt::Long qw(:config gnu_getopt no_auto_abbrev no_ignore_case);
use Local::ShellQuote qw(:all);
use Local::Utils qw(:all);
use Local::Postgres qw(:all);
use Local::Project qw(:all);

my $DUMP_FILE;
my $NO_VACUUM;
my $FORCE;

# Create database user accounts ("create_users.sql")
sub do_create_db_users() {
  my $script = "create_users.sql";
  log_debug "executing $script in $SQL_DIR";
  shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-f", native_path ($script)) . " >/dev/null"));
  $? == 0 or die "failed to create database users\n";
}

# Grant permissions on DB objects to DB user accounts ("update_permissions.sql")
sub do_update_permissions() {
  shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-v", "navdb_name=$DB_NAME", "-f", native_path ("update_permissions.sql")) . " >/dev/null"));
  $? == 0 or die "failed to update database permissions\n";
}

# Find current data model version
sub get_dm_version() {
  my $dm_version = "UNKNOWN";
  my $sql = "select dm_version from navdb_admin limit 1";
  my $cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-c", $sql));
  log_shell_cmd $cmd;
  my $output = `$cmd`;
  if ($? == 0) {
    $output =~ s/^\s+//gso;
    $output =~ s/\s+$//gso;
    length $output and $dm_version = $output;
  }
  return $dm_version;
}

# Destroy the database
sub do_destroy_db() {
  log_notice "terminating existing client connections";
  kill_db_sessions;
  log_notice "destroying database \`$DB_NAME'";
  shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-c", "drop database $DB_NAME;")) . " >/dev/null");
  $? == 0 or die "failed to drop database\n";
}

# Make sure the given dump file contains navdb:
#   call "pg_restore -l" to list the file contents
#   look for well-known objects:
#     schema "navdb"
#     table "navdb_admin"
sub do_check_dump() {
  local $_;
  stat $DUMP_FILE or die "$DUMP_FILE: $!\n";
  -f _ or die "$DUMP_FILE: not a regular file\n";
  my $cmd = chdir_cmd ($SQL_DIR, postgres_cmd_pg_restore ("-l")) . " <" . shell_quote ($DUMP_FILE);
  log_shell_cmd $cmd;
  my $fh = IO::File->new ("$cmd|") or die "failed to read PostgreSQL data file\n";
  my ($schema_navdb_ok, $navdb_admin_ok);
  while (defined ($_ = $fh->getline)) {
    /SCHEMA.* navdb /o and $schema_navdb_ok = 1;
    /TABLE.* navdb_admin /o and $navdb_admin_ok = 1;
  };
  close $fh;
  $? == 0 or die "failed to read PostgreSQL data file\n";
  die "$DUMP_FILE: this file doesn't seem to contain a navdb database\n"
    unless $schema_navdb_ok and $navdb_admin_ok;
}

# Restore the database
sub do_restore_db() {

  # make sure this is a navdb dump
  do_check_dump;

  # create users
  do_create_db_users;

  # drop existing database
  if (db_exists) {
    unless ($FORCE or confirm "Are you sure you want to destroy database \`$DB_NAME' and loose all data (yes/no)? ") {
      log_error "exiting upon user request";
      exit 1;
    };
    do_destroy_db;
  };

  # convert signals to exceptions
  my $cleanup = sub {
    my $signame = shift;
    die "terminaled by SIG${signame}\n";
  };
  local $SIG{INT} = $cleanup;
  local $SIG{TERM} = $cleanup;
  local $SIG{QUIT} = $cleanup;
  local $SIG{HUP} = $cleanup;
  local $SIG{PIPE} = $cleanup;

  eval {
    # create the db
    log_debug "executing create_db.sql in $SQL_DIR";
    shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("-f", native_path ("create_db.sql"), "-v", "navdb_locale=" . navdb_locale(), "-v", "navdb_name=$DB_NAME") . " >/dev/null"));
    $? == 0 or die "failed to create database \`$DB_NAME'\n";

    # restore the database
    do {
      log_notice "restoring database \`$DB_NAME' from $DUMP_FILE";
      my @verbose = $LOG_LEVEL > 4 ? ("-v") : ();
      my $pgrestore_cmd = postgres_cmd_pg_restore ("--exit-on-error", "--dbname", $DB_NAME, @verbose);
      my $cd_cmd = "cd /";
      my $cmd = "( $cd_cmd && $pgrestore_cmd ; ) <" . shell_quote ($DUMP_FILE);
      shell $cmd;
      $? == 0 or die "failed to restore database\n";
    };

    # vacuum
    unless ($NO_VACUUM) {
      my $verbose = $LOG_LEVEL > 4 ? "verbose" : "";
      log_notice "optimizing database";
      shell (chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-c", "vacuum $verbose analyze")));
      $? == 0 or die "vacuum failed\n";
    };

    # Set permissions
    do_update_permissions;

    # print out the restored DM version
    my $dm_version = get_dm_version;
    log_notice ("restored $DB_NAME, data model version $dm_version");
    log_notice ("run \`navdb setup' to upgrade to latest data model");
    
    1;
    
  } or do {
    # cleanup on error
    my $err = $@;
    wait;
    do_destroy_db;
    die $@;
  };
  return 1;
}

# Print out a help message and exit
sub help() {
  print <<_END
Usage: $PROGNAME $SUBCOMMAND [OPTIONS...] FILE
Restore NAVDB database
 -h,--help           print this help and exit
 -v,--verbose        be verbose (additive)
 -c,--config=FILE    read configuration from FILE
                     (default: \`$DFLT_PROJECT_CONFIG_FILE')
 -d,--dbname=DBNAME  override DB name from config file
    --no-vacuum      don't vacuum after restoring
 -f,--force          don't prompt before destroying database
_END
  ;
  exit 0;
}


# Process command line
sub init() {
  local $SIG{__WARN__} = \&cmdline_error;
  GetOptions (
    'h|help'    => \&help,
    'v|verbose' => sub { ++$LOG_LEVEL },
    'c|config=s' => \$PROJECT_CONFIG_FILE,
    'd|dbname=s' => \$DB_NAME,
      'no-vacuum' => \$NO_VACUUM,
    'f|force' => \$FORCE,
  );
  $#ARGV < 0 and warn "not enough arguments\n";
  $#ARGV > 0 and warn "too many arguments\n";
  $DUMP_FILE = shift (@ARGV);
}

# main
sub main() {
  eval {
    init;
    project_init ("pg_restore");
    do_restore_db;
    1;
  } or do {
    log_error $@;
    exit 1;
  }
};

1;

