#!/bin/bash

# Build RPMs and the source TAR file in the "STAGE/" directory

[[ -n $CI ]] || { echo "This script must be run by a GitLab CI runner!" >&2 ; exit 1 ; }
CHECK_VER=$1

set -ex

rhel="$(rpm --eval '%{?rhel}')"

ver=$(. ../package.conf && echo $PACKAGE_VERSION)
if [[ -z $ver ]] ; then
	echo "Unable to determine package version!" >&2
	exit 1
fi
if [[ -n $CHECK_VER ]] ; then
	if [[ $CHECK_VER != $ver ]] ; then
		echo "Package version must equal $CHECK_VER!" >&2
		exit 1
	fi
fi

# Build RPMs
rm -f ../*.tar.*
( cd ../rpm && ./build.sh ; )

# Clean "STAGE/" directory
rm -rf STAGE/

# Move tar file to STAGE/
mkdir -p STAGE/tar/$ver
mv ../*.tar.* STAGE/tar/$ver/
( cd STAGE/tar/$ver && for f in *.tar.* ; do sha256sum $f >$f.sha256 ; done ; )
# move rpm files to STAGE/
mkdir -p STAGE/rpms/el${rhel}/$ver
find ../rpm/RPMS -name '*.rpm' -exec mv '{}' STAGE/rpms/el${rhel}/$ver ';'

# print out the result
set +x
echo
find STAGE -type f -exec ls -hl '{}' '+' | awk '{printf ("\t%-10s %s\n", $5, $9)}'
echo

