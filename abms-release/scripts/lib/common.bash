# bash

# work out top level directory location
TOP_DIR=$(dirname ${BASH_SOURCE[0]})/../..
TOP_DIR_ABS=$(readlink -f $TOP_DIR)

# print out an error message and exit
fatal() {
	echo "error: $*" >&2
	exit 2
}

# Log functions
log_notice() {
	echo "$*" >&2
}
log_error() {
	echo "error: $*" >&2
}

# Find all module directories
get_modules() {
	find modules -mindepth 1 -maxdepth 1 -type d \! -name '.*'
}

# Usage: __replace_text_file $ORIGINAL_FILE $UPDATED_FILE
#
# This will effectively replace $ORIGINAL_FILE with $UPDATED_FILE (as if by calling mv);
# but it will also correct the updated file to use the same UNIX/DOS line endings style
# as the original file before replacing it.
#
__replace_text_file() {
	local original_file="$1" updated_file="$2"
	# original file exists
	if [[ -f "$original_file" ]] ; then
		# target file uses DOS line endings => convert new file to DOS style as well
		if grep -q -U $'\r' "$original_file" ; then
			perl -p -e 's/\r?\n/\r\n/' "$updated_file" >"$original_file"
		# target has no line endings at all => just replace it with updated file
		elif [[ $(cat "$original_file" | wc -l) -le 0 ]] ; then
			mv -f "$updated_file" "$original_file"
		# assume target file uses UNIX line endings => replace it with updated file,
		# while converting it to UNIX-style line endings
		else
			tr -d $'\r' <"$updated_file" >"$original_file"
		fi
	# original file doesn't exist => just replace it
	else
		mv -f "$updated_file" "$original_file"
	fi
	return 0
}

#
# Usage: __get_maven_project_version "/path/to/project/dir"
#
# Prints maven project version on STDOUT. It uses maven to extract version info
# from pom.xml.
#
__get_maven_project_version() {
	local project_dir="$1" mvn=mvn ver=
	if [[ -f "$project_dir/mvnw" ]] ; then
		mvn="./mvnw"
	fi
	ver=$(echo $'VERSION=${project.version}\n' | ( cd "$project_dir" && $mvn "org.apache.maven.plugins:maven-help-plugin:3.0.1:evaluate" ) | sed -nr 's,^VERSION=,,gp') || :
	if [[ -z $ver ]] ; then
		( cd "$project_dir" && $mvn -v >/dev/null ; ) || :
		fatal "$project_dir: unable to determine pom.xml artifact version"
	fi
	echo "$ver"
}

#
# Usage: __set_maven_project_version "/path/to/project/dir" $NEW_VER
#
# Updates pom.xml in the given directory to the specified new version
#
__set_maven_project_version() {
	local project_dir="$1" new_ver="$2" mvn=mvn
	if [[ -f "$project_dir/mvnw" ]] ; then
		mvn="./mvnw"
	fi
	if ! ( cd "$project_dir" && $mvn versions:set -DnewVersion="$new_ver" >/dev/null ; ) ; then 
		fatal "failed to set project version in $project_dir"
	fi
}

#
# Usage: __get_npm_project_version "/path/to/project/dir"
#
# Print the version of an NPM project on STDOUT (from package.json)
#
__get_npm_project_version() {
	local project_dir="$1" ver
	ver=$(perl -n -e '/"version"\s*:\s*"([^"]+)"/ and do { print $1, "\n" ; exit 0; } ; 0' "$project_dir/package.json")
	if [[ -z "$ver" ]] ; then
		fatal "$project_dir: unable to determine package.json project version"
	fi
	echo "$ver"
}

#
# Usage: __set_npm_project_version "/path/to/project/dir" $NEW_VER
#
# Update package.json in the given directory to the specified new version
#
__set_npm_project_version() {
	local project_dir="$1" new_ver="$2"
	if ! NEW_VER="$new_ver" perl -p -e '!$done and s/^(.*"version"\s*:\s*)(?:".+?")/$1"$ENV{NEW_VER}"/ and $done=1' "$project_dir/package.json" >"$project_dir/package.json.$$.tmp" ; then
		rm -f "$project_dir/package.json.$$.tmp"
		fatal "$project_dir: unable to update version information in package.json"
	fi
	__replace_text_file "$project_dir/package.json" "$project_dir/package.json.$$.tmp" 
}


#
# Usage: get_project_version "/path/to/project/dir"
#
# Print project version on STDOUT -- by parsing package.conf or pom.xml or whatever
#
get_project_version() {
	local project_dir="$1" ver=
	if [[ -f "$project_dir/package.conf" ]] ; then
		ver=$(cd "$project_dir" && unset PACKAGE_VERSION && . ./package.conf && echo -n "$PACKAGE_VERSION")
	fi
	if [[ -z $ver && -f "$project_dir/pom.xml" ]] ; then
		__get_maven_project_version "$project_dir" || return 1
		return 0
	fi
	if [[ -z $ver && -f "$project_dir/package.json" ]] ; then
		__get_npm_project_version "$project_dir" || return 1
		return 0
	fi
	if [[ -z $ver ]] ; then
		log_error "$project_dir: unable to determine project version"
		return 1
	fi
	echo "$ver"
		
}

#
# Usage set_project_version "/path/to/project/dir" NEW_VERSION
#
# Set version of the project in the specified directory
#
set_project_version() {
	local project_dir="$1" new_ver="$2" old_ver
	if [[ -f "$project_dir/package.conf" ]] ; then
		old_ver=$(cd "$project_dir" && unset PACKAGE_VERSION && . ./package.conf && echo -n "$PACKAGE_VERSION")
		if [[ -n $old_ver ]] ; then
			sed -r "s,^(\s*PACKAGE_VERSION=).*,\1\"${new_ver}\",g" "$project_dir/package.conf" >"$project_dir/package.conf.$$.tmp"
			__replace_text_file "$project_dir/package.conf" "$project_dir/package.conf.$$.tmp"
			return 0
		fi
	fi
	if [[ -f "$project_dir/pom.xml" ]] ; then
		__set_maven_project_version "$project_dir" "$new_ver" || return 1
		return 0
	fi
	if [[ -f "$project_dir/package.json" ]] ; then
		__set_npm_project_version "$project_dir" "$new_ver" || return 1
		return 0
	fi
	fatal "$project_dir: unable to set project version"
	return 1
}

# evaluates to true if version string is valid
version_is_valid() {
	[[ $1 =~ ^[0-9]+(\.[0-9]+){1,5}(-SNAPSHOT)?$ ]]
}

# increment last component in a version string, e.g.: 1.2.7 => 1.2.8
increment_release_in_version_string() {
	perl -e '$_ = shift (@ARGV) ; s/^([0-9]+(?:\.[0-9]+)*)(?:\.([0-9]+))(-SNAPSHOT)?$/$1 . "." . ($2 + 1) . $3/ge ; print $_. "\n"' "$1"
}

#
# Usage: ask_yesno "$PROMPT" [yes|no]
#
# Ask user a yes/no question; returns 0 if they answered "yes". The optional
# second argument is the default answer.
#
ask_yesno() {
	local prompt="$1" dflt="$2" answer
	while : ; do
		read -e -i "$dflt" -p "$prompt " answer || exit 1
		[[ $answer =~ ^(yes|no)$ ]] && break || :
		echo >&2
		echo "Please type \`yes' or \`no'" >&2
		echo >&2
	done
	echo >&2
	if [[ $answer == yes ]] ; then
		return 0
	fi
	return 1
}


# Make sure user answers "yes" to a confirmation; or else exit script
confirm() {
	local prompt="$1" dlft="$2"
	[[ -n $dlft ]] || dflt="yes"
	ask_yesno "$prompt" "$dflt" || fatal "exiting upon user request"
	return 0
}


