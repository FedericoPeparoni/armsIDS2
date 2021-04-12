#!/bin/bash
# vim:set ts=8 sts=8 sw=8 noet:

set -e

. "$(dirname $(readlink -f "$0"))/../lib/functions.sh"

INPUT_FILE=
EXTRA_ARGS=

get_db_owner() {
	psql_su -l | awk 'BEGIN {name=ARGV[1];--ARGC};{if($1 == name) print $3}' "$DB_NAME" || :
}

# restore the database
restore_db() {

	# validate input file
	if [ -n "$INPUT_FILE" ] ; then
		stat "$INPUT_FILE" >/dev/null
		[ -f "$INPUT_FILE" ] || die "$INPUT_FILE: not a regular file"
	fi

	# drop database first
	( cd / && drop_db ; )
	
	# create database first
	( cd / && create_db ; )
	
	# restore the dump
	echo "restoring database \`$DB_NAME'"
	local -a args
	args=(--dbname="$DB_NAME" --role="$DB_OWNER" --no-owner --no-privileges $EXTRA_ARGS)
	if [ -n "$INPUT_FILE" ] ; then
		( cd / && pg_su $PG_RESTORE  "${args[@]}" ; ) < "$INPUT_FILE"
	else
		( cd / && pg_su $PG_RESTORE  "${args[@]}" ; )
	fi
}


# handle command line errors
cmdline_error() {
	local msg="$1"
	if [ -n "$msg" ] ; then
		echo "error: $msg" >&2
	fi
	echo "Type \`$PROGNAME --help' for more info." >&2
	exit 7
}

# print out an error message and exit
usage() {
	cat <<_END
Usage: $PROGNAME [OPTIONS...] [VAR=VALUE...] {FILE | -}
Create ABMS database
 -h,--help                   print this message and exit
    --dbname=NAME            dump this database (default: $DB_NAME)
 -k,--keep-going             don't exit and rollback on errors
    --no-tablespaces         do not restore tablespace assignments
 -x,--debug                  print each shell command before executing
_END
	exit 0
}

# process command line
init() {
	local keep_going
	quoted_args="$(getopt -n $PROGNAME -l help,dbname:,keep-going,no-tablespaces,debug -o hkx -- "$@")" || cmdline_error
	eval "set -- $quoted_args"
	while [ "$#" -gt 0 ] ; do
		case "$1" in
			--help|-h)
				usage
				;;
			--dbname)
				provided_db_name="$2"
				shift
				;;
			--keep-going|-k)
				keep_going=yes
				;;
			--no-tablespaces)
				EXTRA_ARGS="$EXTRA_ARGS --no-tablespaces"
				;;
			--debug|-x)
				set -x
				;;
			--)
				shift
				break
				;;
			*)
				break
				;;
		esac
		shift
	done
	# process environment variable assignments
	while [ "$#" -gt 0 ] ; do
		key="${1%%=*}"
		if [ "$key" != "$1" ] ; then
			value="${1##*=}"
			declare -g -x "$key=$value"
			shift
			continue
		fi
		break
	done
	[ "$#" -lt 1 ] && cmdline_error "not enough arguments" || :
	[ "$#" -gt 1 ] && cmdline_error "too many arguments" || :
	[ "$1" != "-" ] && { INPUT_FILE="$1" ; shift ; } || :
	[ -n "$keep_going" ] || EXTRA_ARGS="--single-transaction --exit-on-error"
	[ -n "$provided_db_name" ] && DB_NAME="$provided_db_name" || :
}

init "$@"
check_db_connection
restore_db

