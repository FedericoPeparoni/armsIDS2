#!/bin/sh

set -x

[ -f Makefile ] && make distclean
rm -rf rpm/WORK rpm/RPMS
rm -rf aclocal.m4 autom4te.cache build-aux
rm -f configure configure.ac
rm -f sql/data/aero-data.sql sql/data/*aero-data*.sql*
find -type f \( -name Makefile -o -name "Makefile.in" \) -exec rm -f '{}' '+'
rm -f navdb-[0-9]*.tar.gz

