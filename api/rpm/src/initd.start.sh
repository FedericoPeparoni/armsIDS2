#!/bin/bash

set -e

EXE="$(dirname $0)/run.sh"
TRIGGER_FILE="/var/run/abms/api/.startupTrigger"

rm -f "$TRIGGER_FILE"
$EXE \
	-Dabms.startupTriggerFile=$TRIGGER_FILE \
	-Dlogging.journaldConsoleEnabled=false \
	-Dlogging.consoleEnabled=false \
	-Dlogging.fileEnabled=true \
	-Dspring.output.ansi.enabled=NEVER \
	-Dspring.main.banner-mode=off \
	>/dev/null 2>&1 &
echo $!


