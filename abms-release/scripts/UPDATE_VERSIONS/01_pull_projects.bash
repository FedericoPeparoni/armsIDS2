#!/bin/bash

# exit on any error
set -e

# include common functions
. ../lib/common.bash

# read project repo URLs
declare -a REPO_URLS
if [ -f $TOP_DIR/repos.conf ] ; then
	repos_url_file="$TOP_DIR/repos.conf"
else
	repos_url_file="$TOP_DIR/repos.conf.default"
fi
stat "$repos_url_file" >/dev/null
unset line
while read -r line || [[ -n $line ]] ; do
	line=$(echo "$line" | sed -r 's,^\s+,,g;s,\s+$,,g')
	[[ $line =~ ^\s*(#.*)?$ ]] && continue || :
	REPO_URLS+=("$line")
done <"$repos_url_file"

# determine current branch
BRANCH=$(git branch | grep '\*' | awk '{print $2}')
[[ -n $BRANCH ]] || fatal "unable to determine current branch"


# re-create working directory that will contain cloned project repos
rm -rf WORK
mkdir -p WORK

# clone each project
for url in "${REPO_URLS[@]}" ; do
	proj=$(basename "$url" .git)
	log_notice "cloning $proj to $PWD/WORK/$proj"
	git clone -q --branch "$BRANCH" "$url" "WORK/$proj"
done


