#!/bin/bash

set -e
cmd=$(basename "$0" | sed -rn 's,^navdb_([^.]+).*,\1,gp')

if [[ -t 2 ]] ; then
	BOLD="\e[1m"
	RED="\e[0;31m"
	NORM="\e[0m";
else
	BOLD=
	RED=
	NORM=
fi

echo >&2
echo -e "${RED}WARNING${NORM}: This command is deprecated and will be removed in a future release." >&2
if [[ -n $cmd ]] ; then
	echo -e "${RED}WARNING${NORM}: Please use \`${BOLD}navdb $cmd${NORM}' (no space) instead" >&2
fi
echo -e "${RED}WARNING${NORM}: Please type \`${BOLD}navdb --help${NORM}' for more information." >&2
echo >&2

if [[ -t 2 ]] ; then
	for i in 3 2 1 ; do
		echo -e -n "\rCommand will continue in $i second(s) ... " >&2
		sleep 1
	done
	echo >&2
	echo >&2
fi

exec navdb $cmd "$@"

