#!/bin/bash
# vim:ts=2:sts=2:sw=2:et

# set globals
PROGNAME=$(basename $0)
USER=$(id -un)

DATA_DIR="/usr/share/abms/geoserver"
DFLT_SRC_DATA_FILE="$DATA_DIR/data.zip"
GS_DATA_DIR="/var/lib/abms/geoserver/data"
GWC_DIR="/var/lib/abms/geoserver/gwc"
LOG_DIR="/var/log/abms/geoserver"
CONF_DIR="/etc/abms/geoserver"
GS_LOG_FILE="$LOG_DIR/geoserver.log"
SVC="abms-geoserver"

QUIET=
FORCE=
WANT_IMPORT=
WANT_EXPORT=
USER_FILE=
TEMP_DIR=
INTERACTIVE=
RESTART_GEOSERVER=
OWNER=abms
GROUP=abms

# print out a help message and exit
usage() {
  cat <<_END

NAME

  $PROGNAME - manage ABMS geoserver data directory

  This script allows one to import and export the geoserver
  data directory located at "$GS_DATA_DIR".
  
  This script always omits log files and GWC tile cache files when importing
  and exporting.

  
USAGE

  $PROGNAME [OPTIONS...] ACTION [ZIP_FILE|GS_DATA_DIR]

OPTIONS

 -h,--help     print this help and exit
 -f,--force    overwrite existing files and don't complain about geoserver
               running
 -q,--quiet    be quiet

ACTIONS

    --reset
    --reset GS_DATA_DIR
               reset the given data directory to default (destructive)

    --import
    --import SRC_ZIP_FILE
    --import SRC_DIR
               replace geoserver data with the contents of the given zip
               file or source directory (destructive)

    --export DST_ZIP_FILE
               export geoserver data directory to the given zip file

    --fix
               post-process geoserver data directory by removing log files
               and updating files' permissions/ownership; this is also done
               automatically as part of --import.

    --prep DIR
               prepare an external data directory for archiving by removing
               logs, cached tiles, etc.

FILES

  $GS_DATA_DIR
      geoserver data directory

  $GWC_DIR
      geoserver GWC cache directory

  $LOG_DIR
      geoserver log files
      
_END
  exit 0
}

# handle command line errors
cmdline_error() {
  local msg="$1"
  if [ -n "$msg" ] ; then
    echo "error: $msg" >&2
  fi
  echo "Type \`$PROGNAME --help' for more info." >&2
  exit 7
}

# print out an error message and exit
fatal() {
  echo "error: $*" >&2
  exit 2
}

# print out a log message
trace() {
  [ -n "$QUIET" ] || echo "$@" >&2
}

# return zero if directory exists is not empty
dir_not_empty() {
  [ -d "$1" ] && [ -n "$(find "$1" -maxdepth 1 -mindepth 1 -print -quit)" ]
}

# remove $TEMP_DIR if necessary
cleanup() {
  local rv="$?"
  if [ -n "$TEMP_DIR" ] ; then
    rm -rf "$TEMP_DIR"
  fi
  exit $rv
}

# ask a question
ask() {
  local line
  read -p "$*" line
  echo "$line"
}

# ask a yes/no question
ask_yn() {
  local prompt="$1"
  local answer=
  while : ; do
    answer=$(ask "$prompt [y/n]: ")
    case "$answer" in
      y|Y|yes|Yes|YES)
        return 0
        ;;
      n|N|no|No|NO)
        return 1
        ;;
    esac
  done
}

# ask a question whose answer is a non-empty string
ask_str() {
  local prompt="$1" ; shift
  local answer=
  local dflt_answer=
  if [ "$#" -ge 1 ] ; then
    dflt_answer="$1" ; shift
  fi
  prompt="$prompt: [$dflt_answer] "
  while : ; do
    answer=$(ask "$prompt")
    if [ -n "$answer" ] ; then
      echo -n "$answer"
      exit 0
    fi
    echo -n "$dflt_answer"
    exit 0
  done
}

# manage service
svc() {
  if [ -x /usr/bin/systemctl ] ; then
    /usr/bin/systemctl "$1" "$SVC"
  else
    /bin/service "$SVC" "$1"
  fi
}

# restart geoserver if necessary
restart_geoserver() {
  if [ -n "$RESTART_GEOSERVER" ] ; then
    echo
    echo "Starting $SVC"
    svc start || exit 1
  fi
}

# copy data directory
copy_data_dir() {
  local src_dir="$1"
  local dst_dir="$2"
    rsync --delete -arH --no-owner --no-group --chmod=Du=rwx,Dgo=rx,Fu=rw,Fgo=r \
      --exclude "/logs/*.log" --exclude "/logs/*.log.*" \
      --exclude "/gwc/*" \
      "$1"/./ "$2"/
    [ $? = 0 ] || fatal "rsync failed"
}

# post-process an external/offline data directory before creating a zipped file
prep_data_dir() {
  local data_dir="$1"
  
  # check access
  stat "$data_dir" >/dev/null || exit 1
  [ -d "$data_dir" ] || fatal "$data_dir: not a directory"
  [ -w "$data_dir" ] || fatal "$data_dir: permission denied"

  # remove log files from logs/
  if [ -d "$data_dir/logs" ] ; then
    find "$data_dir/logs/" -mindepth 1 -maxdepth 1 -type f \( -name '*.log' -o -name '*.log.*' \) -delete
  fi

  # remove GWC cache files
  if [ -d "$data_dir/gwc" ] ; then
    rm -rf "$data_dir/gwc/"*
  fi
}

# post-process data directory after installing to /var/lib/geoserver/data
fix_data_dir() {

  # check access
  stat "$GS_DATA_DIR" >/dev/null || exit 1
  [ -d "$GS_DATA_DIR" ] || fatal "$GS_DATA_DIR: not a directory"
  [ -w "$GS_DATA_DIR" ] || fatal "$GS_DATA_DIR: permission denied"

  echo
  echo "Fixing $GS_DATA_DIR"

  # update file ownership
  chown -R $OWNER:$GROUP "$GS_DATA_DIR" || exit 1
  find "$GS_DATA_DIR" -type d -execdir chmod 0775 '{}' '+' || exit 1
  find "$GS_DATA_DIR" -type f -execdir chmod 0664 '{}' '+' || exit 1
  
  # move cache tiles to /var/lib/geoserver/gwc/
  if [ -d "$GS_DATA_DIR/gwc" ] ; then
    found=$(find "$GS_DATA_DIR/gwc/./" -mindepth 1 -maxdepth 1 \! -name geowebcache'*'.xml -print -quit)
    if [ -n "$found" ] ; then
      echo "moving GWC cache to $GWC_DIR/"
      find "$GS_DATA_DIR/gwc/./" -mindepth 1 -maxdepth 1 \! -name geowebcache'*'.xml -print \
        | xargs -i /bin/sh -c "echo \"    -> \`basename '{}'\`\" ; rsync -arq '{}' '$GWC_DIR/'"
    fi
    rm -rf "$GS_DATA_DIR/gwc/"/*
  fi

  # remove log files etc
  prep_data_dir "$GS_DATA_DIR"
  
}

# make sure it's safe to write to geoserver data directory
check_data_dir() {
  # check whether geoserver is running
  if [ -z "$FORCE" ] ; then
    if svc status >/dev/null 2>&1 ; then
      if [ -n "$INTERACTIVE" -a $EUID -eq 0 ] ; then
        echo
        ask_yn "Geoserver is running, do you want to stop it now?" || fatal "you chose not to stop geoserver, exiting"
        echo
        echo "Stopping $SVC"
        svc stop || exit 1
        echo
        RESTART_GEOSERVER=yes
      else
        fatal "geoserver is running, please stop it first or use --force to ignore this error"
      fi
    fi
  fi
}
# same as above, but also make sure datadir is empty
check_data_dir_before_overwrite() {
  # make sure geoserver is not running
  check_data_dir
  # make sure we are tomcat or root
  [ "$USER" = "$OWNER" -o "$USER" = "root" ] || fatal "permission denied"
  # check whether directory exists
  if [ -z "$FORCE" ] && dir_not_empty "$GS_DATA_DIR" ; then
    if [ -n "$INTERACTIVE" ] ; then
      ask_yn "
$GS_DATA_DIR is not empty.
Do want to overwrite it and loose all your changes?" || fatal "you chose not to overwrite $GS_DATA_DIR, exiting"
      echo
    else
      fatal "$GS_DATA_DIR is not empty; use --force to override"
    fi
  fi
}

# import data from file or directory to /var/lib/geoserver/data
import_data_dir() {
  local src_file="$1"
  # make sure source file exists and is readable
  if [ "$src_file" != "-" ] ; then
    stat "$src_file" >/dev/null || exit 1
    [ -r "$src_file" ] || fatal "$src_file: permission denied"
  fi
  echo
  echo "Importing $src_file to $GS_DATA_DIR"
  # make sure TEMP_DIR is deleted at the end
  trap cleanup INT TERM HUP PIPE EXIT
  # source is a file?
  if [ "$src_file" = "-" ] || [ -f "$src_file" ] ; then
    # check target dir
    check_data_dir_before_overwrite
    # unzip it in a temporary directory
    TEMP_DIR=$(mktemp -d --tmpdir "geoserver_datadir_import.XXXXXX")
    { [ $? -eq 0 ] && [ -n "$TEMP_DIR" ] ; } || fatal "failed to create temporary directory"
    trace "extracting files from $src_file"
    [ "$src_file" = "-" ] && unzip -d "$TEMP_DIR" -q /dev/stdin || unzip -d "$TEMP_DIR" -q "$src_file"
    [ $? -eq 0 ] || fatal "unzip failed"
    local req_file_count=$(find "$TEMP_DIR" -maxdepth 1 -mindepth 1 -path "$TEMP_DIR/workspaces" -o -path "$TEMP_DIR/www" -o -path "$TEMP_DIR/global.xml" | wc -l)
    [ "$req_file_count" -eq 3 ] || fatal "$src_file: this doesn't look like a geoserver data directory archive"
    # install extracted files to /var/lib/geoserver/data
    trace "installing extracted files to $GS_DATA_DIR"
    copy_data_dir "$TEMP_DIR" "$GS_DATA_DIR" || exit 1
    fix_data_dir || exit 1
    # restart geoserver if necessary
    restart_geoserver
    # done
    exit 0
  fi
  
  # otherwise assume it's a directory
  stat "$src_file" >/dev/null || exit 1
  [ -d "$src_file" ] || fatal "$src_file: not a directory"
  local first_file=$(find "$src_file" -type f -print -quit)
  [ -n "$first_file" ] || fatal "$src_file: directory is empty"
  [ -r "$first_file" ] || fatal "$first_file: permission denied"
  local req_file_count=$(find "$src_file" -maxdepth 1 -mindepth 1 -path "$src_file/workspaces" -o -path "$src_file/www" -o -path "$src_file/global.xml" | wc -l)
  [ "$req_file_count" -eq 3 ] || fatal "$src_file: this doesn't look like a geoserver data directory"
  # check target dir
  check_data_dir_before_overwrite
  # install source dir to /var/lib/geoserver/data
  trace "installing files from $src_dir to $GS_DATA_DIR"
  copy_data_dir "$src_file" "$GS_DATA_DIR" || exit 1
  fix_data_dir || exit 1
  # restart geoserver if necessary
  restart_geoserver
  # done
  exit 0
}

# export data to a ZIP file
export_data_dir() {
  local dst_file="$1"
  # check whether geoserver is running
  if [ -z "$FORCE" ] ; then
    if svc status >/dev/null 2>&1 ; then
      if [ -n "$INTERACTIVE" -a $EUID -eq 0 ] ; then
        if ask_yn "
Geoserver is running, if your export may be inconsistent.
Do you wish to stop it now?" ; then
          echo
          echo "Stopping $SVC"
          svc stop || exit 1
          echo
          RESTART_GEOSERVER=yes
        fi
      else
        fatal "geoserver is running, please stop it first or use --force to ignore this error"
      fi
    fi
  fi

  # find out export file name
  if [ "$#" -ge 1 ] ; then
    dst_file="$1"
  elif [ -n "$DFLT_EXPORT_FILE" ] && [ -n "$DFLT_EXPORT_FILE" ] ; then
    echo
    dst_file=$(ask_str "Enter destination file name" "$DFLT_EXPORT_FILE")
    echo
  else
    fatal "you must provide a destination file name"
  fi
  if [ "$dst_file" != "-" -a -z "$FORCE" -a -e "$dst_file" ] ; then
    if [ -n "$INTERACTIVE" ] ; then
      ask_yn "
File $dst_file already exists.
Do wish to overwrite it?" || fatal "you chose not to overwrite $dst_file, exiting"
      echo
    else
      fatal "file $dst_file already exists; use --force to overwrite"
    fi
  fi
  
  echo
  echo "Exporting $GS_DATA_DIR to $dst_file"

  # make sure TEMP_DIR is deleted at the end
  trap cleanup INT TERM HUP PIPE EXIT
  # copy /var/lib/geoserver/data to a temporary directory
  TEMP_DIR=$(mktemp -d --tmpdir "geoserver_datadir_export.XXXXXX")
  { [ $? -eq 0 ] && [ -n "$TEMP_DIR" ] ; } || fatal "failed to create temporary directory"
  trace "copying files from $GS_DATA_DIR to a temporary location"
  copy_data_dir "$GS_DATA_DIR" "$TEMP_DIR"
  # zip it up
  trace "creating zip archive $dst_file"
  if [ "$dst_file" = "-" ] ; then
    ( cd "$TEMP_DIR" && zip -r -q "-" "." ; )
  else
    if [ -e "$dst_file" -a ! -f "$dst_file" ] ; then
      fatal "$dst_file: not a regular file"
    fi
    ( cd "$TEMP_DIR" && zip -r -q "-" "." ; ) >"$dst_file"
  fi
  [ $? -eq 0 ] || {
    [ "$dst_file" = "-" ] || rm -f "$dst_file"
    fatal "zip failed"
  }

  # restart geoserver if necessary
  restart_geoserver
  # done
  exit 0
}

# process command line
declare -A actions
quoted_args="$(getopt -n $PROGNAME -l help,force,quiet,reset,import,export,fix,prep -o hfq -- "$@")"
[ $? -eq 0 ] || cmdline_error
eval "set -- $quoted_args"
while [ "$#" -gt 0 ] ; do
  case "$1" in
    -h|--help)    usage ;;
    -f|--force)   FORCE=yes ;;
    -q|--quiet)   QUIET=yes ;;
       --reset)   actions[reset]=1 ;;
       --import)  actions[import]=1 ;;
       --export)  actions[export]=1 ;;
       --fix)     actions[fix]=1  ;;
       --prep)    actions[prep]=1 ;;
    --)           shift ; break ;;
    *)            break ;;
  esac
  shift
done
if [ "${#actions[@]}" -gt 1 ] ; then
  cmdline_error "multiple actions specified"
elif [ "${#actions[@]}" -lt 1 ] ; then
  actions[import]=1 
fi
action="${!actions[@]}"

# use itneractive mode if stdin/stdout are both connected to a terminal
if [ -t 0 -a -t 1 ] ; then
  [ -n "$FORCE" ] && INTERACTIVE= || INTERACTIVE=yes
fi

case "$action" in
  reset)
    [ "$#" -le 1 ] || cmdline_error "too many arguments"
    if [ "$#" -ge 1 ] ; then
      import_data_dir "$1"
    else
      import_data_dir "$DFLT_SRC_DATA_FILE"
    fi
    ;;
  import)
    [ "$#" -le 1 ] || cmdline_error "too many arguments"
    [ "$#" -ge 1 ] || cmdline_error "not enough arguments"
    import_data_dir "$1"
    ;;
  export)
    [ "$#" -le 1 ] || cmdline_error "too many arguments"
    if [ "$#" -ge 1 ] ; then
      export_data_dir "$1"
    elif [ -n "$DFLT_EXPORT_FILE" ] ; then
      export_data_dir
    else
      cmdline_error "not enough arguments"
    fi
    ;;
  fix)
    [ "$#" -le 1 ] || cmdline_error "too many arguments"
    check_data_dir
    fix_data_dir
    restart_geoserver
    ;;
  prep)
    [ "$#" -le 1 ] || cmdline_error "too many arguments"
    [ "$#" -ge 1 ] || cmdline_error "not enough arguments"
    prep_data_dir "$1"
    ;;
esac

