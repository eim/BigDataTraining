#!/usr/bin/env bash
docker network create --driver=bridge --subnet=172.16.248.0/24 --gateway=172.16.248.1 voldnetwork

docker run  -d \
            --name vold-1 \
            -h 'vold-1' \
            -e NODE_IP=172.16.248.20 \
            --ip=172.16.248.20 \
            --net=voldnetwork \
            voldemort

docker run  -d \
            --name vold-2 \
            -h 'vold-2' \
            -e NODE_IP=172.16.248.21 \
            -e SEED_NODE_IP=172.16.248.20 \
            --ip=172.16.248.21 \
            --net=voldnetwork \
            voldemort


docker run  -d \
            --name vold-3 \
            -h 'vold-3' \
            -e NODE_IP=172.16.248.22 \
            -e SEED_NODE_IP=172.16.248.20 \
            --ip=172.16.248.22 \
            --net=voldnetwork \
            voldemort


