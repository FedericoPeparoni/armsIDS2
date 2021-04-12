#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# get the version of this package
. $TOP_DIR/package.conf

# for each project
changed=
for d in WORK/* $TOP_DIR ; do
	p=$(basename $(readlink -f $d))
	v=$(get_project_version $d)
	if [[ $PACKAGE_VERSION != $v ]] ; then
		log_notice "updating $p $v => $PACKAGE_VERSION"
		set_project_version $d $PACKAGE_VERSION
		changed=1
	fi
done

if [[ $changed != 1 ]] ; then
	log_notice "all projects are already set to version $PACKAGE_VERSION"
fi
exit 0

