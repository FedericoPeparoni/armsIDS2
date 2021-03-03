#!/bin/bash

#
# Build navdb and navdb-data RPMs.
#
# ENVIRONMENT VARIABLES
# =====================
#
# BUILD_DATA_RPM=0
#   If set to "0", don't build the data RPM at all
#

set -ex

RPM_DIR=$(dirname $0)
TOP_DIR=$RPM_DIR/..

. "$TOP_DIR/package.conf"

AERO_DATA_FILE="$(basename "$AERO_DATA_URL" | sed -r 's,\.(gz|bz2|xz)$,,g')"
read DATASET_NAME DATAFILE_VERSION DATAFILE_MIN_DATAMODEL_VERSION <<<$(echo "$AERO_DATA_FILE" | sed -nr 's#^(navdb.*)-([0-9]{4})(\.it[0-9]{6})?(\.p[0-9]{6})?-([0-9]\.[0-9.]+).*.sql$#\1 \2\3\4 \5#gp')
if [[ -z $DATASET_NAME || -z $DATAFILE_VERSION || -z $DATAFILE_MIN_DATAMODEL_VERSION ]] ; then
	echo "Invalid load data file name \`$AERO_DATA_FILE'" >&2
	exit 1
fi

DATA_MODEL_VERSION="$(sed -rn 's,^\\set\s+navdb_version\s+(.+)\s*$,\1,gp' "$TOP_DIR/sql/config.sql" | sed "s/['\"]//g")"

#: ${BUILD_NUMBER:="$1"}
#: ${BUILD_NUMBER:="${CI_BUILD_ID}"}
#: ${BUILD_NUMBER:=0}

#GIT_HASH=$(git rev-parse --short HEAD)
#[ -n "$GIT_HASH" ] || { echo "Unable to determine git hash" >&2 ; exit 1 ; }

#RPM_RELEASE_SUFFIX=".bld_${BUILD_NUMBER}.git_${GIT_HASH}%{?dist}"
RPM_RELEASE_SUFFIX="%{?dist}"
RPM_RELEASE="${RPM_SPEC_VERSION}${RPM_RELEASE_SUFFIX}"

DIST="$(rpm --eval '%dist' | sed 's,^\(..*\)\.centos$,\1,g')"

rm -rf $RPM_DIR/{WORK,RPMS}
mkdir -p $RPM_DIR/WORK/{SOURCES,SPECS,RPMS,SRPMS,tmp}

# create autoconf files
( cd "$TOP_DIR" && ./autogen.sh ; )

# build source tar file
rm -f $TOP_DIR/$PACKAGE_NAME-[0-9]*.tar.*
( cd $TOP_DIR && ./configure && make dist GZIP_ENV=--fast; )
cp -alr $TOP_DIR/$PACKAGE_NAME-[0-9]*.tar.* "$RPM_DIR/WORK/SOURCES"/

#cp -alr "$RPM_DIR/src/"* "$RPM_DIR/WORK/SOURCES"/

spec_list="$PACKAGE_NAME"
if [[ $BUILD_DATA_RPM != 0 ]] ; then
	spec_list="$spec_list $PACKAGE_NAME-data"
fi
for b in $spec_list ; do
	sed <$b.spec.in >$RPM_DIR/WORK/SPECS/$b.spec \
		-e "s,@@PACKAGE_NAME@@,$PACKAGE_NAME,g" \
		-e "s,@@PACKAGE_VERSION@@,$PACKAGE_VERSION,g" \
		-e "s,@@RPM_EPOCH@@,$RPM_EPOCH,g" \
		-e "s,@@RPM_SPEC_VERSION@@,$RPM_SPEC_VERSION,g" \
		-e "s,@@RPM_RELEASE@@,$RPM_RELEASE,g" \
		-e "s,@@DATA_MODEL_VERSION@@,$DATA_MODEL_VERSION,g" \
		-e "s,@@DATAFILE_VERSION@@,$DATAFILE_VERSION,g" \
		-e "s,@@DATAFILE_MIN_DATAMODEL_VERSION@@,$DATAFILE_MIN_DATAMODEL_VERSION,g"
	rpmbuild -bb \
		--define "_topdir $(readlink -f $RPM_DIR/WORK)" \
		--define "dist $DIST" \
		"$RPM_DIR/WORK/SPECS/$b.spec"
done

mkdir -p $RPM_DIR/RPMS
find $RPM_DIR/WORK/RPMS -type f -name '*.rpm' -exec cp -al '{}' $RPM_DIR/RPMS/ ';'

set +x
echo
find $RPM_DIR/RPMS -maxdepth 1 -type f -name '*.rpm' -exec echo -e '\t{}' ';'
echo

