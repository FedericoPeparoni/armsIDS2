#!/bin/bash

#
# This script is meant to be called from a cron job, /etc/cron.d/abms-backup.
#
# Create a backup in /var/lib/abms/backup, delete old backups and temporary
# files and then push the entire backup directory to the peer server using
# rsync (over ssh). Do all that only if system service "abms-backup" is
# started; otherwise do nothing.
#
# In cluster environments the backup service will be started only on one
# of the servers, where postgres is running as master.
#

export PATH=/sbin:/bin:/usr/sbin:/usr/bin

abms-backup --with-service --create --cleanup --push --log-timestamps >>/var/log/abms/backup/backup.log 2>&1

