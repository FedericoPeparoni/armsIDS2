# vim:ts=4:sts=4:sw=4:et

use warnings;
use strict;
use Test::More tests => 6;

use_ok "Local::Cmdline::drop";
use_ok "Local::Cmdline::dump";
use_ok "Local::Cmdline::export";
use_ok "Local::Cmdline::import";
use_ok "Local::Cmdline::restore";
use_ok "Local::Cmdline::setup";

1;

