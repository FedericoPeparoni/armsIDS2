#!/bin/bash

# Cleans up unwanted Docker containers and images.
# Courtesy of: http://blog.yohanliyanage.com/2015/05/docker-clean-up-after-yourself/

set -ex
docker rm -v $(docker ps -a -q -f status=exited)
docker rmi $(docker images -f "dangling=true" -q)

