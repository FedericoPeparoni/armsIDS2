#!/bin/bash

set -e

PROGNAME="$(basename "$0")"
NEW_PACKAGES_FOUND=0
FORCE_RESTART=0
IGNORE_RUNNING_PACEMAKER=0

# log functions
notice() {
	if [[ -t 1 ]] ; then
		echo -e "\\033[34;1m>>> $*\\033[0m"
	else
		echo -e ">>> $*"
	fi
}
error() {
	if [[ -t 1 ]] ; then
		echo -e "\\033[31;1mERROR\\033[0m: $*"
	else
		echo -e "ERROR: $*"
	fi
}
fatal() {
	error "$@"
	exit 1
}
warning() {
	if [[ -t 1 ]] ; then
		echo -e "\\033[35mWARNING\\033[0m: $*"
	else
		echo -e "WARNING: $*"
	fi
}

# make sure we are running as root
ensure_root() {
	if [[ "$(id -u)" -ne 0 ]] ; then
		fatal "this script must be run by root" >&2
	fi
}

# make sure pacemaker is not running
ensure_pacemaker_not_running() {
	if [[ $IGNORE_RUNNING_PACEMAKER == 0 ]] ; then
		if systemctl -q status pacemaker >/dev/null 2>&1 ; then
			fatal "pacemaker is running; re-run with \`--ignore-running-pacemaker' to suppress this error (not recommended)"
		fi
	fi
}

# check whether there are any updates available
check_updates() {
	local rc out
	yum -q --disablerepo='*' --enablerepo='abms-*' clean all >/dev/null 2>&1
	set +e
	yum -q --disablerepo='*' --enablerepo='abms-*' check-update
	rc="$?"
	set -e
	# yum returns 100 when new packages are available; return true
	if [[ $rc -eq 100 ]] ; then
		return 0
	# "0" = no new packages available; return false
	elif [[ $rc -eq 0 ]] ; then
		return 1
	# yum failed
	else
		exit 1
	fi
}

# handle command line errors
cmdline_error() {
	local msg="$1"
	if [[ -n $msg ]] ; then
		error "$msg" >&2
	fi
	echo "Type \`$PROGNAME --help' for more info." >&2
	exit 7
}

# print out an error message and exit
usage() {
	cat <<_END
Usage: $PROGNAME [OPTIONS...]
Update and restart ABMS services
 -h,--help            print this message and exit
 -R,--force-restart   restart services even if there are no updates
 -I,--ignore-running-pacemaker
                      update and/or restart services even if pacemaker is
                      running (dangerous)
_END
	exit 0
}

# process command line
ACTION="action_setup"
init() {
	quoted_args="$(getopt -n $PROGNAME -l help,force-restart,ignore-running-pacemaker -o hRI -- "$@")"
	[[ $? -eq 0 ]] || cmdline_error
	eval "set -- $quoted_args"
	while [[ $# -gt 0 ]] ; do
		case "$1" in
			-h|--help)
				usage
				;;
			-R|--force-restart)
				FORCE_RESTART=1
				;;
			-I|--ignore-running-pacemaker)
				IGNORE_RUNNING_PACEMAKER=1
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
	[[ $# -gt 0 ]] && cmdline_error "too many arguments" || :
}

###################################################

init "$@"
ensure_root
ensure_pacemaker_not_running
notice "checking for new packages"
check_updates && NEW_PACKAGES_FOUND=1 || :

# download updates
if [[ $NEW_PACKAGES_FOUND == 1 ]] ; then
	notice "downloading new packages"
	yum update --downloadonly -y
fi

# if there are no new packages, print out a warning
if [[ $NEW_PACKAGES_FOUND != 1 ]] ; then
	if [[ $FORCE_RESTART == 1 ]] ; then
		warning "no new packages found"
	else
		warning "no new packages found; use --force-restart to restart services anyway"
	fi
fi

# if there are new packages or --force-restart were given: stop the services
if [[ $NEW_PACKAGES_FOUND == 1 || $FORCE_RESTART == 1 ]] ; then
	notice "stopping services"
	systemctl stop abms-api abms-geoserver pghelper
fi

# update RPMs
if [[ $NEW_PACKAGES_FOUND == 1 ]] ; then
	notice "installing updates"
	yum update -y
fi

# start pghelper if necessary
if ! systemctl is-active pghelper >/dev/null ; then
	notice "starting database"
	systemctl start pghelper
fi

# create/update abms-db
notice "creating/upgrading abms-db"
abms-db-setup >/dev/null || fatal "failed to create/upgrade abms database"

# find out names & versions of the installed navdb* packages
NAVDB_PACKAGE=$(rpm -q --qf '%{name}\n' --whatprovides navdb | grep -E -v '\s' | head -n 1 || :)
NAVDB_DATA_PACKAGE=$(rpm -q --qf '%{name}\n' --whatprovides navdb-data | grep -E -v '\s' | head -n 1 || :)
if [[ -n $NAVDB_PACKAGE && -n $NAVDB_DATA_PACKAGE ]] ; then
	NAVDB_DATA_INSTALLED_VERSION=$(rpm -q --qf '%{epoch}:%{version}-%{release}' $NAVDB_DATA_PACKAGE 2>/dev/null || :)
	if [[ -n $NAVDB_DATA_INSTALLED_VERSION ]] ; then
		# Find out the last version of navdb-data package that we previously successfully
		# loaded into the navdb database (via "navdb setup" below).
		NAVDB_DATA_LAST_LOADED_VERSION=$(cat /var/lib/abms/navdb/navdb_data.last.loaded.version 2>/dev/null || :)
		# if currently installed navdb is different from previously-loaded navdb, then drop it
		if [[ $NAVDB_DATA_INSTALLED_VERSION != $NAVDB_DATA_LAST_LOADED_VERSION ]] ; then
			notice "destroying obsolete navdb database ..."
			navdb drop --force
		fi
		# alsways run navdb setup; this will either create it from scratch because we just dropped it,
		# or it hasn't been created at all yet; otherwise, in case sombeody restored
		# an old(-er) navdb backup, then "navdb setup" will upgrade it's schema (table structure,
		# etc) in place.
		notice "creating/upgrading navdb (RPM version $NAVDB_DATA_INSTALLED_VERSION)"
		navdb setup
		mkdir -p "/var/lib/abms/navdb"
		echo $NAVDB_DATA_INSTALLED_VERSION >"/var/lib/abms/navdb/navdb_data.last.loaded.version"
	fi
fi

# find out names & versions of the installed geoserver-data package
GEOSERVER_DATA_PACKAGE=$(rpm -q --qf '%{name}\n' --whatprovides abms-geoserver-data | grep -E -v '\s' | head -n 1 || :)
if [[ -n $GEOSERVER_DATA_PACKAGE ]] ; then
	GEOSERVER_DATA_INSTALLED_VERSION=$(rpm -q --qf '%{epoch}:%{version}-%{release}' $GEOSERVER_DATA_PACKAGE 2>/dev/null || :)
	if [[ -n $GEOSERVER_DATA_INSTALLED_VERSION ]] ; then
		# Find out the last version of geoserver-data package that we previously successfully
		# loaded into the navdb database (via "abms-gstool --reset" below).
		GEOSERVER_DATA_LAST_LOADED_VERSION=$(cat /var/lib/abms/geoserver/geoserver_data.last.loaded.version 2>/dev/null || :)
		# if currently installed navdb is different from previously-loaded geoserver-data, then reset it
		if [[ $GEOSERVER_DATA_INSTALLED_VERSION != $GEOSERVER_DATA_LAST_LOADED_VERSION ]] ; then
			notice "resetting geoserver configuration ..."
			abms-gstool --reset --force
			mkdir -p "/var/lib/abms/geoserver"
			echo $GEOSERVER_DATA_INSTALLED_VERSION >"/var/lib/abms/geoserver/geoserver_data.last.loaded.version"
		fi
	fi
fi


# start services
for s in abms-{api,geoserver} ; do
	if ! systemctl is-active $s >/dev/null ; then
		notice "starting $s ..."
		systemctl start $s
	fi
done

# print out package versions and build times
notice "Now using:"
echo
echo "+----------------------+-----------------+--------------------------------+"
echo "| Package              | Version         | Compiled on                    |"
echo "+----------------------+-----------------+--------------------------------+"
for p in $NAVDB_PACKAGE $NAVDB_DATA_PACKAGE abms-{api,web,geoserver} ; do
	build_time=$(rpm -q --qf '%{BUILDTIME}' $p 2>/dev/null)
	build_time_str=$(date -d "@$build_time")
	ver=$(rpm -q --qf '%{VERSION}' $p 2>/dev/null)
	if [[ -n $build_time ]] ; then
		printf '| %-20s | %-15s | %-30s |\n' $p "$ver" "$build_time_str"
	fi
done
echo "+----------------------+-----------------+--------------------------------+"
exit 0


