#!/bin/bash

# exit on any error
set -e

# defaults, maybe overridden in the environment
: ${CONF_DIR:="/etc/abms/api"}
: ${DATA_DIR:="/usr/share/abms/api"}
: ${SERVER_PORT:=14000}
: ${LOG_DIR:="/var/log/abms/api"}
: ${JAR_DIR:="/usr/share/abms/api/lib"}
: ${MAIN_JAR_FILE:=}
: ${JAVA_HOME:=}
: ${JAVA_OPTS:=}
: ${TZ:=}
STARTUP_CONF="$CONF_DIR/startup.conf"

# make sure conf directory and files are accessible
ls -1 "$CONF_DIR" >/dev/null
head -n 1 "$CONF_DIR/"*.{conf,properties} >/dev/null
if [ -e "$CONF_DIR/startup.conf" ] ; then
	. "$CONF_DIR/startup.conf" || exit 1
else
	echo "$CONF_DIR/startup.conf: file not found, using default startup options" >&2
fi

# Listen on this port
REQ_OPTS="-Dserver.port=$SERVER_PORT"

# If log files are used make sure log directory is readable
REQ_OPTS="$REQ_OPTS -Dlogging.file=$LOG_DIR/api.log"
case "$*" in
	*-Dlogging.fileEnabled=true)
		REQ_OPTS="$REQ_OPTS -Dlogging.fileEnabled=true"
		ls -1 "$LOG_DIR" >/dev/null
		[ -r "$LOG_DIR" -a -w "$LOG_DIR" -a -x "$LOG_DIR" ] || { echo "$LOG_DIR: permission denied" >&2 ; exit 1 ; }
		for f in "$LOG_DIR/"*.log ; do
			[ -f "$f" ] || continue
			head -n1 "$f" >/dev/null
		done
		;;
esac

# Set spring options
REQ_OPTS="$REQ_OPTS -Dspring.config.location=$DATA_DIR/conf/application-defaults.properties,$CONF_DIR/,$CONF_DIR/logging.properties"

# Set dbqueries location
REQ_OPTS="$REQ_OPTS -Dabms.dbqueries.queryFilesLocation=$CONF_DIR/dbqueries"

# Set app prefix
REQ_OPTS="$REQ_OPTS -Dabms.http.proxyContextPath=/abms"

# Find the main JAR file
if [ -z "$MAIN_JAR_FILE" ] ; then
	MAIN_JAR_FILE="$(find "$JAR_DIR" -maxdepth 1 -type f -name '*.jar')"
	JAR_COUNT="$(echo "$MAIN_JAR_FILE" | wc -l)"
	if [ "$JAR_COUNT" -lt 1 ] ; then
		echo "No JAR files found in \`$JAR_DIR'" >&2
		exit 1
	fi
	if [ "$JAR_COUNT" -gt 1 ] ; then
		cat >&2 <<_END
		
Multiple JAR files found in \`$JAR_DIR'

Please remove extra JAR files or specify the JAR_PATH in
\`$ENV_FILE'.

_END
		exit 1
	fi
fi

# include RedHat Java configutation
if [ -r /usr/share/java-utils/java-functions ] ; then
	. /usr/share/java-utils/java-functions
	set_javacmd
fi
: ${JAVACMD:=java}


# exec the app
set_javacmd
export TZ
exec ${JAVACMD} ${REQ_OPTS} ${JAVA_OPTS} "$@" -jar "${MAIN_JAR_FILE}"

