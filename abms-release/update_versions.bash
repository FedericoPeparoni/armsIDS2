#!/bin/bash

set -e
cd scripts/UPDATE_VERSIONS
for s in $(ls [0-1]*.bash) ; do
	./$s
done
