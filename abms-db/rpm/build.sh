#!/bin/bash

RPM_NAME="abms-db"

RPM_DIR=$(dirname $0)
TOP_DIR=$RPM_DIR/..
: ${BUILD_NUMBER:="$1"}
: ${BUILD_NUMBER:="$CI_BUILD_ID"}
: ${BUILD_NUMBER:=0}

set -ex

# include & validate package configuration file
. $TOP_DIR/package.conf
[ -n "$PACKAGE_VERSION" ] || { echo "Unable to determine package version" >&2 ; exit 1 ; }

# Set RPM version
RPM_VERSION="$PACKAGE_VERSION"

# build suffix
BUILD_SUFFIX=".bld_${BUILD_NUMBER}"

# git suffix
GIT_HASH=$([ -d "$TOP_DIR/.git" ] && git rev-parse --short HEAD || :)
[ -n "$GIT_HASH" ] && GIT_SUFFIX=".git_${GIT_HASH}" || GIT_SUFFIX=""

# suffix to be added to rpm "release" field -- includes build number and git hash
RPM_RELEASE_SUFFIX="${BUILD_SUFFIX}${GIT_SUFFIX}${PACKAGE_QUALIFIER}%{?dist}"

# remove ".centos" from rpm macro "dist" 
rpm_dist="$(rpm --eval '%dist' | sed 's,\.centos$,,g')"

rm -rf $RPM_DIR/{WORK,RPMS}
mkdir -p $RPM_DIR/WORK/{SOURCES,SPECS,RPMS,SRPMS,tmp}

# create tar file
mkdir -p "$RPM_DIR/WORK/tmp/$RPM_NAME-$RPM_VERSION"
find "$TOP_DIR" -mindepth 1 -maxdepth 1 "(" "(" -path "$TOP_DIR/rpm" -o -path "$TOP_DIR/conf" -o -name ".*.swp" -o -name ".git*" ")" -prune ")" -o -exec cp -alr '{}' "$RPM_DIR/WORK/tmp/$RPM_NAME-$RPM_VERSION"/ ';'
tar -C "$RPM_DIR/WORK/tmp" --exclude ".*.swp" --exclude ".git*" -cf "$RPM_DIR/WORK/SOURCES/$RPM_NAME-$RPM_VERSION.tar" "$RPM_NAME-$RPM_VERSION"

# create spec file
sed <$RPM_NAME.spec.in >$RPM_DIR/WORK/SPECS/$RPM_NAME.spec \
	-e "s,@@RPM_NAME@@,$RPM_NAME,g" \
	-e "s,@@RPM_VERSION@@,$RPM_VERSION,g" \
	-e "s,@@RPM_RELEASE_SUFFIX@@,$RPM_RELEASE_SUFFIX,g"

#cp -alr "$RPM_DIR/src/"* "$RPM_DIR/WORK/SOURCES"/

rpmbuild -bb --define "_topdir $(readlink -f $RPM_DIR/WORK)" --define "dist $rpm_dist" "$RPM_DIR/WORK/SPECS/$RPM_NAME.spec"

mkdir -p $RPM_DIR/RPMS
find $RPM_DIR/WORK/RPMS -type f -name '*.rpm' -exec cp -al '{}' $RPM_DIR/RPMS/ ';'

set +x
echo
find $RPM_DIR/RPMS -maxdepth 1 -type f -name '*.rpm' -exec echo -e '\t{}' ';'
echo

