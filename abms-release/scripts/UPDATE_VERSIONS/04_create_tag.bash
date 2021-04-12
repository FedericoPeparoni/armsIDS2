#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# get the version of this package
. $TOP_DIR/package.conf

# calculate tag value
TAG="v${PACKAGE_VERSION}"

# check whether any projects already contain this tag that points to a commit
# other than HEAD
# - don't process this project -- abms-release because we don't want the tag on it
declare -a UNTAG_DIRS
declare -a TAG_DIRS
for d in WORK/* ; do
	head_sha=$(cd $d && git rev-parse --verify HEAD)
	tag_sha=$(cd $d && git rev-parse --verify "$TAG" 2>/dev/null || :)
	# tag exists and points to somewhere other than HEAD
	if [[ -n $tag_sha && $head_sha != $tag_sha ]] ; then
		UNTAG_DIRS+=($d)
		TAG_DIRS+=($d)
		continue
	fi
	# tag doesn't exist at all
	if [[ -z $tag_sha ]] ; then
		TAG_DIRS+=($d)
		continue
	fi
done

# Ask user to confirm old tag deletion
if [[ ${#UNTAG_DIRS[@]} -gt 0 ]] ; then
	(
		echo
		echo "It looks like you are trying to re-tag the following projects:"
		for d in "${UNTAG_DIRS[@]}" ; do
			p=$(basename $(readlink -f $d))
			echo $'\t'$p
		done
		echo
		echo "I need to delete the old tags first, but this may introduce"
		echo "inconsistencies in the installers and existing source code checkouts"
		echo
	) >&2

	confirm "Continue?"
	for d in "${UNTAG_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		log_notice "deleting old tag $TAG from project $p"
		( cd "$d" && git tag -d "$TAG" ; )
	done
fi

# Ask user to confirm tag creation
if [[ ${#TAG_DIRS[@]} -gt 0 ]] ; then
	(
		echo
		echo "New tag $TAG needs to be added for the following projects:"
		for d in "${TAG_DIRS[@]}" ; do
			p=$(basename $(readlink -f $d))
			echo $'\t'$p
		done
		echo
	) >&2
	confirm "Continue?"
	for d in "${TAG_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		log_notice "creating new tag $TAG in project $p"
		( cd "$d" && git tag "$TAG" ; )
	done
	exit 0
fi

log_notice "tag $TAG already exists in all projects"
exit 0

