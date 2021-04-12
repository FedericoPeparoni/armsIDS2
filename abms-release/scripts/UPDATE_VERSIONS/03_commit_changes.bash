#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# get the version of this package
. $TOP_DIR/package.conf

# find hanged projects
declare -a CHANGED_DIRS
for d in WORK/* $TOP_DIR ; do
	# determine whether there are uncommitted changes
	# see here: https://stackoverflow.com/questions/3878624/how-do-i-programmatically-determine-if-there-are-uncommitted-changes

	# When processing this project, look only at package.conf, in case
	# whoever is running this script made additinal changes that they don't want to commit.
	if [[ $(readlink -f $d) == $TOP_DIR_ABS ]] ; then
		diff_index_args=package.conf
	# Otherwise look at all changes, because it's hard for us to know what specific files was changed,
	# since it's different in every project (pom.xml? package.json? etc)
	else
		diff_index_args=
	fi
	# look for uncommitted changes in projects directory
	if ! ( cd "$d" && git diff-index --quiet HEAD -- $diff_index_args ; ) then
		CHANGED_DIRS+=("$d")
	fi
done

# Ask user if they want to commit version changes in projects under WORK/ and/or
# in the current directory (abms-release)
if [[ ${#CHANGED_DIRS[@]} -gt 0 ]] ; then
	echo >&2
	echo "Version information has been updated in the following projects:" >&2
	for d in "${CHANGED_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		echo $'\t'$p >&2
	done
	echo >&2

	confirm "Do you want to commit these changes?"
	echo >&2
	for d in "${CHANGED_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		log_notice "committing version changes in $p"
		# When processing this project, look only at package.conf, in case
		# whoever is running this script made additinal changes that they don't want to commit.
		if [[ $(readlink -f $d) == $TOP_DIR_ABS ]] ; then
			commit_args=package.conf
		# Otherwise look at all changes, because it's hard for us to know what specific files was changed,
		# since it's different in every project (pom.xml? package.json? etc)
		else
			commit_args=.
		fi
		( cd "$d" && git commit -q -m "set project version to $PACKAGE_VERSION" $commit_args ; )
	done
	exit 0
fi

log_notice "no changes to commit"
exit 0

