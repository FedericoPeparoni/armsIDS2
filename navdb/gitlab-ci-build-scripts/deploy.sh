#!/bin/bash

# This script deploys RPM and TAR files from "STAGE/" to
# a remote server. The "STAGE/" directory is populated by
# script "build.sh", which must be called first.
#
# The following environment variables must be defined when
# calling this script:
#
#   DEPLOY_HOST
#       Host name of the repository server, e.g. "ubidev04.idscorporation.ca"
#   DEPLOY_KEY
#       SSH private key to be used for connecting to $DEPLOY_HOST. This is
#       not a file name, it's the literal key.
#   DEPLOY_USER
#       SSH user name used for connecting
#   DEPLOY_PATH
#       Top-level directory on the remote server to deploy to. We will
#       copy RPMs to "$DEPLOY_PATH/rpms/el6" or similar. We will copy
#       TAR files to "$DEPLOY_PATH/tar".

set -e

# make sure environment variables are defined
for v in DEPLOY_{KEY,USER,HOST,PATH} ; do
	if [[ -z ${!v} ]] ; then
		echo "environment variable $v not defined!" >&2
		exit 1
	fi
done

# create the private key file from the $DEPLOY_KEY variable
# make sure it's deleted on exit
cleanup() {
	rm -f deploy.key
}
echo "$DEPLOY_KEY" >deploy.key
chmod 0600 deploy.key
trap cleanup 0 INT TERM PIPE QUIT

# common SSH options
SSH_OPTS="-i deploy.key -o StrictHostKeyChecking=no -o BatchMode=yes"

# make sure files exist in "STAGE/:
# also make sure they don't exist on the deploy server
# because that would indicate a double git-tag-push
files="$(cd STAGE && find * -type f)"
if [[ $(echo "$files" | wc -l) < 4 ]] ; then
	echo "No build artifacts found!" >&2
	exit 1
fi
for f in $files ; do
	if ssh $SSH_OPTS "$DEPLOY_USER@$DEPLOY_HOST" "[ -f '$DEPLOY_PATH/$f' ]" ; then
		echo "file $DEPLOY_HOST:$DEPLOY_PATH/$f already exists" >&2
		exit 1
	fi
done

# copy files to deploy server
echo
echo "deploying files ..."
RSYNC_RSH="ssh $SSH_OPTS" rsync -rHv STAGE/./ -v --include '*.rpm' "$DEPLOY_USER@$DEPLOY_HOST:$DEPLOY_PATH/"

# re-generate metadata
for platform in el6 el7 ; do
	echo
	echo "updating YUM metadata ($platform) ..."
	ssh $SSH_OPTS "$DEPLOY_USER@$DEPLOY_HOST" "cd $DEPLOY_PATH/rpms/$platform && flock . createrepo -q --update ."
done

# print deployed files
set +x
echo
echo "Deployed files:"
find STAGE -type f -exec ls -hl '{}' '+' | awk '{printf ("\t%-10s %s\n", $5, $9)}'
echo

exit 0

