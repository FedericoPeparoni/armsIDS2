#!/bin/bash

set -e

TOP_DIR="$(dirname "$0")"
TOP_DIR="$(readlink -f "$TOP_DIR")"
HOME_DIR="$TOP_DIR/.geoserver_home"
USER="$(id -un)"

# temp dir
GEOSERVER_TMPDIR="$TOP_DIR/.tmp/$USER"
GEOSERVER_TMPDIR_PORTABLE=
if which cygpath >/dev/null 2>&1 ; then
	GEOSERVER_TMPDIR_PORTABLE="$(cygpath --windows "$GEOSERVER_TMPDIR")"
else
	GEOSERVER_TMPDIR_PORTABLE="$GEOSERVER_TMPDIR"
fi

# source java options
if [ -f "$TOP_DIR/conf/startup.conf" ] ; then
	. "$TOP_DIR/conf/startup.conf"
fi
: ${JAVACMD:="java"}

# make sure conf/geoserver.properties exists
if ! [ -f "$TOP_DIR/conf/geoserver.properties" ] ; then
	echo "$TOP_DIR/conf/geoserver.properties: file not found" >&2
	exit 1
fi

# download extra jars
( cd "$TOP_DIR"/src/extra && bash download.sh ; )

# create home directory
$SHELL "$TOP_DIR"/create_home_dir.sh "$HOME_DIR"

# create GWC cache directory
mkdir -p "$TOP_DIR/.gwc"

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

mkdir -p "$GEOSERVER_TMPDIR"

cd "$HOME_DIR"
exec "$JAVACMD" \
	$JAVA_OPTS \
	-DGEOSERVER_DATA_DIR="$TOP_DIR/data_dir" \
	-DGEOWEBCACHE_CACHE_DIR="$TOP_DIR/.gwc" \
	-DGT2_LOGGING_REDIRECTION="CommonsLogging" \
	-DRELINQUISH_LOG4J_CONTROL="true" \
	-Djava.awt.headless=true \
	-Dlogging.propFile="$TOP_DIR/conf/logging.properties" \
	-Dlogback.configurationFile="$HOME_DIR/etc/logback.xml" \
	-Djava.io.tmpdir="$GEOSERVER_TMPDIR_PORTABLE" \
	"${jvm_args[@]}" \
	-jar start.jar \
	"$TOP_DIR/conf/geoserver.properties" \
	"$HOME_DIR/etc/jetty-jndi.xml" \
	"${user_args[@]}"

