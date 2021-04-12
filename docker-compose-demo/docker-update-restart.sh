#!/bin/bash

set -ex

. abms.conf

docker-compose build server
docker-compose pull
docker-compose down
docker-compose up -d
