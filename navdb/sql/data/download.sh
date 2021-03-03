#!/bin/sh

set -e

. ../../package.conf

PACKED_FILE=`basename "$AERO_DATA_URL"`
UNPACKED_FILE=`echo "$PACKED_FILE" | sed -r 's,\.(xz|bz2|gz)$,,g'`

rm -f "$PACKED_FILE.tmp"

# If unpacked file exists, then create the packed version
if [[ -f $UNPACKED_FILE ]] ; then
	if [[ ! -f $PACKED_FILE ]] ; then
		./compress.sh "$UNPACKED_FILE"
	fi
# If packed file doesn't exist download it
elif [[ ! -f $PACKED_FILE ]] ; then
	echo "downloading $PACKED_FILE"
	if which wget >/dev/null 2>&1 ; then
		wget -O "$PACKED_FILE.tmp" "$AERO_DATA_URL"
	else
		curl -fsSL -o "$PACKED_FILE.tmp" "$AERO_DATA_URL"
	fi
	if [ $? -ne 0 ] ; then
		rm -f "$PACKED_FILE.tmp"
		exit 1
	fi
	mv "$PACKED_FILE.tmp" "$PACKED_FILE"
fi

