#!/bin/bash

set -ex
TOP_SRCDIR=$(dirname $0)/..
TOP_SRCDIR_ABS=$(readlink -f $TOP_SRCDIR)
RPM_DIR=.
: ${COMPRESS:=0}

# SCM suffix
if [ -d "$TOP_SRCDIR/.git" ] ; then
	GIT_HASH=$(git rev-parse --short HEAD)
	[ -n "$GIT_HASH" ] || { echo "Unable to determine git hash" >&2 ; exit 1 ; }
	SCM_SUFFIX=".git_${GIT_HASH}"
else
	SCM_SUFFIX=""
fi

# build suffix
: ${BUILD_NUMBER:="$1"}
: ${BUILD_NUMBER:="$CI_BUILD_ID"}
: ${BUILD_NUMBER:=0}
BUILD_SUFFIX=".bld_${BUILD_NUMBER}${SCM_SUFFIX}${PACKAGE_QUALIFIER}"

# remove generated files
rm -rf $RPM_DIR/RPMS
rm -rf $RPM_DIR/WORK

# Save PACKAGE_VERSION from environment
override_PACKAGE_VERSION="$PACKAGE_VERSION"

# read package.conf
. $TOP_SRCDIR/package.conf

# restore PACKAGE_VERSION from environment
[ -z "$override_PACKAGE_VERSION" ] || PACKAGE_VERSION="$override_PACKAGE_VERSION"

# find out package name and version
PACKAGE_NAME=abms-geoserver
SRC_NAME=${PACKAGE_NAME}-${PACKAGE_VERSION}
TAR_FILE=$SRC_NAME.tar

# create RPM build area
mkdir -p $RPM_DIR/WORK/{SOURCES,SPECS,RPMS,SRPMS,tmp}

# download external files
(cd $TOP_SRCDIR/src/extra && bash download.sh ; )

# create source tarball
bash $TOP_SRCDIR/create_home_dir.sh "$RPM_DIR/WORK/tmp/${PACKAGE_NAME}-${PACKAGE_VERSION}"
cp -alr $TOP_SRCDIR/data_dir/* "$RPM_DIR/WORK/tmp/${PACKAGE_NAME}-${PACKAGE_VERSION}"/data_dir/
tar -c -C "$RPM_DIR/WORK/tmp" "${PACKAGE_NAME}-${PACKAGE_VERSION}" >"$RPM_DIR/WORK/SOURCES/${PACKAGE_NAME}-${PACKAGE_VERSION}".tar

# prepare RPM build area
cp -a $TOP_SRCDIR/conf.example/* $RPM_DIR/WORK/SOURCES/
find $RPM_DIR/src -maxdepth 1 -mindepth 1 -exec cp -a '{}' $RPM_DIR/WORK/SOURCES/ ';'

# generate spec file
sed \
	-e "s,@@PACKAGE_NAME@@,$PACKAGE_NAME,g" \
	-e "s,@@PACKAGE_VERSION@@,$PACKAGE_VERSION,g" \
	-e "s,@@BUILD_SUFFIX@@,$BUILD_SUFFIX,g" \
	-e "s,@@GS_VERSION@@,$GS_VERSION,g" \
	-e "s,@@JETTY_VERSION@@,$JETTY_VERSION,g" \
	<$RPM_DIR/$PACKAGE_NAME.spec.in >$RPM_DIR/WORK/SPECS/$PACKAGE_NAME.spec

# build the rpm
dist=$(rpm --eval '%{?dist}' | sed 's,\.centos$,,g')
rpmbuild -bb \
	--define "_topdir $TOP_SRCDIR_ABS/rpm/WORK" \
	--define "dist $dist" \
	--define "compress $COMPRESS" \
	"$RPM_DIR/WORK/SPECS/$PACKAGE_NAME.spec"

# create RPMS directory
mkdir -p $RPM_DIR/RPMS
find $RPM_DIR/WORK/RPMS -type f -name '*.rpm' -exec cp -al '{}' $RPM_DIR/RPMS/ ';'

set +x
echo
find $RPM_DIR/RPMS -maxdepth 1 -type f -name '*.rpm' -exec echo -e '\t{}' ';'
echo


