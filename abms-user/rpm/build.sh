#!/bin/bash

RPM_NAME="abms-user"
RPM_EPOCH="0"
#RPM_VERSION="1.0.0"
RPM_SPEC_VERSION="1"

RPM_RELEASE_SUFFIX=

RPM_DIR=$(dirname $0)
TOP_DIR=$RPM_DIR/..
: ${BUILD_NUMBER:="$1"}
: ${BUILD_NUMBER:="$CI_BUILD_ID"}
: ${BUILD_NUMBER:=0}

set -ex

set -ex

# include & validate package configuration file
. $TOP_DIR/package.conf
[ -n "$PACKAGE_VERSION" ] || { echo "Unable to determine package version" >&2 ; exit 1 ; }

# Set RPM version
RPM_VERSION="$PACKAGE_VERSION"


GIT_HASH=$(git rev-parse --short HEAD || :)
[ -n "$GIT_HASH" ] && GIT_SUFFIX=".git_${GIT_HASH}" || GIT_SUFFIX=

BUILD_SUFFIX=".bld_${BUILD_NUMBER}"
RPM_RELEASE_SUFFIX="${BUILD_SUFFIX}${GIT_SUFFIX}${PACKAGE_QUALIFIER}%{?dist}"
DIST="$(rpm --eval '%dist' | sed 's,^\(..*\)\.centos$,\1,g')"

rm -rf $RPM_DIR/{WORK,RPMS}
mkdir -p $RPM_DIR/WORK/{SOURCES,SPECS,RPMS,SRPMS,tmp}
sed <abms-user.spec.in >$RPM_DIR/WORK/SPECS/$RPM_NAME.spec \
	-e "s,@@RPM_NAME@@,$RPM_NAME,g" \
	-e "s,@@RPM_EPOCH@@,$RPM_EPOCH,g" \
	-e "s,@@RPM_VERSION@@,$RPM_VERSION,g" \
	-e "s,@@RPM_SPEC_VERSION@@,$RPM_SPEC_VERSION,g" \
	-e "s,@@RPM_RELEASE_SUFFIX@@,$RPM_RELEASE_SUFFIX,g"

rpmbuild -bb \
	--define "_topdir $(readlink -f $RPM_DIR/WORK)" \
	--define "dist $DIST" \
	"$RPM_DIR/WORK/SPECS/$RPM_NAME.spec"

mkdir -p $RPM_DIR/RPMS
find $RPM_DIR/WORK/RPMS -type f -name '*.rpm' -exec cp -al '{}' $RPM_DIR/RPMS/ ';'

set +x
echo
find $RPM_DIR/RPMS -maxdepth 1 -type f -name '*.rpm' -exec echo -e '\t{}' ';'
echo

