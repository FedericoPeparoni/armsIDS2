#!/bin/bash

set -e

URL='http://ubidev04.idscorporation.ca/pub/repos/ids/navdb/rpms/el$releasever'

# Validate OS make/version
if [[ ! -f /etc/system-release ]] ; then
	echo "error: unable to deremine OS version" >&2
	exit 1
fi
if ! grep -q -i -E '(centos|redhat).* [67]\.[0-9]+' /etc/system-release >/dev/null ; then
	echo "error: this script requires CentOS or RedHat Enterprise Linux version 6.x or 7.x" >&2
	exit 1
fi

# Make sure we are running as root
if [[ $EUID != 0 ]] ; then
	echo "error: this scrip must be run by root user" >&2
	exit 1
fi

# Create repo file
repo_file=/etc/yum.repos.d/navdb.repo
if [[ ! -f $repo_file ]] ; then
	echo "Creating $repo_file ..." >&2
	cat >>$repo_file.tmp <<-END
		[navdb]
		name=navdb
		baseurl=$URL
		gpgcheck=0
		enabled=1
	END
	mv $repo_file.tmp $repo_file
else
	sed -r "/^[[:space:]]*enabled[[:space:]]*=/d;s#^[[:space:]]*baseurl=.*#baseurl=$URL#g" $repo_file | sed "\$aenabled=1" >$repo_file.tmp
	mv $repo_file.tmp $repo_file
fi


# Install the packages
yum -q -y --disablerepo='*' --enablerepo=navdb clean all
yum -q -y --disablerepo='*' --enablerepo=navdb makecache
yum "$@" install navdb navdb-data

