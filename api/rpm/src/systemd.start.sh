#!/bin/bash

set -e

EXE="$(dirname $0)/run.sh"
TRIGGER_FILE="/var/run/abms/api/.startupTrigger"
#PID_FILE="/var/run/abms/api/api.pid"

rm -f "$TRIGGER_FILE"
exec $EXE \
	-Dabms.startupTriggerFile=$TRIGGER_FILE \
	-Dlogging.journaldConsoleEnabled=true \
	-Dlogging.consoleEnabled=false \
	-Dlogging.fileEnabled=true \
	-Dspring.output.ansi.enabled=NEVER \
	-Dspring.main.banner-mode=off \
	2>&1

#echo "$!" >$PID_FILE

