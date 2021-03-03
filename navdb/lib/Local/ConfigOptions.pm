# vim: set ts=4 sts=4 sw=4 et:
package Local::ConfigOptions;

#
# A set of config (key/value) options.
#
# Usage:
#
#   $conf = Local::ConfigOptions->new;  # create
#   $conf->set ("key", "value");        # save a key/value pair
#   $conf->set ("key", "value", "loc"); # save key, value and location
#   $val = $conf->get ("key");          # retrieve value
#   ($val, $loc) = $conf->get ("key")   # retrieve value and location
#   @keys = $conf->keys;                # all keys
#   $filename = $conf->filename;        # filename that was passed to constructor
# 

use warnings;
use strict;

sub new() {
    my $class = shift;
    my $filename = shift;
    my $self = {
        _data => {},
        _filename => $filename,
    };
    bless ($self, $class);
};

sub filename {
    shift->{_filename}
};

sub keys {
    keys %{shift->{_data}}
};

sub get {
    my ($self, $key) = @_;
    if (wantarray) {
        if (exists ($self->{_data}->{$key})) {
            return (
                $self->{_data}->{$key}->{val},
                $self->{_data}->{$key}->{loc}
            )
        };
        return ();
    };
    if (exists ($self->{_data}->{$key})) {
        return $self->{_data}->{$key}->{val};
    };
    return undef;
};

sub set {
    my ($self, $key, $val, $loc) = @_;
    if (not defined $self->{_data}->{$key}) {
        $self->{_data}->{$key} = {};
    };
    $self->{_data}->{$key}->{val} = $val;
    $self->{_data}->{$key}->{loc} = $loc;
    return $val;
}

1;


