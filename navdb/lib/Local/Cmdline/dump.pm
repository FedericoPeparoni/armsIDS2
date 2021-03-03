# vim:ts=2:sts=2:sw=2:et
package Local::Cmdline::dump;

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
my $COMPRESSION_LEVEL = 5;

# dump the database
sub do_dump_db() {
  my @pgdump_args = ("-F", "custom", "-N", "public", "-N", "topology", "-Z", $COMPRESSION_LEVEL);
  $LOG_LEVEL > 4 and push @pgdump_args, "-v";
  # pg_dump < 9.5 doesn't support the "--dbname" option, expects
  # database name as a non-option argument
  push @pgdump_args, $DB_NAME;
  my $cmd = chdir_cmd ("/tmp", postgres_cmd_pg_dump (@pgdump_args));

  # interpret "-" as STDOUT
  if ($DUMP_FILE eq '-') {
    # make sure it's not a terminal
    if (-t \*STDOUT) {
      die "won't dump binary data to terminal\n";
    }
  }
  # actual filename
  else {
    # Make sure this file is not a terminal; to do that
    # we have to open it because "-t" only works on open file
    # handes. We open it for reading because we only need this handle
    # to test whether it's a terminal.
    do {
      if (my $fh = IO::File->new ($DUMP_FILE)) {
        -t $fh and die "won't dump binary data to terminal\n";
        if (-S _ or -d _ or -S $DUMP_FILE or -d $DUMP_FILE) {
          die "$DUMP_FILE: not a regular file\n";
        }
      }
      elsif (not $!{ENOENT}) {
        die "$DUMP_FILE: $!\n";
      };
    };
    $cmd = "$cmd >" . shell_quote ($DUMP_FILE);
  };

  log_notice "dumping database \`$DB_NAME' to $DUMP_FILE";
  shell $cmd;
  $? == 0 or die "failed to dump database\n";
}

# Print out a help message and exit
sub help() {
  print <<_END
Usage: $PROGNAME $SUBCOMMAND [OPTIONS...] {-|FILE}
Create a NavDB database backup
 -h,--help                print this help and exit
 -v,--verbose             be verbose (additive)
 -d,--dbname=DBNAME       override DB name from config file
 -c,--config=FILE         read configuration from FILE
                          (default: \`$DFLT_PROJECT_CONFIG_FILE')
 -Z,--compress=0..9       compression level (default: 5)
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
    'd|dbname=s' => \$DB_NAME,
    'c|config=s' => \$PROJECT_CONFIG_FILE,
    'Z|compress=i' => \$COMPRESSION_LEVEL,
  );
  $#ARGV < 0 and warn "not enough arguments\n";
  $DUMP_FILE = shift (@ARGV);
  $#ARGV >= 0 and warn "too many arguments\n";
}

# main
sub main() {
  eval {
    my $action = init;
    project_init ("pg_dump");
    do_dump_db;
    1;
  } or do {
    log_error $@;
    exit 1;
  };
};

1;

