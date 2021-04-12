#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# get the version of this package
. $TOP_DIR/package.conf

# calculate tag value
TAG="v${PACKAGE_VERSION}"

# analyze existing tags of each project
declare -a UNTAG_DIRS
declare -a TAG_DIRS
# don't process this project (abms-release) because we don't want the tag on it
for d in WORK/* ; do
	p=$(readlink -f $d)
	# local tag checksum -- created by 04_create_tag.bash
	local_tag_sha1=$(cd "$d" && git rev-parse --verify "$TAG")
	[[ -n $local_tag_sha1 ]] || fatal "local tag $TAG doesn't exist in project $p"
	# remote tag checksum -- pulled during clone from remote "origin" repo
	remote_tag_spec=$(cd "$d" && git ls-remote -t -q origin "$TAG")
	remote_tag_sha1=$(echo "$remote_tag_spec" | awk '{print $1}')
	# if remote tag exists and not equal to local, then we have to delete on remote
	if [[ -n $remote_tag_sha1 && $remote_tag_sha1 != $local_tag_sha1 ]] ; then
		UNTAG_DIRS+=($d)
		TAG_DIRS+=($d)
	fi
	# if remote tag doesn't exist, then we need to push it
	if [[ -z $remote_tag_sha1 ]] ; then
		TAG_DIRS+=($d)
	fi
	
done

# Delete remote tags if necessary
if [[ ${#UNTAG_DIRS[@]} -gt 0 ]] ; then
	(
		echo
		echo "The following projects already contain tag $TAG in remote repos:"
		for d in "${UNTAG_DIRS[@]}" ; do
			p=$(basename $(readlink -f $d))
			echo $'\t'$p
		done
		echo
		echo "I need to delete the old tags from the remote repos first, but this may introduce"
		echo "inconsistencies in the installers and existing source code checkouts"
		echo
	) >&2

	confirm "Continue?"
	for d in "${UNTAG_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		log_notice "deleting old tag $TAG from remote repo of project $p"
		( cd "$d" && git push --delete -q origin "$TAG" ; )
	done
fi

# Push tags to remotes if necessary
if [[ ${#TAG_DIRS[@]} -gt 0 ]] ; then
	(
		echo
		echo "New tag $TAG needs to be pushed for the following projects:"
		for d in "${TAG_DIRS[@]}" ; do
			p=$(basename $(readlink -f $d))
			echo $'\t'$p
		done
		echo
	) >&2

	confirm "Continue?"
	for d in "${TAG_DIRS[@]}" ; do
		p=$(basename $(readlink -f $d))
		log_notice "pushing tag $TAG to remote repo of project $p"
		( cd "$d" && git push -q origin "$TAG" ; )
	done
	exit 0
fi

log_notice "tag $TAG already exists in all remote repos"
exit 0

