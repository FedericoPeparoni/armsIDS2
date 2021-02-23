#!/bin/sh

set -ex

dropdb abms
createdb abms
psql -c "create user abms with password 'abms';" || :
psql -c "grant all privileges on database abms to abms;"
psql abms -c "create schema abms authorization abms;"
psql abms -c "create extension if not exists postgis schema public;"

