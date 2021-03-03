# vim:ts=4:sts=4:sw=4:et
package Local::Versions;

#
# USAGE:
#
#   $result = versioncmp ("1.3.45", "2.0"); # returns -1
#
# This is a replacement for module Sort::Versions
#

use warnings;
use strict;
use Carp;

# Split a version string into chunks
sub _split_ver($) {
  my $s = shift;
  my @parts;
  while ($s =~ /([0-9]+|[^0-9]+)/go) {
    push @parts, $1
  };
  return @parts;
}

# Compare two version strings
sub versioncmp($$) {
  my ($s1, $s2) = @_;
  defined $s1 or confess "s1 is undef";
  defined $s2 or confess "s2 is undef";
  my @parts1 = _split_ver ($s1);
  my @parts2 = _split_ver ($s2);
  my $max_i = $#parts1 > $#parts2 ? $#parts1 : $#parts2;
  use bigint;
  for (my $i = 0; $i <= $max_i; ++$i) {
    $i > $#parts1 and return -1;
    $i > $#parts2 and return 1;
    my $p1 = $parts1[$i];
    my $p2 = $parts2[$i];
    if ($p1 =~ /^\d+$/ and $p2 =~ /^\d+$/) {
      $p1 < $p2 and return -1;
      $p1 > $p2 and return 1;
      next
    };
    my $res = $p1 cmp $p2;
    $res < 0 and return -1;
    $res > 0 and return 1;
  };
  return 0;
}

BEGIN {
  use Exporter qw();
  our @ISA = qw(Exporter);
  our @EXPORT_OK = qw(versioncmp);
  our %EXPORT_TAGS = (all => [@EXPORT_OK]);
};

1;

