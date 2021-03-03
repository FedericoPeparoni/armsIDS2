# vim:ts=4:sts=4:sw=4:et

#
# SYNOPSIS
# 
#   use Local::ShortestPath;
#   @list = shortest_path ($FROM, $TO, $DIRECTED_GRAPH);
# 
# Returns the shortest path between the specified from and to nodes
# for a simple unweighted directed graph.
#
# The graph is a listref of edges. Each edge is a listref of 2
# node names that represent the "from" and the "two" nodes of the edge.
#
# The function returns the list of node names that lead from $FROM to $TO,
# forming the (shortest) path.
#
# This is used to determine script upgrade script execution order.
# 
# For example:
#
#   @path = shortest_path ("1.0", "5.0", [
#     [ "1.0", "1.1" ],
#     [ "1.1", "1.2" ],
#     [ "1.2", "1.3" ],
#     [ "1.3", "1.4" ],
#     [ "1.3", "1.5" ]
#   ]);
#
#   Returns ("1.0", "1.1", "1.2", "1.3", "1.5")
#
#

###############################################################
# Local::ShortestPath::Graph::Directed
###############################################################

# The API of this class is a subset of the Graph::Directed perl module
package Local::ShortestPath::Graph::Directed;

use warnings;
use strict;
use Carp;

sub new() {
    my $class = shift;
    my $self = {
        nodes => {}
    };
    bless $self, $class;
};

sub add_edge($$) {
    my ($self, $from, $to) = @_;
    if (not defined $from or not defined $to) {
        confess 'Usage: $ref->add_edge($FROM, $TO)';
    };
    my $from_node = do { $self->{nodes}->{$from} or $self->{nodes}->{$from} = { id => $from, neighbours => {} } };
    my $to_node = do { $self->{nodes}->{$to} or $self->{nodes}->{$to} = { id => $to, neighbours => {} } };
    $from_node->{neighbours}->{$to} = $to_node;
};

# Adapted from here: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
sub SP_Dijkstra($$) {
    my ($self, $from, $to) = @_;
    if (not defined $from or not defined $to) {
        confess 'Usage: $ref->SP_Dijkstra($FROM, $TO)';
    };
    
    my $from_node = $self->{nodes}->{$from};
    #$from_node or croak "Node \`$from' doesn't exist";
    $from_node or return ();
    
    my $to_node = $self->{nodes}->{$to};
    #$to_node or croak "Node \`$to' doesn't exist";
    $to_node or return ();

    my $q = {};
    my $dist = {};
    my $prev = {};
    my ($u, $v);
    for $v (values (%{$self->{nodes}})) {
        $dist->{$v->{id}} = 1000000;
        $prev->{$v->{id}} = undef;
        $q->{$v->{id}} = $v;
    };

    $dist->{$from} = 0;
    while (keys(%$q)) {
        my $u_id = do {
            my @keys = keys (%$q);
            my $min_key = pop (@keys);
            for my $key (@keys) {
                $min_key = $key if ($dist->{$key} < $dist->{$min_key});
            };
            $min_key;
        };
        #my $u_id = reduce { $dist->{$a} < $dist->{$b} ? $a : $b } keys (%$q);
        $u = $self->{nodes}->{$u_id};
        delete $q->{$u_id};
        $u eq $to_node and last;
        for $v (values (%{$u->{neighbours}})) {
            my $v_id = $v->{id};
            my $alt = $dist->{$u_id} + 1;
            if ($alt < $dist->{$v_id}) {
                $dist->{$v_id} = $alt;
                $prev->{$v_id} = $u;
            };
        };
    };

    my @S;
    $u = $to_node;
    while (defined $prev->{$u->{id}}) {
        push @S, $u->{id};
        $u = $prev->{$u->{id}};
    };
    push @S, $u->{id};

    $u->{id} eq $from or return ();

    my @path = reverse (@S);
    return @path;

};

1;

###############################################################
# Local::ShortestPath
###############################################################
package Local::ShortestPath;
use warnings;
use strict;
#use Graph::Directed;

sub shortest_path($$$) {
  my ($from, $to, $graph) = @_;
  my $g = Local::ShortestPath::Graph::Directed->new();
#  my $g = Graph::Directed->new();
  for my $edge (@$graph) {
    my ($e_from, $e_to) = @$edge;
    $g->add_edge ($e_from, $e_to);
  };
  my @all_nodes = keys %{$g->{nodes}};
  return $g->SP_Dijkstra ($from, $to);
};

BEGIN {
  use Exporter qw();
  our @ISA = qw(Exporter);
  our @EXPORT = qw(shortest_path);
};

1;

