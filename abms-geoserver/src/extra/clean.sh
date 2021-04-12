#!/bin/bash

find . -mindepth 1 -maxdepth 1 '!' -name '*.sh' -a '!' -name '.*' | xargs rm -r

