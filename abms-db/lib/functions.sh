# vim:set ts=8 sts=8 sw=8 noet: bash

PROGNAME="$(basename $0)"
declare -a ARGS
BIN_DIR="$(dirname $(readlink -f "$0"))"
TOP_DIR="$(readlink -f "$BIN_DIR/..")"
CONF_DIR="$TOP_DIR/conf"
SQL_DIR="$TOP_DIR/sql"
LIB_DIR="$TOP_DIR/lib"
: ${RUNUSER:=/sbin/runuser}

# export all subsequent variables to environment
set -a

# set defaults
: ${OS_SUPERUSER:=postgres}
: ${PGUSER:=postgres}
: ${DB_NAME:=abms}
: ${DB_OWNER:=abms}
: ${PSQL:=psql}
: ${PG_DUMP:=pg_dump}
: ${PG_RESTORE:=pg_restore}

# read configuration
if [ -f "$CONF_DIR/abms-db.conf" ] ; then
	. "$CONF_DIR/abms-db.conf"
fi

# Owner user
DB_OWNER="abms"

# stop exporting variable declarations
set +a

# print out an error message and exit
die() {
	echo "error: $*" >&2 ; 
	exit 1
}

# print out a log message
trace() {
	echo "$*" >&2
}

# Execute a shell command as Linux user "postgres", if possible
pg_su() {
	if [ -n "$OS_SUPERUSER" ] && which "$RUNUSER" >/dev/null 2>&1 ; then
		if [ "$(id -u)" != 0 ] ; then
			die "this script must be run by user \`root' or \`$OS_SUPERUSER'"
		fi
		$RUNUSER -p -u "$OS_SUPERUSER" -- "$@"
	else
		"$@"
	fi
}

# Execute psql as user "postgres"
psql_su() {
	PGOPTIONS="--client-min-messages=warning" pg_su $PSQL -v ON_ERROR_STOP=1 --pset pager=off -q -t "$@"
}

# Make sure we can connect to database as super user
check_db_connection() {
	( cd "$TOP_DIR" && psql_su -c '\q' ; )
}

# Create database
create_db() {
	# user doesn't exist?
	if [ -z "$(psql_su -c '\du' | awk 'BEGIN {name=ARGV[1];--ARGC};{if($1 == name) print $1}' "$DB_OWNER")" ] ; then
		# create it
		trace "creating user \`$DB_OWNER'"
		psql_su -c "create role $DB_OWNER login superuser password 'abms'"
	else
		# oterwise add necessary permissions to owner user
		psql_su -c "alter role $DB_OWNER login superuser"
	fi

	# check whether database exists
	local current_db_owner="$(psql_su -l | awk 'BEGIN {name=ARGV[1];--ARGC};{if($1 == name) print $3}' "$DB_NAME" || :)"
	# database doesn't exist -- create it
	if [ -z "$current_db_owner" ] ; then
		trace "creating database \`$DB_NAME'"
		psql_su -c "create database $DB_NAME owner $DB_OWNER encoding 'UTF-8'"
	# database exists, make sure owner is correct
	elif [ "$current_db_owner" != "$DB_OWNER" ] ; then
		die "database \`$DB_NAME' already exists, but not owned by expected user \`$DB_OWNER', aborting"
	fi
	psql_su -c "alter database $DB_NAME set search_path to abms,public"
}

# Drop database
drop_db() {
	local current_db_owner="$(psql_su -l | awk 'BEGIN {name=ARGV[1];--ARGC};{if($1 == name) print $3}' "$DB_NAME" || :)"
	if [ -n "$current_db_owner" ] ; then
		# terminate existing connections
		psql_su -c "select pg_terminate_backend(pid) from pg_stat_activity where datname='$DB_NAME' and pid <> pg_backend_pid()" >/dev/null
		# drop database
		trace "dropping database \`$DB_NAME'"
		psql_su -c "drop database if exists $DB_NAME"
	fi
}


