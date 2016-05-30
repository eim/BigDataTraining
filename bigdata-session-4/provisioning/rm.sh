#!/usr/bin/env bash
docker rm -f vold-1
docker rm -f vold-2
docker rm -f vold-3
docker network rm voldnetwork
VOLD_IMG=$(docker images|grep voldemort|awk '{print $3}')
docker rmi $VOLD_IMG

