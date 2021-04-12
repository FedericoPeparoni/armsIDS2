#!/bin/bash
# vim:set ts=8 sts=8 sw=8 noet:

set -e

. "$(dirname $(readlink -f "$0"))/../lib/functions.sh"

table_exists() {
	if [ -n "$(cd / && psql_su --dbname="$DB_NAME" -c "\\dt $1
" | egrep -v '^[[:space:]]*$' 2>/dev/null)" ] ; then
		return 0
	fi
	return 1
}

# populate database with tables etc
create_tables() {
	local script_path="$TOP_DIR/sql/create_db.sql"
	if which cygpath >/dev/null 2>&1 ; then
		script_path="$(cygpath --windows "$script_path" | sed 's,\\,/,g')"
	fi
	# run the create script
	{
		set -e
		cd /
		{
			cat <<-END
				\\connect $DB_NAME
				begin;
				set session authorization $DB_OWNER;
				\\include $script_path
				commit;
			END
		} | psql_su -f -
	}
	cat <<-END
	
		WARNING: the database object (but no tables) has been created.
		WARNING: Please start the \`abms-api' service to create or upgrade
		WARNING: table and view definitions.

	END
}

# create or update the database
action_setup() {
	(
		set -e
		cd /
		create_db
		create_tables
	)
}

# drop database
action_drop() {
	( cd / && drop_db ; )
}

# Delete OAUTH tokens
action_clear_auth() {
	if table_exists "abms.oauth_client_token" ; then
		(
			set -e
			cd /
			psql_su --dbname="$DB_NAME" -c "set session authorization $DB_OWNER ; delete from oauth_access_token ; delete from oauth_client_token ; delete from oauth_refresh_token"
		)
	else
		echo "WARNING: table \`oauth_client_token' doesn't exist in database \`$DB_NAME'; exiting..."
		exit 0
	fi
}

# Disable all plugins
action_disable_plugins() {
	if table_exists "abms.plugins" ; then
		(
			set -e
			cd /
			psql_su --dbname="$DB_NAME" -c "set session authorization $DB_OWNER ; update plugins set enabled = false;"
		)
	else
		echo "WARNING: table \`plugins' doesn't exist in database \`$DB_NAME'; exiting..."
		exit 0
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
Usage: $PROGNAME [OPTIONS...] [VAR=VALUE...]
Create or drop ABMS database
 -h,--help            print this message and exit
    --dbname=NAME     name of database to create or drop (default: $DB_NAME)
    --drop            drop database
 -C,--clear-auth      delete all OAUTH access tokens
 -P,--disable-plugins disable all plugins
 -x,--debug           print each shell command before executing
_END
	exit 0
}

# process command line
ACTION="action_setup"
init() {
	local provided_db_name
	quoted_args="$(getopt -n $PROGNAME -l help,drop,dbname:,debug -o hCPx -- "$@")" || cmdline_error
	eval "set -- $quoted_args"
	while [ "$#" -gt 0 ] ; do
		case "$1" in
			--help|-h)
				usage
				;;
			--drop)
				ACTION="action_drop"
				;;
		        --dbname)
				provided_db_name="$2"
				shift
				;;
			--clear-auth|-C)
				ACTION="action_clear_auth"
				;;
			--disable-plugins|-P)
				ACTION="action_disable_plugins"
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
	[ "$#" -gt 0 ] && cmdline_error "too many arguments" || :
	[ -n "$provided_db_name" ] && DB_NAME="$provided_db_name" || :
}

init "$@"
check_db_connection
$ACTION

