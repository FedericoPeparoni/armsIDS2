#!/usr/bin/perl
# vim:ts=4:sts=4:sw=4:et

#
# Remove obsolete keys from a .properties file
#
# Usage:
#    remove_obsolete_props.pl OLD_FILE NEW_FILE OBSOLETE_PROPS...
#
# where:
#   OLD_FILE -- the file that had been previously installed and possibly
#               edited by a human. This is the prop file from an older version
#               of the package that is possibly missing some keys, or might
#               contain keys that are no longer used
#
#   NEW_FILE -- the new version of the file that comes from git
#
#   OBSOELETE_PROPS - properties that may be present in OLD_FILE, which are
#                     no longer used
#
# This script will then print out the contents of NEW_FILE with any
# properties that were present in OLD_FILE replaced with their old values;
# and any properties that were present in the old file, but not in the new file,
# re-added to the output -- except for OBSOLETE_PROPS.
#
# This script returns 0 on success and non-zero on any error; the
# case when OLD_FILE didn't contain any OBSOLETE_PROPS is considered an error.
#

use warnings;
use strict;

# process command line
scalar (@ARGV) >= 2 or die "Usage: $0 OLD_FILE NEW_FILE OBSOLETE_PROPS...\n";
my ($OLD_FILE, $NEW_FILE, @OBSOLETE_PROPS) = @ARGV;
my %OBSOLETE_PROPS = map { $_ => undef } @OBSOLETE_PROPS;
my $has_obsolete_props;

# read properties from OLD_FILE into a hash
my %OLD_PROPS;
open FH, $OLD_FILE or die "$OLD_FILE: $!\n";
while (<FH>) {
    /^\s*(#+\s*)?([a-zA-Z][a-zA-Z0-9.-]{3,})\s*[=:].*$/o and do {
        if (not exists $OBSOLETE_PROPS{$2}) {
            unless ($1) {
                $OLD_PROPS{$2} = $_;
            };
            next;
        };
        $has_obsolete_props = 1;
    };
};
close FH;

# If we didn't find any obsolete props, bail out
unless ($has_obsolete_props) {
    die "$OLD_FILE: no obsolete properties found\n";
};

# read NEW_FILE, printing each line, but replace any lines that were present
# in OLD_FILE
open FH, $NEW_FILE or die "$NEW_FILE: $!\n";
while (<FH>) {
    if (/^\s*(?:#+\s*)?([a-zA-Z][a-zA-Z0-9.-]{3,})\s*[=:].*$/o and exists $OLD_PROPS{$1}) {
        print $OLD_PROPS{$1};
        delete $OLD_PROPS{$1};
        next;
    };
    print;
};

# Also print any additional properties from OLD_FILE, unless they were in the ignore list
my $first_output_line = 1;
for (sort (keys (%OLD_PROPS))) {
    if (not exists $OBSOLETE_PROPS{$_}) {
        if ($first_output_line) {
            print "\n\n# Other options\n";
        };
        print $OLD_PROPS{$_};
        $first_output_line = undef;
    };
};

close FH;

