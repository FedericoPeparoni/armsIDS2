# vim:ts=2:sts=2:sw=2:et
package Local::Cmdline::drop;

use warnings;
use strict;
use Getopt::Long qw(:config gnu_getopt no_auto_abbrev no_ignore_case);
use Local::Utils qw(:all);
use Local::Project qw(:all);

my $FORCE;

# Destroy the database
sub drop_db() {
  db_exists or do {
    log_notice "database \`$DB_NAME' does not exist";
    return;
  };
  if (not $FORCE) {
    interactive or die "Database exists; use \`--force' to disable this check\n";
    if (not confirm ("Are you sure you want to destroy database \`$DB_NAME' and loose all data (yes/no)? ")) {
      log_error "exiting upon user request";
      exit 1;
    };
  };
  log_notice "terminating existing client connections";
  kill_db_sessions;
  log_notice "destroying database \`$DB_NAME'";
  my $cmd = chdir_cmd ("/", psql_superuser_cmd ("-c", "drop database $DB_NAME;")) . " >/dev/null";
  shell ($cmd);
  $? == 0 or die "failed to drop database\n";
}

# Print out a help message and exit
sub help() {
  print <<_END
Usage: $PROGNAME $SUBCOMMAND [OPTIONS...]
Destroy NavDB database
 -h,--help           print this help and exit
 -v,--verbose        be verbose (additive)
 -d,--dbname=DBNAME  override DB name from config file
 -c,--config=FILE    read configuration from FILE
                     (default: \`$DFLT_PROJECT_CONFIG_FILE')
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
    'f|force' => \$FORCE,
  );
  $#ARGV >= 0 and warn "too many arguments\n";
}

# main
sub main() {
  eval {
    init;
    project_init;
    drop_db;
    1;
  } or do {
    log_error $@;
    exit 1;
  };
  return 1;
};

1;

