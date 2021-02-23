#!/bin/bash

RPM_NAME="abms-api"
POM_PROPS="META-INF/maven/ca.ids.abms/api/pom.properties"

RPM_DIR=$(dirname $0)
TOP_DIR=$RPM_DIR/..
: ${BUILD_NUMBER:="$1"}
: ${BUILD_NUMBER:="${CI_BUILD_ID}"}
: ${BUILD_NUMBER:=0}
: ${COMPRESS:=0}

set -ex

JAR_FILE=$(find $TOP_DIR/target -maxdepth 1 -type f -name '*.jar')
[ $(echo "$JAR_FILE" | wc -w) -eq 1 ] || { echo "No or multiple JAR files found" >&2 ; exit 1 ; }
JAR_FILE_BASENAME="$(basename $JAR_FILE)"

if [ -z "$RPM_VERSION" ] ; then
	RPM_VERSION=$(unzip -c $JAR_FILE "$POM_PROPS" | sed -n 's,^version=\(.*\),\1,gp' | sed 's,-SNAPSHOT,,g')
	[ -n "$RPM_VERSION" ] || { echo "Unable to determine package version" >&2 ; exit 1 ; }
fi

GIT_HASH=$(git rev-parse --short HEAD)
[ -n "$GIT_HASH" ] || { echo "Unable to determine git hash" >&2 ; exit 1 ; }

RPM_RELEASE_SUFFIX=".bld_${BUILD_NUMBER}.git_${GIT_HASH}${PACKAGE_QUALIFIER}%{?dist}"

DIST="$(rpm --eval '%dist' | sed 's,^\(..*\)\.centos$,\1,g')"

rm -rf $RPM_DIR/{WORK,RPMS}
mkdir -p $RPM_DIR/WORK/{SOURCES,SPECS,RPMS,SRPMS,tmp}
sed <rpm.spec.in >$RPM_DIR/WORK/SPECS/$RPM_NAME.spec \
	-e "s,@@RPM_NAME@@,$RPM_NAME,g" \
	-e "s,@@RPM_VERSION@@,$RPM_VERSION,g" \
	-e "s,@@RPM_RELEASE_SUFFIX@@,$RPM_RELEASE_SUFFIX,g" \
	-e "s,@@JAR_FILE_BASENAME@@,$JAR_FILE_BASENAME,g"

cp -alr "$JAR_FILE" "$RPM_DIR/WORK/SOURCES"
cp -alr "$RPM_DIR/src/"* "$RPM_DIR/WORK/SOURCES"/

rpmbuild -bb \
	--define "_topdir $(readlink -f $RPM_DIR/WORK)" \
	--define "dist $DIST" \
	--define "compress $COMPRESS" \
	"$RPM_DIR/WORK/SPECS/$RPM_NAME.spec"

mkdir -p $RPM_DIR/RPMS
find $RPM_DIR/WORK/RPMS -type f -name '*.rpm' -exec cp -al '{}' $RPM_DIR/RPMS/ ';'

set +x
echo
find $RPM_DIR/RPMS -maxdepth 1 -type f -name '*.rpm' -exec echo -e '\t{}' ';'
echo

