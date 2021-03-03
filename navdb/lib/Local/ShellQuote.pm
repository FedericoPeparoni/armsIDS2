# vim:set ts=2 sts=2 sw=2 et:

#
# Quote strings for passing through shell
#
# This is basically a replacement for a 3rd-party module String::ShellQuote
# to avoid depending on it.
#


package Local::ShellQuote;
use warnings;
use strict;

sub shell_quote (@) {
  my @list;
  for my $s0 (@_) {
    my $s = $s0;
    if (defined ($s)) {
      # Does the string contain any shell special characters?
      if ($s =~ /['"`\$ \t\r\n~!#%^&*;<>?|\\{}()\[\]]/o) {
        # Does it contain a single quote? If yes, we will quote it with double quotes
        if ($s =~ /[']/o) {
          $s =~ s/(["`\$\\])/\\$1/go;
          $s = '"' . $s . '"';
        }
        # Otherwise use single quotes
        else {
          $s = "'" . $s . "'";
        }
      }
    }
    else {
      $s = "''";
    }
    push @list, $s;
  };
  return join (" ", @list);
}

BEGIN {
      use Exporter qw();
      our @ISA = qw(Exporter);
      our @EXPORT_OK = qw(shell_quote);
      our %EXPORT_TAGS = (all => [@EXPORT_OK]);
}

1;

