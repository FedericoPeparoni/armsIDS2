#!/bin/bash

set -e

CONF_DIR=/etc/abms/geoserver
HOME_DIR=/usr/share/abms/geoserver/jetty
CONTEXT_PATH="/abms/geoserver"
DFLT_PORT=8090

[ -n "$MAINPID" ] || { echo "MAINPID not defined" >&2 ; exit 1 ; }

# find Jetty port 
if [ -z "$PORT" -a -f "$CONF_DIR/startup.conf" ] ; then
	. "$CONF_DIR/startup.conf" || :
	for arg in $JAVA_OPTS ; do
		case "$arg" in
			-Djetty.port=*)
				PORT="${arg##-Djetty.port=}"
				;;
		esac
	done
fi
if [ -z "$PORT" -a -f "$CONF_DIR/geoserver.properties" ] ; then
	PORT=$(sed -nr 's,^jetty\.port\s*=\s*([0-9]+)\s*$,\1,gp' "$CONF_DIR/geoserver.properties" || :)
fi
if [ -z "$PORT" -a -f "$HOME_DIR/start.ini" ] ; then
	PORT=$(sed -nr 's,^jetty\.port\s*=\s*([0-9]+)\s*$,\1,gp' "$HOME_DIR/start.ini" || :)
fi
if [ -z "$PORT" ] ; then
	PORT="$DFLT_PORT"
fi

if [ -z "$PORT" ] ; then
	echo "error: unable to determine jetty PORT number" >&2
	exit 1
fi

# Keep requesting main Geoserver page until successful
while ! curl -sSfL http://localhost:${PORT}${CONTEXT_PATH}/web >/dev/null 2>&1 ; do
	if ! [ -d /proc/$MAINPID ] ; then
		echo "MAINPID=$MAINPID exited unexpectedly" >&2
		exit 1
	fi
	sleep 1
done
exit 0

