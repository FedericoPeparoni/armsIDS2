# vim:ts=4:sts=4:sw=4:et

use warnings;
use strict;
use Test::More tests => 4;

use Local::ShortestPath;

my @path;

@path = shortest_path ("a", "b",
    [
        ["a", "x"],
        ["b", "y" ]
    ]);
is_deeply([@path],[]);

@path = shortest_path ("a", "b",
    [
        ["a", "x"],
        ["x", "b" ]
    ]);
is_deeply([@path],[qw(a x b)]);

@path = shortest_path ("a", "b",
    [
        ["a", "x1"],
        ["x1", "x2" ],
        ["x2", "x3" ],
        ["x3", "b" ],
    ]);
is_deeply([@path],[qw(a x1 x2 x3 b)]);


@path = shortest_path ("a", "b",
    [
        ["a", "x1"],
        ["x1", "x2" ],
        ["x2", "x3" ],
        ["x3", "b" ],
        ["x2", "b" ],
        ["x1", "b" ],
    ]);
is_deeply([@path],[qw(a x1 b)]);

1;

