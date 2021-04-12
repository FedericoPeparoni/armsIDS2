#!/bin/bash

set -e

USER=abms
CONF_DIR=/etc/abms/geoserver
DATA_DIR=/usr/share/abms/geoserver
HOME_DIR=$DATA_DIR/jetty
GEOSERVER_DATA_DIR=/var/lib/abms/geoserver/data
GEOWEBCACHE_CACHE_DIR=/var/cache/abms/geoserver/gwc
GEOSERVER_LOG_LOCATION=/var/log/abms/geoserver/geoserver.log
GEOSERVER_TMP_DIR="/var/tmp/abms-geoserver--$USER"

# Make sure we are running as user abms
if ! [ `id -un` = "$USER" ] ; then
	echo "This script must be run by user \`abms'" >&2
	exit 1
fi

# read JVM options
if [ -f "$CONF_DIR/startup.conf" ] ; then
	. "$CONF_DIR/startup.conf"
fi
: ${JAVACMD:="java"}

# make sure conf/geoserver.properties exists
if ! [ -f "$CONF_DIR/geoserver.properties" ] ; then
	echo "$CONF_DIR/geoserver.properties: file not found" >&2
	exit 1
fi

# Add system properties to command JVM command line
declare -a jvm_args
declare -a user_args
while [ "$#" -gt 0 ] ; do
	case "$1" in
		-D?*=*)
			jvm_args+=("$1")
			shift || :
			continue
			;;
	esac
	user_args+=("$1")
	shift || :
done

# create temp dir if necessary
mkdir -p "$GEOSERVER_TMP_DIR"

# exec geoserver
cd "$HOME_DIR"
exec "$JAVACMD" \
	$JAVA_OPTS \
	-DGEOSERVER_DATA_DIR="$GEOSERVER_DATA_DIR" \
	-DGEOWEBCACHE_CACHE_DIR="$GEOWEBCACHE_CACHE_DIR" \
	-DGEOSERVER_LOG_LOCATION="$GEOSERVER_LOG_LOCATION" \
	-Dlogging.propFile="$CONF_DIR/logging.properties" \
	-Dlogback.configurationFile="$HOME_DIR/etc/logback.xml" \
	-DGT2_LOGGING_REDIRECTION="CommonsLogging" \
	-DRELINQUISH_LOG4J_CONTROL="true" \
	-Djava.awt.headless=true \
	-Djava.io.tmpdir="$GEOSERVER_TMP_DIR" \
	"${jvm_args[@]}" \
	-jar start.jar \
	"$CONF_DIR/geoserver.properties" \
	"$HOME_DIR/etc/jetty-jndi.xml" \
	"${user_args[@]}"


