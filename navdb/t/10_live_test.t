# vim:ts=4:sts=4:sw=4:et

use warnings;
use strict;
use Test::More;
use Test::Exception;

unless (($ENV{NAVDB_LIVE_TEST} || "") eq "1") {
    plan skip_all => "Environment NAVDB_LIVE_TEST=1 is not set"
}
else {
    plan tests => 10;
};

use English;
use Carp;
use File::Spec::Functions;
use File::Basename;
use File::Temp qw(tempfile);
use Local::ShellQuote qw(:all);

$ENV{NAVDB_TTY_COLORS} = 1;

my $SELF = basename ($0);
my $NAVDB_PL = catfile (dirname($0), "..", "navdb.pl");
my $DB_NAME = "navdb_unit_test";

sub navdb(@) {
    my @cmdline = ("--dbname=$DB_NAME", "-vv", @_);
    my $cmd = shell_quote ($EXECUTABLE_NAME, $NAVDB_PL, @cmdline);
    return $cmd;
}

sub cmd($) {
    my $cmd = shift;
    system ($cmd);
    $? == 0 or croak "navdb script failed";
}

sub create_temp_file ($) {
    my $suffix = shift;
    my (undef, $filename) = tempfile ("${SELF}_XXXXXX", DIR => File::Spec->tmpdir(), SUFFIX => $suffix, UNLINK => 1);
    chmod 0666, $filename or die "chmod(): $filename $!\n";
    return $filename;
}

my $TEMP_DAT_1 = create_temp_file (".dat");
my $TEMP_DAT_2 = create_temp_file (".dat");
my $TEMP_SQL_1 = create_temp_file (".sql");
my $TEMP_SQL_2 = create_temp_file (".sql");

# create it w/o data
cmd (navdb ("drop", "--force"));
lives_ok { cmd (navdb ("setup", "--nodata")) } "setup --nodata";

# create it w/data
cmd (navdb ("drop", "--force"));
lives_ok { cmd (navdb ("setup")) }  "setup";

# dump
lives_ok { cmd (navdb ("dump", $TEMP_DAT_1)) }  "dump FILENAME";
lives_ok { cmd (navdb ("dump", "-") . " >$TEMP_DAT_2") }  "dump -";

# restore
lives_ok { cmd (navdb ("restore", "--force", $TEMP_DAT_1)) };
lives_ok { cmd (navdb ("restore", "--force", $TEMP_DAT_2)) };

# export
lives_ok { cmd (navdb ("export", $TEMP_SQL_1)) };
lives_ok { cmd (navdb ("export", "-") . " >$TEMP_SQL_2") };

# import
lives_ok { cmd (navdb ("import", "--force", $TEMP_SQL_1)) };
lives_ok { cmd (navdb ("import", "--force", "--staging", "-") . " <$TEMP_SQL_2") };

1;

