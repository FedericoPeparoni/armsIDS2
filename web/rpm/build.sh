#!/bin/bash

RPM_NAME="abms-web"

RPM_DIR=$(dirname $0)
TOP_DIR=$RPM_DIR/..
: ${BUILD_NUMBER:="$1"}
: ${BUILD_NUMBER:="$CI_BUILD_ID"}
: ${BUILD_NUMBER:=0}

set -ex

if [ -z "$RPM_VERSION" ] ; then
	# determine package version from package.json
	RPM_VERSION=$(perl -n -e '/"version"\s*:\s*"([^"]+)"/ and do { print $1, "\n" ; exit 0; } ; 0' $TOP_DIR/package.json)
fi
[ -n "$RPM_VERSION" ] || { echo "Unable to determine package version" >&2 ; exit 1 ; }

GIT_HASH=$(git rev-parse --short HEAD)
[ -n "$GIT_HASH" ] || { echo "Unable to determine git hash" >&2 ; exit 1 ; }

# suffix to be added to rpm "release" field -- includes build number and git hash
RPM_RELEASE_SUFFIX=".bld_${BUILD_NUMBER}.git_${GIT_HASH}${PACKAGE_QUALIFIER}%{?dist}"

# remove ".centos" from rpm macro "dist" 
DIST="$(rpm --eval '%dist' | sed 's,^\(..*\)\.centos$,\1,g')"

rm -rf $RPM_DIR/{WORK,RPMS}
mkdir -p $RPM_DIR/WORK/{SOURCES,SPECS,RPMS,SRPMS,tmp}

# build project
if ! [ -d "$TOP_DIR/dist" ] || [ -z "$(find $TOP_DIR/dist -mindepth 1 -print -quit)" ] ; then
	cp -f "$TOP_DIR/.env.example.linux" "$TOP_DIR/.env"
	( cd "$TOP_DIR" && npm run ci-build ; )
fi

# TFS bug 70450: remove reference to the internet resource from stylesheets
# See comments in post_process_css.pl for more info
for f in "$TOP_DIR/dist/styles/"app-*.css ; do
	echo "=== TFS Bug 70450: removing Internet resource reference from $f"
	perl $TOP_DIR/rpm/post_process_css.pl <$f >$f.tmp
	mv $f.tmp $f
done
	
# create tar file
cp -alr "$TOP_DIR/dist" "$RPM_DIR/WORK/tmp/$RPM_NAME-$RPM_VERSION"
tar -C $RPM_DIR/WORK/tmp -czf "$RPM_DIR/WORK/tmp/$RPM_NAME-$RPM_VERSION.tar.gz" "$RPM_NAME-$RPM_VERSION"
mv "$RPM_DIR/WORK/tmp/$RPM_NAME-$RPM_VERSION.tar.gz" "$RPM_DIR/WORK/SOURCES/"

sed <$RPM_NAME.spec.in >$RPM_DIR/WORK/SPECS/$RPM_NAME.spec \
	-e "s,@@RPM_NAME@@,$RPM_NAME,g" \
	-e "s,@@RPM_VERSION@@,$RPM_VERSION,g" \
	-e "s,@@RPM_RELEASE_SUFFIX@@,$RPM_RELEASE_SUFFIX,g"

cp -alr "$RPM_DIR/src/"* "$RPM_DIR/WORK/SOURCES"/

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

