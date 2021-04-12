#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# get the version of this package
. $TOP_DIR/package.conf

# find changed projects
declare -a CHANGED_DIRS
for d in WORK/* $TOP_DIR ; do
	# See https://makandracards.com/makandra/927-git-see-all-unpushed-commits-or-commits-that-are-not-in-another-branch
	if [[ $(cd "$d" && git cherry | wc -l) -gt 0 ]] ; then
		CHANGED_DIRS+=("$d")
	fi
done

# Ask user if they want to commit version changes
changed=
if [[ ${#CHANGED_DIRS[@]} -gt 0 ]] ; then
	echo >&2
	echo "Version information has been updated in the following projects:" >&2
	for d in "${CHANGED_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		echo $'\t'$p >&2
	done
	echo >&2

	confirm "Do you want to push these changes?"
	echo >&2
	for d in "${CHANGED_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		log_notice "pushing $p"
		WORK_DIR=$PWD/WORK
		rm -f WORK/.$p.success
		( cd "$d" && git push -q && touch $WORK_DIR/.$p.success ; ) 2>&1 | grep -v '^remote: ' >&2 || :
		[[ -f WORK/.$p.success ]] || fatal "failed to push $p"
	done
	changed=1
fi
rm -f WORK/.*.success

[[ $changed == 1 ]] || log_notice "no changes to push"
exit 0

