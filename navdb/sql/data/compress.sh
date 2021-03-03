#!/bin/bash

[ -n "$1" ] || { echo "Usage: $0 FILENAME" >&2 ; exit 7 ; }
set -ex

xz --force -8 -v "$1"

