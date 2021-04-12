#!/bin/bash

#
# This script is meant to be called from a cron job, /etc/cron.d/abms-backup.
#
# Copy any missing backups from peer server to local server, then remove
# any old backups and temporary files. Do all that, only when the system
# service called "abms-backup" is stopped; if that service is running do
# nothing. This allows us to catch up with missing backups in case the local
# server was down for a while.
#
# In cluster environments the backup service will be started only on one
# of the servers, where postgres is running as master.
#

export PATH=/sbin:/bin:/usr/sbin:/usr/bin

abms-backup --without-service --pull --cleanup --log-timestamps >>/var/log/abms/backup/backup.log 2>&1
