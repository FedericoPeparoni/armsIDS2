#!/bin/sh

set -e

. ./package.conf

AERO_DATA_FILE="`basename "$AERO_DATA_URL" .bz2`"

rm -f configure.ac
sed <"configure.ac.in" >configure.ac \
	-e "s,@@PACKAGE_NAME@@,$PACKAGE_NAME,g" \
	-e "s,@@PACKAGE_VERSION@@,$PACKAGE_VERSION,g" \
	-e "s,@@AERO_DATA_FILE@@,$AERO_DATA_FILE,g"

autoreconf -ivf
chmod a-w configure.ac

( cd sql/data && ./download.sh ; )

