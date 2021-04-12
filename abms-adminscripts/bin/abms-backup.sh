#!/bin/bash
# vim:ts=8:sts=8:sw=8:noet

# These may be overridden in $CONFIG_FILE
SSH="ssh -o BatchMode=yes -o ConnectTimeout=5 -o ServerAliveCountMax=3 -o ServerAliveInterval=10 -o KeepAlive=yes -o TCPKeepAlive=yes"
RSYNC="rsync"
MAX_BACKUP_COUNT="60"
MAX_BACKUP_TOTAL_SIZE="100GB"
TIMESTAMP=$(date --utc '+%Y%m%d%H%M%SZ')
BACKUP_DIR="/var/lib/abms/backup"
BACKUP_PREFIX="ARMS_"
BACKUP_NAME="${BACKUP_PREFIX}${TIMESTAMP}"
DB_BACKUP_FILE="$BACKUP_NAME.dat"
DB_NODES=

# Other vars
PROGNAME=$(basename "$0")
CONFIG_FILE=
DFLT_CONFIG_FILE="/etc/abms/backup/backup.conf"
VERBOSE=
PG_MASTER_HOST=
XTRACE=
EXPORT_TARGET_DIR=
SVC_NAME="abms-backup"
SVC_CHECK=
LOG_TIMESTAMPS=

# actions -- these variables will be set to "1" when corresponding actions are
# requested on command line
ACTION_CREATE=
ACTION_PUSH=
ACTION_PULL=
ACTION_CLEANUP=
ACTION_EXPORT=

# check whether printf supports timestamps (older versions of Bash don't)
PRINTF_SUPPORTS_TIMESTAMPS=$(printf '%(%Y-%m-%d %H:%M:%S%z)T' -1 >/dev/null 2>&1 && echo 1 || :)

# Log functions
do_log() {
	if [[ $LOG_TIMESTAMPS == 1 ]] ; then
		if [[ $PRINTF_SUPPORTS_TIMESTAMPS == 1 ]] ; then
			printf '[%(%Y-%m-%d %H:%M:%S%z)T] %s\n' -1 "$@" >&2
		else
			printf '[%s] %s\n' "$(date +'%Y-%m-%d %H:%M:%S%z')" "$@" >&2
		fi
	else
		echo "$@" >&2
	fi
}
log_error() {
	do_log "error: " "$@"
}
log_warning() {
	do_log "warning: $@"
}
log_notice() {
	do_log "$@"
}
log_info() {
	if [[ -n $VERBOSE ]] ; then
		do_log "$@"
	fi
}

# Display a config error message and exit
cfg_error() {
	log_error "$@"
	exit 3
}

# Display an command line error message and exit
cmdline_error() {
	if [[ $# -gt 0 ]] ; then
		echo "error: $*" >&2
	fi
	echo "Type \`$PROGNAME --help' for more info." >&2
	exit 2
}

# Make sure directory exists and we have read, write and execute permissions to it
ensure_dir_all_access() {
	[[ -e "$1" ]] || fatal "$1: directory doesn't exist"
	[[ -d "$1" ]] || fatal "$1: not a directory"
	[[ -r "$1" && -w "$1" && -x "$1" ]] || fatal "$1: permission denied"
}

# Make sure backup dir exists and we have full access to it
validate_backup_dir() {
	ensure_dir_all_access "$BACKUP_DIR"
}

# Display a fatal error message and exit
fatal() {
	log_error "$@"
	exit 2
}

# Return true if the given host name refers to the local host
is_local_host() {
	[[ $1 == $(hostname) || $1 == $(hostname -s) || $1 == $(hostname -f) ]]
}

# Find the host where pghelper is running in master mode
find_pg_master() {
	if [[ -z $PG_MASTER_HOST ]] ; then
		local host is_master_cmd="/sbin/runuser -c \"psql -t -c 'select pg_is_in_recovery()'\" - postgres | head -n 1 | egrep -q '^\\s*f\\s*' >/dev/null"
		# check each db node, starting with local host name
		for host in $(reorder_hostnames $DB_NODES) ; do
			# try to resolve host name
			getent hosts $host >/dev/null || fatal "unknown database host name \`$host'"
			# if it's a remote host
			if ! is_local_host "$host" ; then
				# try to ping it
				ping -c 1 -i 0.2 -w 0.5 $host >/dev/null 2>&1 || continue
				# try to ssh to it
				$SSH $host : || fatal "unable to SSH to \`$host' in batch mode"
				# check if it's running in normal mode
				$SSH $host "$is_master_cmd" >/dev/null 2>&1 || continue
			# local host -- run the check locally
			else
				$SHELL -c "$is_master_cmd" >/dev/null 2>&1 || continue
			fi
			# done
			PG_MASTER_HOST="$host"
			log_info "found postgres master \`$host'"
			return 0
		done
		fatal "unable to determine hostname of postgres master"
	fi
	return 0
}

# Reorder a list of host names so that the local host comes first
reorder_hostnames() {
	local h local_host
	local -a new_list
	for h in $* ; do
		if is_local_host $h ; then
			local_host=$h
			continue
		fi
		new_list+=($h)
	done
	if [[ -n $local_host ]] ; then
		echo $local_host ${new_list[*]}
	else
		echo $*
	fi
}

# Read and validate configuration from $CONFIG_FILE
load_config() {
	local filename

	# work out config file name
	if [[ -n $CONFIG_FILE ]] ; then
		filename="$CONFIG_FILE"
	elif [[ -f $DFLT_CONFIG_FILE ]] ; then
		filename="$DFLT_CONFIG_FILE"
	fi

	# source it
	if [[ -n $filename ]] ; then
		log_info "loading configuration from \`$CONFIG_FILE'"
		. "$filename" || exit 1
	fi

	# SSH
	if [[ -z $SSH ]] ; then
		cfg_error "$filename: invalid empty SSH key"
	fi

	# RSYNC
	if [[ -z $RSYNC ]] ; then
		cfg_error "$filename: invalid empty RSYNC key"
	fi

	# MAX_BACKUP_COUNT
	if ! [[ $MAX_BACKUP_COUNT =~ ^[0-9]{1,5}$ ]] ; then
	       cfg_error "$filename: invalid MAX_BACKUP_COUNT \`$MAX_BACKUP_COUNT', expecting integer [0..99999]"
	fi

	# MAX_BACKUP_TOTAL_SIZE
	if ! [[ $MAX_BACKUP_TOTAL_SIZE =~ ^[1-9][0-9]{0,8}(K|KB|M|MB|G|GB)?$ ]] ; then
		cfg_error "$filename: invalid MAX_BACKUP_TOTAL_SIZE \`$MAX_BACKUP_TOTAL_SIZE', expecting integer optionally followed by KB, MB or GB"
	fi

	# BACKUP_DIR
	if [[ -z $BACKUP_DIR ]] ; then
		cfg_error "$filename: invalid empty BACKUP_DIR key"
	fi

	# DB_NODES
	if [[ -z $DB_NODES ]] ; then
		if [[ -f /etc/abms/cluster/setup.conf ]] ; then
			log_info "loading DB_NODES from /etc/abms/cluster/setup.conf"
			DB_NODES=$( . /etc/abms/cluster/setup.conf && echo "$DB_NODES" ) || exit 1
		fi
		if [[ -z $DB_NODES ]] ; then
			DB_NODES=$(hostname -s) || exit 1
			[[ -n $DB_NODES ]] || fatal "unable to determine database host names"
		fi
	fi
	local host local_host_found
	for host in $DB_NODES ; do
		if is_local_host $host ; then
			local_host_found=1
			break
		fi
	done
	[[ -n $local_host_found ]] || fatal "invalid configuration: DB_NODES must include current host name"

	# done
	log_info "loaded configuration:"
	log_info "    SSH=\"$SSH\""
	log_info "    RSYNC=\"$RSYNC\""
	log_info "    MAX_BACKUP_COUNT=\"$MAX_BACKUP_COUNT\""
	log_info "    MAX_BACKUP_TOTAL_SIZE=\"$MAX_BACKUP_TOTAL_SIZE\""
	log_info "    BACKUP_DIR=\"$BACKUP_DIR\""
	log_info "    DB_NODES=\"$DB_NODES\""

}

# create a backup in the given directory
create_backup() {
	local dir="$1" target_dir="$2"

	find_pg_master

	stat "$dir" >/dev/null
	rm -f "$dir/$DB_BACKUP_FILE"
	log_info "creating $target_dir/$DB_BACKUP_FILE from master database running on host \`$PG_MASTER_HOST'"
	if is_local_host "$PG_MASTER_HOST" ; then
		if ! abms-db-dump -Z 9 - >"$dir/$DB_BACKUP_FILE" ; then
			rm -f "$dir/$DB_BACKUP_FILE"
			exit 1
		fi
	else
		if ! ssh "$PG_MASTER_HOST" "abms-db-dump -Z 9 -" >"$dir/$DB_BACKUP_FILE" ; then
			rm -f "$dir/$DB_BACKUP_FILE"
			exit 1
		fi
	fi
	log_info "creating checksum \`$DB_BACKUP_FILE.sha256'"
	if ! ( cd "$dir" && sha256sum "$DB_BACKUP_FILE" >"$DB_BACKUP_FILE.sha256" ; ) ; then
		rm -f "$dir/$DB_BACKUP_FILE"
		rm -f "$dir/$DB_BACKUP_FILE.sha256"
		exit 1
	fi
}

# create backup in the system directory
create_system_backup() {
	# create a backup in a .tmp subdir of the system backup dir; then rename it
	local dir="$BACKUP_DIR/$BACKUP_NAME"
	local tmp_dir="$dir.$$.tmp"
	trap "rm -rf \"$tmp_dir\" ; trap - EXIT ; exit 1" EXIT INT TERM PIPE HUP
	mkdir "$tmp_dir"
	create_backup "$tmp_dir" "$dir"
	mv -Tn "$tmp_dir" "$dir"
	trap - EXIT INT TERM PIPE HUP
	log_notice "created new backup in directory \`$dir'"
}

# create a backup in a directory specified on the command line
export_backup() {
	create_backup "$EXPORT_TARGET_DIR" "$EXPORT_TARGET_DIR"
}

# convert a size value, such as "4K" to bytes; e.g. "4K" => 4096
convert_size() {
	local value factor=1
	case "$1" in
		*[kK]|*[kK][bB]) let factor=1024 ;;
		*[mM]|*[mM][bB]) let factor=1024*1024 ;;
		*[gG]|*[gG][bB]) let factor=1024*1024*1024 ;;
	esac
	value=$(echo $1 | sed -r 's,[a-z]+,,gi')
	echo "$value * $factor" | bc
}

# remove old backups and temp files from system backup directory
cleanup_system_backups() {
	log_info "removing old backups from \`$BACKUP_DIR'"

	# resolve any symlinks in BACKUP_DIR
	local backup_dir
	if backup_dir=$(readlink -e "$BACKUP_DIR") ; then

		# refuse to process "/"
		if [[ $backup_dir == / ]] ; then
			log_error "invalid backup directory"
		else

			# remove "old" temporary directores
			find "$BACKUP_DIR" -mindepth 1 -maxdepth 1 -type d -name "${BACKUP_PREFIX}[0-9]*.tmp" -mmin 60 | while read dir ; do
				log_notice "removing \`$dir'"
				rm -rf "$dir"
			done
			
			# remove backup dirs that exceed configuration limits
			local count=0
			local total_size=0
			local total_size_max=`convert_size "$MAX_BACKUP_TOTAL_SIZE"`
			find "$BACKUP_DIR" -mindepth 1 -maxdepth 1 -type d -name "${BACKUP_PREFIX}[0-9]*" | sort -r | while read dir ; do
				let count="$count+1"
				size=`du -s --block-size=1 $dir | awk '{print $1}'`
				[ $? -eq 0 ] || break
				total_size=$(echo "$total_size + $size" | bc)
				[ $? -eq 0 ] || break
				if [[ $count -gt $MAX_BACKUP_COUNT || $(echo "$total_size > $total_size_max" | bc) -eq 1 ]] ; then
					log_notice "removing \`$dir'"
					rm -rf "$dir"
					continue
				fi
			done
		fi
	fi

}

# push backups to peer
push_to_peer() {
	local host done
	
	# find first non-local DB host and push local backup dir to it using rsync
	for host in $DB_NODES ; do
		if ! is_local_host $host ; then
			log_notice "pushing \`$BACKUP_DIR' to $host"
			RSYNC_RSH="$SSH" $RSYNC -arH --ignore-existing --exclude '*.tmp' $BACKUP_DIR/ $host:$BACKUP_DIR/
			done=1
			break
		fi
	done
	[[ -n $done ]] || log_warning "no peer servers defined"
}

# pull backups from peer
pull_from_peer() {
	local host done
	
	# find first non-local DB host and pull local backup dir from it using rsync
	for host in $DB_NODES ; do
		if ! is_local_host $host ; then
			log_notice "pulling \`$BACKUP_DIR' from $host"
			RSYNC_RSH="$SSH" $RSYNC -arH --ignore-existing --exclude '*.tmp' $host:$BACKUP_DIR/ $BACKUP_DIR/
			done=1
			break
		fi
	done
	[[ -n $done ]] || log_warning "no peer servers defined"
}

# print out usage information and exit
usage() {
	cat <<_END

Usage: $0 [--export] TARGET_DIR
       $0 [-S|-N] {--create|--push|--pull|--clean}...

Create ABMS system backups

 -h,--help             print this help and exit
 -v,--verbose          be verbose
 -x,--xtrace           turn on Bash trace mode (set -x)
 -t,--log-timestamps   include timestamps in log and error messages
    --export           create backup in TARGET_DIR
    --create           create new backup in system backup directory
    --push             push system backup directory to peer server
    --pull             pull system backup directory from peer server
    --cleanup          delete old backups and temporary files
    --system           shorthand for \`--create --cleanup --push'
 -S,--with-service     ensure service is started before performing any
                         actions (can't be used with --export)
 -N,--without-service  ensure service is stopped before performing any
                         actions (can't be used with --export)

Configuration is in \`$DFLT_CONFIG_FILE'

System backup directory is \`$BACKUP_DIR'

_END
	exit 0
}


# process command line arguments
process_cmdline() {
	local create_found push_found pull_found clean_found export_found
	local with_service_found without_service_found
	local quoted_args
	quoted_args=$(getopt -n "$PROGNAME" -l help,verbose,xtrace,log-timestamps,export,create,push,pull,cleanup,system,with-service,without-service -o hvxtSN -- "$@")
	[ $? -eq 0 ] || cmdline_error
	eval "set -- $quoted_args"
	while [[ $# -gt 0 ]] ; do
		: "\$#=$#"
		: "\$1=$1"
		case "$1" in
			-h|--help)	      usage ;;
			-v|--verbose)	      VERBOSE=1 ;;
			-x|--xtrace)	      XTRACE=1 ;;
			-t|--log-timestamps)  LOG_TIMESTAMPS=1 ;;
			   --export)	      ACTION_EXPORT=1 ;;
			   --create)	      ACTION_CREATE=1 ;;
			   --push)	      ACTION_PUSH=1 ;;
			   --pull)	      ACTION_PULL=1 ;;
			   --cleanup)	      ACTION_CLEANUP=1 ;;
			   --system)	      ACTION_CREATE=1 ; ACTION_PUSH=1 ; ACTION_CLEANUP=1 ;;
			-S|--with-service)    with_service_found=1 ;;
			-N|--without-service) without_service_found=1 ;;
			   --)		      shift ; break ;;
			-*)		      cmdline_error ;;
			*)		      break ;;
		esac
		shift
	done
	: "\$#=$#"
	
	# no actions specified
	if [[ $ACTION_CREATE != 1 && $ACTION_PUSH != 1 && $ACTION_PULL != 1 && $ACTION_CLEANUP != 1 ]] ; then
		if [[ $ACTION_EXPORT != 1 ]] ; then
			# --export also not specified, but a non-option argument (directory) is => assume --export
			if [[ $# -gt 0 ]] ; then
				ACTION_EXPORT=1
			# no actions at all
			else
				cmdline_error "no actions specified"
			fi
		fi
	fi
		
	# --export
	if [[ $ACTION_EXPORT == 1 ]] ; then
		# --export can't be combined with any other actions
		if [[ $ACTION_CREATE == 1 || $ACTION_PUSH == 1 || $ACTION_PULL == 1 || $ACTION_CLEANUP == 1 ]] ; then
			cmdline_error "--export can't be combined with any other actions"
		fi
		# --export can't be combined with service options
		if [[ $with_service_found == 1 ]] ; then
			cmdline_error "--with-service can't be used in export mode"
		fi
		if [[ $without_service_found == 1 ]] ; then
			cmdline_error "--without-service can't be used in export mode"
		fi
		# --export requires a target directory
		if [[ $# -le 0 ]] ; then
			cmdline_error "not enough arguments"
		fi
		[[ -n $1 ]] || cmdline_error "invalid export target directory"
		ensure_dir_all_access "$1"
		EXPORT_TARGET_DIR="$1"


	# some other action
	else
		if [[ $# -gt 0 ]] ; then
			cmdline_error "too many arguments"
		fi
	fi

	# --with/without-service
	if [[ $with_service_found == 1 && $without_service_found == 1 ]] ; then
		cmdline_error "--with-service and --without-service can't be used together"
	elif [[ $with_service_found == 1 ]] ; then
		SVC_CHECK=started
	elif [[ $without_service_found == 1 ]] ; then
		SVC_CHECK=stopped
	fi

}



# process command line
process_cmdline "$@"

# set trace mode if necessary
[[ $XTRACE == 1 ]] && set -x || :

# read configuration
load_config

# exit on any errors
set -e

# export
if [[ $ACTION_EXPORT == 1 ]] ; then
	export_backup
	exit 0
fi

# ensure backup directory exists, etc
validate_backup_dir

# check service status if requested
if [[ -n $SVC_CHECK ]] ; then
	svc_is_started=no

	# systemd (CentOS >= 7)
	if [[ -x /usr/bin/systemctl ]] ; then
		/usr/bin/systemctl status $SVC_NAME >/dev/null 2>&1 && svc_is_started=yes || :
	# System V (CentOS < 7)
	elif [[ -x /sbin/service ]] ; then
		/sbin/service $SVC_NAME status >/dev/null 2>&1 && svc_is_started=yes || :
	# something else
	else
		fatal "unable to determine status of system service $SVC_NAME"
	fi

	# check requested service status
	if [[ $SVC_CHECK == started ]] ; then
		if [[ $svc_is_started != yes ]] ; then
			log_info "exiting because service \`$SVC_NAME' is stopped"
			exit 0
		else
			log_info "service \`$SVC_NAME' is started"
		fi
	elif [[ $SVC_CHECK == stopped ]] ; then
		if [[ $svc_is_started == yes ]] ; then
			log_info "exiting because service \`$SVC_NAME' is started"
			exit 0
		else
			log_info "service \`$SVC_NAME' is stopped"
		fi
	fi

	# undefine local vars
	unset svc_is_started
fi

# make sure newly-created files have all world permissions disabled
umask 0027

# use a lock file to make sure we don't mess with the system directory
# while another instance of this script is running; see flock(1) for
# more info
(
	# wait until we can obtain a lock
	while ! flock -n 9 ; do
		log_notice "$BACKUP_DIR is locked by another process, waiting..."
		sleep 1
	done

	# perform the action
	if [[ $ACTION_CREATE == 1 ]] ; then
		create_system_backup
	fi
	if [[ $ACTION_PULL == 1 ]] ; then
		pull_from_peer
	fi
	if [[ $ACTION_CLEANUP == 1 ]] ; then
		cleanup_system_backups
	fi
	if [[ $ACTION_PUSH == 1 ]] ; then
		push_to_peer
	fi
) 9>$BACKUP_DIR/.lock

