#!/bin/bash

[[ -n $CI ]] || { echo "This script must be run by a GitLab CI runner!" >&2 ; exit 1 ; }
CHECK_VER=$1

set -x

rhel="$(rpm --eval '%{?rhel}')"
cp centos${rhel}-idsna.repo /etc/yum.repos.d/

yum clean all

yum --disablerepo='*' --enablerepo="centos${rhel}-idsna" -y install yum-utils perl

yum-config-manager --disable '*' >/dev/null

yum-config-manager --enable "centos${rhel}-idsna" >/dev/null
yum clean all

perl_build_req="$( ( cd ../rpm && grep BuildRequires navdb.spec* ; ) | sed 's,BuildRequires:,,g' | sort -u )"

yum install -y \
	$perl_build_req \
	openssh-clients rsync bzip2 wget perl rpm-build autoconf automake \
	redhat-rpm-config

