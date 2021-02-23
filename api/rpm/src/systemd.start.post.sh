#!/bin/bash

set -e

TRIGGER_FILE="/var/run/abms/api/.startupTrigger"

[ -n "$MAINPID" ] || { echo "MAINPID not defined" >&2 ; exit 1 ; }

while : ; do
	if ! [ -d /proc/$MAINPID ] ; then
		echo "MAINPID=$MAINPID exited unexpectedly" >&2
		exit 2
	fi
	[ -f "$TRIGGER_FILE" ] && exit 0 || :
	sleep 1
done

exit 0

