#!/bin/bash

set -e

RESOURCES=
CONSTRAINTS=
PROGNAME="$(basename $0)"
[ -x /usr/bin/systemctl ] && SYSTEMD=1 || SYSTEMD=

# prnt out a log message
log_info() {
	echo "$*" >&2
}

# wrapper for systemctl/service command
svc() {
	local svc="$1"
	local cmd="$2"
	if [ "$SYSTEMD" = 1 ] ; then
		systemctl $cmd $svc >/dev/null 2>&1
	else
		service $cmd $svc >/dev/null 2>&1
	fi
}

# check permissions and services
check_prereq() {
	local p
	if [ "$(id -u)" -ne 0 ] ; then
		echo "this script must be run by root" >&2
		exit 1
	fi
	for p in xpath pcs crm_resource mktemp ; do
		if ! which $p >/dev/null 2>&1 ; then
			echo "this script requires \`$p' command to be installed" >&2
			exit 1
		fi
	done
	if ! svc pacemaker status ; then
		echo "pacemaker service not running" >&2
		exit 1
	fi
	if ! svc corosync status ; then
		echo "corosync service not running" >&2
		exit 1
	fi
}

# load resource and constraint names from running pacemaker
load_cib() {
	local cib_file
	cib_file="$(mktemp --suffix=.xml abms-cluster-XXXXXX)"
	trap "rm -f $cib_file" INT TERM HUP PIPE
	pcs cluster cib "$cib_file" --config
	RESOURCES="$(xpath "$cib_file" '/configuration/resources/*/@id' 2>/dev/null | xargs -n1 | sed 's,^id=,,g')"
	CONSTRAINTS="$(xpath "$cib_file" '/configuration/constraints/*/@id' 2>/dev/null | xargs -n1 | sed 's,^id=,,g')"
	rm -f "$cib_file"
	trap - INT TERM HUP PIPE
}

# return true if the given pacemaker resource is running
resource_is_running() {
	local rsc="$1"
	{ crm_resource --locate -r "$rsc" | grep -q ' is running on:' ; } >/dev/null 2>&1
}
	
# stop all pacemaker resources
stop_all_resources() {
	local rsc all_stopped
	all_stopped=1
	# for each resource
	for rsc in $RESOURCES ; do
		# if it's running, disable it (i.e., stop)
		if resource_is_running "$rsc" ; then
			log_info "disabling resource $rsc"
			pcs resource disable $rsc
			all_stopped=
		fi
	done
	# if any were running, wait till they actually stop
	if [ -z "$all_stopped" ] ; then
		while : ; do
			sleep 1
			all_stopped=1
			for rsc in $RESOURCES ; do
				if resource_is_running "$rsc" ; then
					pcs resource cleanup "$rsc" >/dev/null 2>&1 || :
					all_stopped=
				fi
			done
			[ -z "$all_stopped" ] || break
		done
	fi
}

# delete pacemaker configuration
delete_all_objects() {
	local id
	# constraints
	for id in $CONSTRAINTS ; do
		log_info "deleting constraint $id"
		pcs constraint delete $id >/dev/null
	done
	# resources
	for id in $RESOURCES ; do
		log_info "deleting resource $id"
		pcs resource delete $id >/dev/null
	done
	# delete operation history
	crm_resource -Q -C >/dev/null 2>&1 || :
}

# print out usage information and exit
usage() {
	cat <<END
Usage: $PROGNAME [OPTIONS...]
Stop and delete all pacemaker resources and constraints
 -h,--help           print this help and exit
END
	exit 0
}

# print out a command line error message and exit
cmdline_error() {
	echo "$PROGNAME: $*" >&2
	echo "Type \`$PROGNAME --help' for more info."
	exit 1
}

# process command line
init() {
	local quoted_args="$(getopt -n $PROGNAME -l help -o h -- "$@")"
	[ $? -eq 0 ] || cmdline_error
	eval "set -- $quoted_args"
	while [ "$#" -gt 0 ] ; do
		case "$1" in
			-h|--help)
				usage
				;;
			--)
				shift
				break
				;;
			*)
				break
				;;
		esac
	done
}

#########################

init "$@"
check_prereq
load_cib
stop_all_resources
delete_all_objects

