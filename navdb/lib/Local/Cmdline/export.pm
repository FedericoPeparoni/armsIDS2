# vim:ts=2:sts=2:sw=2:et
package Local::Cmdline::export;

use warnings;
use strict;
use English;
use Getopt::Long qw(:config gnu_getopt no_auto_abbrev no_ignore_case);
use Local::Utils qw(:all);
use Local::Project qw(:all);
use Local::Postgres qw(:all);

my $OUTPUT_FILE;
my $SCHEMA = "navdb";

# export only data from navdb or navdb_staging schemas as an SQL
# script suitable for using as the initial load script, or as input
# to "import"
sub do_export() {
  local $_;
  my @pgdump_args = ("--data-only", "--schema=$SCHEMA");
  $LOG_LEVEL > 4 and push @pgdump_args, "-v";
  # pg_dump < 9.5 doesn't support the "--dbname" option, expects
  # database name as a non-option argument
  push @pgdump_args, $DB_NAME;
  my $pgdump_cmd = postgres_cmd_pg_dump (@pgdump_args);

  my $filename = (defined ($OUTPUT_FILE) and length ($OUTPUT_FILE) and $OUTPUT_FILE ne '-') ? $OUTPUT_FILE : "-";
  log_notice "exporting feature data from database \`$DB_NAME' schema \`$SCHEMA' to $filename";
  my $cd_cmd = "cd /tmp";
  my $cmd = "$cd_cmd && $pgdump_cmd";
  my $out = \*STDOUT;
  my $close_out;
  if (defined ($OUTPUT_FILE) and length ($OUTPUT_FILE) and $OUTPUT_FILE ne '-') {
    $out = IO::File->new (">$OUTPUT_FILE") or die "$OUTPUT_FILE $!\n";
    $close_out = 1;
  };
  log_shell_cmd ($cmd);
  my $in = IO::File->new ("$cmd|") or die "failed to execute pg_dump\n";
  my ($seen_client_min_messages, $seen_search_path, $seen_row_security, $seen_lock_timeout);
  while (defined ($_ = $in->getline)) {
    # these options are not supported in postgres 9.2, comment them out
    unless ($seen_client_min_messages) {
      if (s/^(\s*SET\s+client_min_messages\s*=)/--$1/i) {
        $seen_client_min_messages = 1;
        $out->print ($_);
        next;
      };
    };
    unless ($seen_search_path) {
      if (s/^(\s*SET\s+search_path\s*=)/--$1/i) {
        $seen_search_path = 1;
        $out->print ($_);
        next;
      };
    };
    unless ($seen_row_security) {
      if (s/^(\s*SET\s+row_security\s*=)/--$1/i) {
        $seen_row_security = 1;
        $out->print ($_);
        next;
      };
    };
    unless ($seen_lock_timeout) {
      if (s/^(\s*SET\s+lock_timeout\s*=)/--$1/i) {
        $seen_lock_timeout = 1;
        $out->print ($_);
        next;
      };
    };
    $out->print ($_);
  };
  $in->close;
  $? == 0 or die "pg_dump failed\n";
  if ($close_out) {
    $out->close or die "$OUTPUT_FILE: $!\n";
  };
}

# Print out a help message and exit
sub help() {
  print <<_END
Usage: $PROGNAME export [OPTIONS...] {-|FILE}
Export feature data as SQL
 -h,--help                print this help and exit
 -v,--verbose             be verbose (additive)
 -c,--config=FILE         read configuration from FILE
                          (default: \`$DFLT_PROJECT_CONFIG_FILE')
 -d,--dbname=DBNAME       override DB name from config file
    --world               export from "navdb" schema (default)
    --staging             export from "navdb_staging" schema
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
  );
  @ARGV or warn "not enough arguments\n";
  $OUTPUT_FILE = shift (@ARGV);
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
    project_init ("pg_dump");
    do_export;
    1;
  } or do {
    my $err = $@;
    wait;
    log_error $err;
    exit 1;
  };
};

1;

