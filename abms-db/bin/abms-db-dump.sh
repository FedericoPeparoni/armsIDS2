#!/bin/bash
# vim:set ts=8 sts=8 sw=8 noet:

set -e

. "$(dirname $(readlink -f "$0"))/../lib/functions.sh"

OUTPUT_FILE=
EXTRA_ARGS=

# dump the database
dump_db() {
	local -a args
	args=(--dbname="$DB_NAME" --format=custom --no-acl --no-unlogged-table-data $EXTRA_ARGS)
	if [ -n "$OUTPUT_FILE" ] ; then
		( cd "$TOP_DIR" && pg_su $PG_DUMP "${args[@]}" ; ) >$OUTPUT_FILE
	elif [ -t 1 ] ; then
		die "refusing to dump to terminal"
	else
		( cd "$TOP_DIR" && pg_su $PG_DUMP "${args[@]}" ; )
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
 -h,--help                      print this message and exit
    --dbname=NAME               dump this database (default: $DB_NAME)
 -Z,--compress=0..9             compression level
    --serializable-deferrable   wait until the dump can run without anomalies
    --lock-wait-timeout=MILLIS  fail after waiting TIMEOUT for a table lock
 -x,--debug                     print each shell command before executing
_END
	exit 0
}

# process command line
init() {
	local key value
	local quoted_args="$(getopt -n $PROGNAME -l help,dbname:,compress:,serializable-deferrable,lock-wait-timeout:,debug -o hZ:x -- "$@")" || cmdline_error
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
			--compress|-Z)
				EXTRA_ARGS="$EXTRA_ARGS --compress=$2"
				shift
				;;
			--serializable-deferrable)
				EXTRA_ARGS="$EXTRA_ARGS --serializable-deferrable"
				;;
			--lock-wait-timeout)
				EXTRA_ARGS="$EXTRA_ARGS --lock-wait-timeout=$1"
				shift
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
	[ "$1" != "-" ] && { OUTPUT_FILE="$1" ; shift ; } || :
	[ -n "$provided_db_name" ] && DB_NAME="$provided_db_name" || :
}

init "$@"
check_db_connection
dump_db

