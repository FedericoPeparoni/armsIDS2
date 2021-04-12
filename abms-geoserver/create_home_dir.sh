#!/bin/bash

set -e

# globals
TOP_DIR="$(dirname "$0")"
TOP_DIR_ABS="$(readlink -f "$TOP_DIR")"
SRC_DIR="$TOP_DIR/src"
EXTRA_DIR="$SRC_DIR/extra"
: ${HOME_DIR:="$1"}
: ${HOME_DIR:="$TOP_DIR_ABS/.geoserver_home"}
HOME_DIR="$(readlink -f "$HOME_DIR")"
[ "$HOME_DIR" = "/" ] && { echo "Invalid HOME_DIR" >&2 ; exit 1 ; } || :

# source package.conf
. "$TOP_DIR/package.conf"

GS_BASE="$(basename "$GS_BIN_URL" | sed 's,-bin\.zip$,,g')"
GS_DIST_DIR="$EXTRA_DIR/$GS_BASE"

JETTY_BASE="$(basename "$JETTY_BIN_URL" | sed 's,\.tar\.gz$,,g')"
JETTY_DIST_DIR="$EXTRA_DIR/$JETTY_BASE"

EXTRA_JARS_DIR="$EXTRA_DIR/extra-jars"

LOGGING_MODULES_DIR="$(find "$EXTRA_DIR" -mindepth 1 -maxdepth 1 -type d -name "logging-modules-*")"

JETTY_WENAPP_LOGGING_DIR="$(find "$EXTRA_DIR" -mindepth 1 -maxdepth 1 -type d -name "jetty-webapp-logging-*")"

# create the GS home directory from pieces
rm -rf "$HOME_DIR"
# original GS home directory
cp -alr "$GS_DIST_DIR" "$HOME_DIR"
# replace "etc" and "start.ini" with our own files
rm -rf "$HOME_DIR/etc"
cp -afr "$SRC_DIR/jetty"/* "$HOME_DIR/"
# replace CORS config file "$HOME_DIR/etc/webapp/cors.web.xml" with the one from "$HOME_DIR/conf/", if one exists
if [ -f "$TOP_DIR/conf/cors.web.xml" ] ; then
	cp -af "$TOP_DIR"/conf/cors.web.xml "$HOME_DIR/etc/webapp/"
fi
# remove "https" module
rm -f "$HOME_DIR/modules/https.mod"
# add "plus" module
cp -rl "$JETTY_DIST_DIR/lib/"jetty-plus-*.jar "$HOME_DIR/lib/"
cp -l "$JETTY_DIST_DIR"/modules/plus.mod "$HOME_DIR/modules/"
# add "ext" module
cp -rl "$JETTY_DIST_DIR/lib/"ext "$HOME_DIR/lib/"
cp -l "$JETTY_DIST_DIR"/modules/ext.mod "$HOME_DIR/modules/"
# add "jndi" module
cp -rl "$JETTY_DIST_DIR/lib/"{jetty-jndi-*.jar,jndi} "$HOME_DIR/lib/"
cp -l "$JETTY_DIST_DIR"/modules/jndi.mod "$HOME_DIR/modules/"
# add additional JARs for JNDI/connection pooling
mkdir -p "$HOME_DIR/lib/ext"
cp -l "$EXTRA_JARS_DIR"/commons-{dbcp2,pool2}-*.jar "$HOME_DIR/lib/ext/"
# add jett-servlets-*.jar for the CORS filter
cp -l "$JETTY_DIST_DIR/lib/"jetty-servlets-*.jar "$HOME_DIR/webapps/geoserver/WEB-INF/lib/"
# install capture-all module
mkdir -p "$HOME_DIR/lib/logging"
cp -l "$EXTRA_JARS_DIR/jcl-over-slf4j"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/jul-to-slf4j"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/log4j-over-slf4j"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/logback-classic"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/logback-core"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/slf4j-api"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/janino"*.jar "$HOME_DIR/lib/logging"/
cp -l "$EXTRA_JARS_DIR/commons-compiler"*.jar "$HOME_DIR/lib/logging"/
cp -l "$LOGGING_MODULES_DIR/capture-all/logging.mod" "$HOME_DIR/modules/"
cp -l "$LOGGING_MODULES_DIR/capture-all/logback.xml" "$HOME_DIR/resources"/
cp -l "$LOGGING_MODULES_DIR/capture-all/jetty-logging.properties" "$HOME_DIR/resources"/
cp -l "$LOGGING_MODULES_DIR/capture-all/jetty-logging.xml" "$HOME_DIR/etc"/
cp -l "$LOGGING_MODULES_DIR/capture-all/logging.mod" "$HOME_DIR/modules"/
# install webapp-logging modules
mkdir -p "$HOME_DIR/lib/webapp-logging"
cp -l "$EXTRA_JARS_DIR/jetty-webapp-logging"*.jar "$HOME_DIR/lib/webapp-logging"/
cp -l "$JETTY_WENAPP_LOGGING_DIR/src/main/config/etc/jetty-webapp-logging.xml" "$HOME_DIR/etc"/
cp -l "$JETTY_WENAPP_LOGGING_DIR/src/main/config/etc/jetty-mdc-handler.xml" "$HOME_DIR/etc"/
cp -l "$LOGGING_MODULES_DIR/centralized/webapp-logging.mod" "$HOME_DIR/modules"/
# remove unused default data dir
rm -rf "$HOME_DIR/"data_dir/*
# remove any log files
rm -f "$HOME_DIR/"logs/{*.log,*.log.*}
