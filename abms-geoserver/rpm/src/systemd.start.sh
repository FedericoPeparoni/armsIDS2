#!/bin/bash

set -e

exec /usr/share/abms/geoserver/jetty/bin/run.sh -Dlogging.consoleEnabled=false -Dlogging.journaldEnabled=true -Dlogging.fileEnabled=true

