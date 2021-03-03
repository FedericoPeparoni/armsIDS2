#!/usr/bin/perl

#
# Top-level "navdb" command. Most of the code is in modules under "lib/"
#

use warnings;
use strict;

BEGIN {
	use File::Basename;
	use File::Spec::Functions;
	our $perlmoddir = catfile (dirname ($0), "lib"); ###RELOC### 	our $perlmoddir = "@perlmoddir@";
	unshift @INC,$perlmoddir;
	require Local::Utils;
	Local::Utils->import (qw(:all));

	$Local::Utils::SQL_DIR = catfile (dirname ($0), "sql");        ###RELOC### 	$Local::Utils::SQL_DIR = '@sqldir@';
	$Local::Utils::CONF_DIR = catfile (dirname ($0), "conf");      ###RELOC### 	$Local::Utils::CONF_DIR = '@confdir@';
	$Local::Utils::INSTALLED = undef;                              ###RELOC###	$Local::Utils::INSTALLED = '@INSTALLED@';
	our $PACKAGE_VERSION = "devel";                                ###RELOC###	our $PACKAGE_VERSION = '@PACKAGE_VERSION@';

};

use File::Basename;
use Getopt::Long qw(:config bundling require_order no_auto_abbrev no_ignore_case);
use Local::Utils qw(:all);
use Local::Project qw(:all);
use Local::ShellQuote qw(:all);
our $perlmoddir;
our $PACKAGE_VERSION;

$DFLT_PROJECT_CONFIG_FILE = catfile ($CONF_DIR, "navdb.conf");

my $WANT_HELP;

sub missing_command_error() {
	cmdline_error "you must specify a command\n";
}

sub print_version() {
	unless ($INSTALLED) {
		my $package_conf = catfile ($Local::Utils::CONF_DIR, "..", "package.conf");
		my $cmd = ". " . shell_quote ($package_conf) . " >/dev/null 2>&1 ; echo \$PACKAGE_VERSION";
		log_shell_cmd ($cmd);
		my $version = trim (`$cmd`);
		if ($version) {
			print $version, "\n";
			exit 0;
		}
	};
	print $PACKAGE_VERSION, "\n";
	exit 0;
};

sub print_help() {
	my $progname = basename ($0);
	print <<_END
Usage: $progname COMMAND... [ARGS...]
IDS Navigation database control tool
 -h,--help           print this help and exit
    --version        print out package version information and exit
 -v,--verbose        be verbose (additive)
 -d,--dbname=DBNAME  override DB name from config file
 -c,--config=FILE    read configuration from FILE
                     (default: \`$DFLT_PROJECT_CONFIG_FILE')

The available commands are:
    setup            create or upgrade the database
    drop             destroy database and all data
    dump             backup database to a file
    restore          restore database from a file
    export           export feature data as SQL
    import           import feature data from SQL

Use \`$progname --help COMMAND' to read about a specific subcommand.

_END
	;
	exit 0;
}

$SIG{__WARN__} = \&cmdline_error;
GetOptions (
	'h|help' => \$WANT_HELP,
	  'version' => \&print_version,
	'v|verbose' => sub { ++$LOG_LEVEL },
	'd|dbname=s' => \$DB_NAME,
	'c|config=s' => \$PROJECT_CONFIG_FILE,
);
Getopt::Long->import(qw(:config no_pass_through no_require_order gnu_getopt no_auto_abbrev no_ignore_case));
$SIG{__WARN__} = 'DEFAULT';

if (@ARGV) {
	my $word = shift (@ARGV);
	length $word or missing_command_error;
	$WANT_HELP and unshift @ARGV, "--help";
	my $file = "$perlmoddir/Local/Cmdline/$word.pm";
	-f $file or cmdline_error "unknown command \`$word'";
	my $mod = "Local::Cmdline::$word";
	$SUBCOMMAND = $word;
	require $file;
	$mod->import();
	$mod->main();
	exit 0;
};

$WANT_HELP and print_help;
missing_command_error;
exit 0;

