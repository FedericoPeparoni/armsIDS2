#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# include project config
. $TOP_DIR/package.conf

# determine current branch
BRANCH=$(git branch | grep '\*' | awk '{print $2}')
[[ -n $BRANCH ]] || fatal "unable to determine current branch"

# if branch is of the form "develop-N.xxx", make sure package version
# begins with N.xxx
if [[ $BRANCH =~ ^develop-[0-9] ]] ; then
	ver_prefix=${BRANCH##develop-}
	case "$PACKAGE_VERSION" in
		$ver_prefix|$ver_prefix.*)
			;;
		*)
			fatal "current branch name $BRANCH doesn't match prefix of PACKAGE_VERSION $PACKAGE_VERSION"
			;;
	esac
fi

# make sure current project has a tracking branch
git cherry -v >/dev/null || fatal "no tracked remote branch found"

# try to run perl
perl -v >/dev/null

# try to run java
java -version >/dev/null 2>&1 || fatal "java not found"
javac -version >/dev/null 2>&1 || fatal "javac not found"


