#!/bin/bash

set -e
PROGNAME="$(basename $0)"
WANT_PRINT=
WANT_APPLY=
WANT_CHECK=
CIB_FILE=
CMD_FILE=
CONF_FILE="/etc/abms/cluster/setup.conf"

# These are defined in CONF_FILE
declare DB_NODES WEB_NODES DB_HOST WEB_HOST

# Floating IP addresses that correspond to DB_HOST and WEB_HOST
declare DB_IP WEB_IP

# Check if we are using SysV or SystemD
[ -x /usr/bin/systemctl ] && SYSTEMD=1 || SYSTEMD=

# print out an error message
error() {
	echo "ERROR: $*" >&2
}

# print out an error message and exit
fatal() {
	error "$@"
	exit 1
}

# Wraper for the service/systemctl command
svc() {
	local svc="$1"
	local cmd="$2"
	if [ "$SYSTEMD" = 1 ] ; then
		systemctl $cmd $svc >/dev/null 2>&1
	else
		service $cmd $svc >/dev/null 2>&1
	fi
}

# check permssions & running services
check_prereq() {
	if [ "$(id -u)" -ne 0 ] ; then
		fatal "this script must be run by root"
	fi
}

# make sure corosync & pacemaker are running
check_pcmk_prereq() {
	if ! svc pacemaker status ; then
		fatal "pacemaker is not running"
	fi
	if ! svc corosync status ; then
		fatal "corosync is not running"
	fi
}

# read configuration from /etc/abms/cluster/setup.conf
read_config() {
	local file var ip

	# clear config options
	DB_NODES=
	WEB_NODES=
	DB_IP=
	WEB_IP=
	ALL_NODES=

	# read configuration
	. "$CONF_FILE" || exit 1

	# make sure all options are not empty
	for var in DB_NODES WEB_NODES DB_HOST WEB_HOST ; do
		if [ -z "${!var}" ] || ! { echo "${!var}" | egrep -qv '^[[:space:]]*$' ; } ; then
			fatal "$CONF_FILE: missing or empty $var"
		fi
	done

	# make sure DB_NODES and WEB_NODES don't contain repeated host names
	for var in {DB,WEB}_NODES ; do
		if [[ $(echo ${!var} | wc -w) -ne $(echo ${!var} | xargs -n1 | LC_ALL=C sort -u | xargs | wc -w) ]] ; then
			fatal "$CONF_FILE: $var: contains duplicate host names"
		fi
	done

	# resolve all host name to IP addresses (from /etc/hosts)
	local all_hosts=$(echo $DB_NODES $WEB_NODES $DB_HOST $WEB_HOST | xargs -n1 | LC_ALL=C sort -u | xargs)
	local host addr
	local -A host_map
	for host in $all_hosts ; do
		addr=$({ getent ahostsv4 "$host" 2>/dev/null | awk '/ STREAM /{print $1}' | head -n1 ; } || :)
		if [[ -z "$addr" ]] ; then
			fatal "$CONF_FILE: host name \`$host' can't be resolved to an IP address"
		fi
		if [[ $addr =~ ^127.* || $addr == 0.0.0.0 || $addr == 255.255.255.0 ]] ; then
			fatal "$CONF_FILE: host name \`$host' resolves to an invalid or unsupported IP address \`$addr'"
		fi
		host_map[$host]=$addr
	done
	
	# make sure host names within each of DB_NODES and WEB_NODES don't map to the same addresses
	local -A tmp_addr_map
	for var in {DB,WEB}_NODES ; do
		tmp_addr_map=()
		for host in ${!var} ; do
			addr=${host_map[$host]}
			if [[ -n ${tmp_addr_map[$addr]} ]] ; then
				fatal "$CONF_FILE: one or more physical host names resolve to the same IP address"
				exit 1
			fi
			tmp_addr_map[$addr]=$host
		done
	done

	# set {DB,WEB}_IP variables to ip addresses of {DB,WEB}_HOST names
	for var in {DB,WEB}_HOST ; do
		host=${!var}
		eval "${var%%_HOST}_IP=${host_map[$host]}"
	done

	# make sure floating IP names and addresses are different from physical node names and addresses
	local floating_host_var floating_host
	local node_list_var node_list
	local physical_host
	for floating_host_var in {DB,WEB}_HOST ; do
		floating_host=${!floating_host_var}
		for node_list_var in {DB,WEB}_NODES ; do
			node_list=${!node_list_var}
			for physical_host in $node_list ; do
				if [[ $floating_host == $physical_host ]] ; then
					fatal "$CONF_FILE: floating host name $floating_host_var \`$floating_host' is already used as a physical host name in $node_list_var"
				fi
				if [[ ${host_map[$floating_host]} == ${host_map[$physical_host]} ]] ; then
					fatal "$CONF_FILE: floating host name $floating_host_var \`$floating_host' resolves to the same address as physical host name \`$physical_host' (${host_map[$floating_host]})"
				fi
			done
		done
	done

	# make sure DB and WEB host names and addresses are not the same
	if [[ $DB_HOST == $WEB_HOST ]] ; then
		fatal "$CONF_FILE: DB_HOST and WEB_HOST must be different"
	fi
	if [[ ${host_map[$DB_HOST]} == ${host_map[$WEB_HOST]} ]] ; then
		fatal "$CONF_FILE: DB_HOST and WEB_HOST resolve to the same IP address"
	fi

	# sort node names & remove duplicates
	DB_NODES="$(echo $DB_NODES | xargs -n1 | sort -u | xargs)"
	WEB_NODES="$(echo $WEB_NODES | xargs -n1 | sort -u | xargs)"
	ALL_NODES="$(echo $DB_NODES $WEB_NODES | xargs -n1 | sort -u | xargs)"
}

# helper function: generate resource location constraints
pcs_location_constraints() {
	local resource="$1"
	local node weight
	let weight=10+$#-2
	shift
#	echo
	for node in $* ; do
		echo "pcs \$PCS_OPTS constraint location add $resource-on-$node $resource $node $weight"
		let weight=$weight-1
	done
	echo
}

# generate a shell script for initializing the cluster using pcs
generate_pcs_config() {

	local db_nodes_count="$(echo $DB_NODES | wc -w)"
	local web_nodes_count="$(echo $WEB_NODES | wc -w)"

	# global options
	cat <<END

###################################################
# global options
###################################################
pcs \$PCS_OPTS property set no-quorum-policy=ignore
pcs \$PCS_OPTS property set stonith-enabled=false
pcs \$PCS_OPTS property set symmetric-cluster=false
pcs \$PCS_OPTS resource defaults resource-stickiness=100

END

	# pghelper
	cat <<END
###################################################
# pghelper
###################################################
pcs \$PCS_OPTS resource create pghelper ocf:ids:pghelper \\
	op start   timeout=30m \\
	op stop    timeout=10m \\
	op monitor timeout=30s interval=10s role=Master \\
	op monitor timeout=30s interval=11s role=Slave \\
	op promote timeout=30s \\
	op demote  timeout=120s \\
	op notify  timeout=30m

pcs \$PCS_OPTS resource master pghelper-ms pghelper \\
	meta \\
		master-max=1 \\
		master-node-max=1 \\
		clone-max=$DB_NODES_COUNT \\
		clone-node-max=1 \\
		notify=true \\
		migration-threshold=1 \\
		target-role=Master
		
END
	pcs_location_constraints "pghelper-ms" $DB_NODES

	# dbip
	cat <<END
###################################################
# dbip
###################################################
pcs \$PCS_OPTS resource create dbip ocf:heartbeat:IPaddr2 \\
	ip=$DB_IP \\
	cidr_netmask=32 \\
	op monitor interval=30s
	
pcs \$PCS_OPTS constraint colocation add dbip with master pghelper-ms INFINITY
pcs \$PCS_OPTS constraint order promote pghelper-ms then start dbip kind=Mandatory
END
	pcs_location_constraints "dbip" $DB_NODES
	
	# api
	cat <<END
###################################################
# api
###################################################
pcs \$PCS_OPTS resource create api systemd:abms-api \\
	op start timeout=5m \\
	op stop timeout=2m \\
	op monitor timeout=10s interval=5s

pcs \$PCS_OPTS resource clone api \\
	meta \\
		clone-max=$DB_NODES_COUNT \\
		clone-node-max=1 \\
		migration-threshold=2 \\
		failure-timeout=0

END
	pcs_location_constraints "api-clone" $DB_NODES
	
	# geoserver
	cat <<END
###################################################
# geoserver
###################################################
pcs \$PCS_OPTS resource create geoserver systemd:abms-geoserver \\
	op start timeout=5m \\
	op stop timeout=2m \\
	op monitor timeout=10s interval=5s

pcs \$PCS_OPTS resource clone geoserver \\
	meta \\
		clone-max=2 \\
		clone-node-max=1 \\
		migration-threshold=2 \\
		failure-timeout=0m
END
	pcs_location_constraints "geoserver-clone" $DB_NODES

	# webip
	cat <<END
###################################################
# webip
###################################################
pcs \$PCS_OPTS resource create webip ocf:heartbeat:IPaddr2 \\
	ip=$WEB_IP \\
	cidr_netmask=32 \\
	op monitor interval=30s
	
pcs \$PCS_OPTS constraint colocation add webip with api-clone 100
END
	pcs_location_constraints "webip" $WEB_NODES
	
	# backup
	cat <<END
###################################################
# backup
###################################################
pcs \$PCS_OPTS resource create backup systemd:abms-backup \\
	op start timeout=10s \\
	op stop timeout=10s \\
	op monitor timeout=10s interval=5s

pcs \$PCS_OPTS constraint colocation add backup with master pghelper-ms 100
END
	pcs_location_constraints "backup" $DB_NODES

	# x400
	cat <<END
###################################################
# x400
###################################################
pcs \$PCS_OPTS resource create --disabled x400 systemd:abms-x400 \\
	op start timeout=30s \\
	op stop timeout=30s \\
	op monitor timeout=10s interval=5s \\
	meta migration-threshold=2

pcs \$PCS_OPTS constraint colocation add x400 with master pghelper-ms 100
END
	pcs_location_constraints "x400" $DB_NODES

	# misc
	cat <<END
###################################################
# miscellaneous constraints
###################################################
#pcs \$PCS_OPTS constraint order set dbip api-clone geoserver-clone sequential=true require-all=true
pcs \$PCS_OPTS constraint order start dbip then start api-clone kind=Mandatory
pcs \$PCS_OPTS constraint order start dbip then start x400 kind=Mandatory
pcs \$PCS_OPTS constraint order start api-clone then start geoserver-clone kind=Mandatory

END
}


# remove temp files
cleanup() {
	rm -f $CIB_FILE $CMD_FILE
}

# generate configuration and uploaded into running pacemaker
replace_pcmk_config() {
	local resources constraints
	trap cleanup INT TERM HUP PIPE EXIT

	# save current CIB in a temp file
	CIB_FILE="$(mktemp --tmpdir --suffix=.xml abms-cluster-XXXXXX)"
	pcs cluster cib "$CIB_FILE"

	# make sure there are no resources or constraints defined
	resources="$(xpath "$CIB_FILE" '/cib/configuration/resources/*/@id' 2>/dev/null | xargs -n1 | sed 's,^id=,,g')"
	constraints="$(xpath "$CIB_FILE" 'cib//configuration/constraints/*/@id' 2>/dev/null | xargs -n1 | sed 's,^id=,,g')"
	if [ "$(echo $resources $constraints | wc -w)" -gt 0 ] ; then
		rm -f "$CIB_FILE"
		fatal "pacemaker already configured, please delete old configuration first using \`abms-cluster-reset' command"
	fi

	# generate a shell script with pcs commands
	CMD_FILE="$(mktemp --tmpdir --suffix=.sh abms-cluster-XXXXXX)"
	( echo "set -e" ; generate_pcs_config ; ) >"$CMD_FILE"

	# run it against the cib file
	PCS_OPTS="-f $CIB_FILE" $BASH $CMD_FILE #>/dev/null

	# upload updated CIB file to pacemaker
	pcs cluster cib-push "$CIB_FILE"

	# reset signal handlers
	trap - INT TERM HUP PIPE EXIT

	# remove temp files
	cleanup
}


# print out usage information and exit
usage() {
	cat <<END
Usage: $PROGNAME [OPTIONS...]
Define pacemaker resources and constraints for running ABMS
 -h,--help           print this help and exit
    --print          print out a shell script with configuration commands
    --check          validate $CONF_FILE and exit (default)
    --apply          apply configuration to running pacemaker
END
	exit 0
}

# print out a command line error message and exit
cmdline_error() {
	error "$PROGNAME: $*"
	echo "Type \`$PROGNAME --help' for more info." >&2
	exit 1
}

# process command line
init() {
	local quoted_args="$(getopt -n $PROGNAME -l help,print,check,apply -o h -- "$@")"
	[[ $? -eq 0 ]] || cmdline_error
	eval "set -- $quoted_args"
	while [[ $# -gt 0 ]] ; do
		case "$1" in
			--help|-h)
				usage
				;;
			--print)
				WANT_PRINT=1
				;;
			--check)
				WANT_CHECK=1
				;;
			--apply)
				WANT_APPLY=1
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
}

init "$@"
check_prereq
read_config
if [[ -z $WANT_CHECK && -z $WANT_PRINT && -z $WANT_APPLY ]] ; then
	WANT_CHECK=1
fi
if [[ -n $WANT_CHECK ]] ; then
	echo "$CONF_FILE: configuration is valid" >&2
	exit 0
fi
if [[ -n $WANT_PRINT ]] ; then
	generate_pcs_config
	exit 0
fi
if [[ -n $WANT_APPLY ]] ; then
	check_pcmk_prereq
	replace_pcmk_config
	exit 0
fi
fatal "internal error: unreachable code"


