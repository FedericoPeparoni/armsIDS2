#!/bin/bash

set -e

. ../../package.conf

for spec in "$GS_BIN_URL" "$JETTY_BIN_URL" "$LOGGING_MODULES_URL" "$JETTY_WEBAPP_LOGGING_URL" ; do
	read -r -a parts <<< "$spec"
	url="${parts[0]}"
	basename="${parts[1]}"
	[ -n "$basename" ] || basename="$(basename "$url")"
	if ! [ -f "$basename" ] ; then
		rm -rf "$basename"*
		echo ">>> downloading $url"
		rm -f "$basename.tmp"
		curl -fLsS -o "$basename.tmp" "$url"
		case "$basename" in
			*.zip)
				unzip -q "$basename.tmp"
				;;
			*.tar.gz)
				tar -xf "$basename.tmp"
				;;
			*)
				echo "$0: Unsupported archive type \`$b'" >&2
				exit 1
				;;
		esac
		mv "$basename.tmp" "$basename"
	fi
done

mkdir -p extra-jars
for url in $EXTRA_JARS_URL_LIST ; do
	basename="$(basename "$url")"
	if ! [ -f "extra-jars/$basename" ] ; then
		rm -rf "extra-jars/$basename"*
		echo ">>> downloading $url"
		rm -f "extra-jars/$basename.tmp"
		curl -fLsS -o "extra-jars/$basename.tmp" "$url"
		mv "extra-jars/$basename.tmp" "extra-jars/$basename"
	fi
done

