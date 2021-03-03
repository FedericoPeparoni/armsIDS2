# vim:ts=4:sts=4:sw=4:et

use warnings;
use strict;
use Test::More tests => 8;

use Local::Versions qw(:all);

is (versioncmp ("1.0.0", "1.0.0"), 0);

is (versioncmp ("1.0.0", "1.0.00"), 0);

is (versioncmp ("01.0.0", "1.0.00"), 0);

is (versioncmp ("1.0.0", "1.0.1"), -1);
is (versioncmp ("1.0.1", "1.0.0"), 1);
is (versioncmp ("1.0.0", "1.0"), 1);
is (versioncmp ("1.0.bld3", "1.0.bld2"), 1);
is (versioncmp ("1.0.bld", "1.0.bldX"), -1);

