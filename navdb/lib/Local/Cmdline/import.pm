# vim:ts=2:sts=2:sw=2:et
package Local::Cmdline::import;

use warnings;
use strict;
use English;
use IO::File;
use Cwd qw(abs_path);
use File::Temp qw(tempfile);
use Getopt::Long qw(:config gnu_getopt no_auto_abbrev no_ignore_case);
use Local::Utils qw(:all);
use Local::Project qw(:all);
use Local::Postgres qw(:all);

my $SCHEMA = "navdb";
my $INPUT_FILE;
my $FORCE;

# returns the list of all table names in the given schema
sub do_find_tables_in_schema($) {
  local $_;
  my $schema = shift;
  my $cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "-c", "select table_name from information_schema.tables where table_schema = '$schema' and table_type <> 'VIEW'"));
  log_shell_cmd $cmd;
  my $res = `$cmd`;
  $? == 0 or die "failed to determine the names of existing tables in database \`$DB_NAME' schema \`$schema'\n";
  my @list = split (/\s+/, $res);
  @list or die "failed to determine the names of existing tables in database \`$DB_NAME' schema \`$schema'\n";
  @list = grep { length } @list;
  return @list;
};

# import feature data only
sub do_import() {
  local $_;
  my @psql_args;

  # validate input file name
  my ($filename, $use_stdin);
  if (defined $INPUT_FILE and length $INPUT_FILE and $INPUT_FILE ne '-') {
    $filename = $INPUT_FILE;
    stat $filename or die "$filename: $!\n";
    if ( not -f _ and not -p _) {
      die "$filename: not a regular file\n";
    };
  }
  else {
    $filename = "-";
    $use_stdin = 1;
  };

  # Make sure database exists
  if (db_exists) {
    if (not $FORCE) {
      interactive or die "Database \`$DB_NAME' not empty; use \`--force' to suppress this check\n";
      if (not confirm "Are you sure you want to completely replace feature data in database \`$DB_NAME' schema \`$SCHEMA' (yes/no)? ") {
        log_error "exiting upon user request";
        exit 1;
      };
    };
  }
  else {
    log_error "database \`$DB_NAME' doesn't exist";
    exit 1;
  };

  # find the names of all tables in the given schema
  my @tables = do_find_tables_in_schema ($SCHEMA);
  my $sql_trunc = "TRUNCATE " . join (", ", @tables) . " CASCADE";
  my $script_out = "";
  if ($LOG_LEVEL <= 4) {
    $script_out = " >/dev/null";
    @psql_args = "--quiet";
  }
  else {
    @psql_args = "--echo-queries";
  };

  # Script for PSQL:
  #   begin transaction
  #   set up session options
  #   truncate all tables
  #   include load file
  #   commit
  my $psql_script = <<_END
BEGIN;
SET CONSTRAINTS ALL DEFERRED;
SET search_path = $SCHEMA,public;
SET client_min_messages = warning;
SET role $DB_OWNER;
$sql_trunc;
\\i -
COMMIT;
_END
  ;

  # Save this script in a temporary file
  my ($psql_script_file, $psql_script_fh);
  my $cleanup = sub {
    $psql_script_file and unlink $psql_script_file;
  };
  my $sig_handler = sub {
    die "terminated\n";
  };
  $SIG{INT} = $SIG{TERM} = $SIG{PIPE} = $SIG{HUP} = $sig_handler;
  ($psql_script_fh, $psql_script_file) = tempfile ("navdb_import_XXXXXX", TMPDIR => 1, SUFFIX => ".sql");
  eval {
    log_debug "saving psql script in $psql_script_file";
    chmod 0644, $psql_script_file or die "$psql_script_file: $!\n";
    print $psql_script_fh $psql_script;
    $psql_script_fh->close;

    # execute script
    my $psql_cmd = chdir_cmd ($SQL_DIR, psql_superuser_cmd ("--dbname=$DB_NAME", "--no-readline", @psql_args, "--file=" . native_path ($psql_script_file))) . $script_out;
    log_shell_cmd $psql_cmd;
    
    log_notice "importing feature data to database \`$DB_NAME' schema \`$SCHEMA' from $filename\n";
    my $input_fh;
    if ($use_stdin) {
      $input_fh = \*STDIN;
    }
    else {
      $input_fh = IO::File->new ($filename) or die "$filename: $!\n";
    };
    my $psql_pipe = IO::File->new ("|$psql_cmd") or die "failed to execute psql\n";
    while (defined ($_ = $input_fh->getline)) {
        $psql_pipe->print ($_);
    };
    $psql_pipe->close;
    $? == 0 or die "failed to load feature data\n";
    unless ($use_stdin) {
      $input_fh->close or die "close(): $!\n";
    };
  } or do {
    my $err = $@;
    &$cleanup;
    wait;
    die $@;
  };
  
}

# Print out a help message and exit
sub help() {
  print <<_END
Usage: $PROGNAME import [OPTIONS...] [FILE]
Import feature data from an SQL file
 -h,--help           print this help and exit
 -v,--verbose        be verbose (additive)
 -c,--config=FILE    read configuration from FILE
                     (default: \`$DFLT_PROJECT_CONFIG_FILE')
 -d,--dbname=DBNAME  override DB name from config file
    --world          import to "navdb" schema (default)
    --staging        import to "navdb_staging" schema
 -f,--force          don't prompt before replacing feature data
_END
  ;
  exit 0;
}


# Process command line
sub init() {
  local $SIG{__WARN__} = \&cmdline_error;
  my ($want_world, $want_staging);
  GetOptions (
    'h|help'    => \&help,
    'v|verbose' => sub { ++$LOG_LEVEL },
    'c|config=s' => \$PROJECT_CONFIG_FILE,
    'd|dbname=s' => \$DB_NAME,
      'world' => \$want_world,
      'staging' => \$want_staging,
    'f|force' => \$FORCE,
  );
  @ARGV or warn "not enouth arguments\n";
  $INPUT_FILE = shift (@ARGV);
  @ARGV and warn "too many arguments\n";
  if ($want_world and $want_staging) {
    warn "--world and --staging cannot be used together\n";
  };
  $want_world and $SCHEMA = "navdb";
  $want_staging and $SCHEMA = "navdb_staging";
}

# main
sub main() {
  eval {
    init;
    project_init;
    do_import;
    1;
  } or do {
    my $err = $@;
    wait;
    log_error $err;
    exit 1;
  };
};

1;

