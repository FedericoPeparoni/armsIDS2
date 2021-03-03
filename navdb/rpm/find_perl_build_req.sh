#!/bin/sh

#
# This script extracts prints out Perl build dependencies from all perl
# scripts and modules. You can paste its output into the spec file manually
# as necessary.
#

set -ex

find .. -name '*.pl' -o -name '*.pm' -o -name '*.t' \
	| grep -v '^\.\./rpm' \
	| xargs perl -n -e '/^(?:use|require)\s+([a-zA-Z_:0-9]+).*/ and print $1, "\n"' \
	| sort -u \
	| egrep -v '^(lib|warnings|strict|Local::)' \
	| perl -p -e 'chomp;$_="BuildRequires: perl($_)\n"'

