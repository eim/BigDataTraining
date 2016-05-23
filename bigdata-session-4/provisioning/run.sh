#!/usr/bin/env bash
docker network create --driver=bridge --subnet=172.16.228.0/24 --gateway=172.16.228.1 riaknetwork

docker run  -d \
            --name riak-1 \
            -h 'riak-1' \
            -e NODE_IP=172.16.228.20 \
            --ip=172.16.228.20 \
            --net=riaknetwork \
            riak

docker run  -d \
            --name riak-2 \
            -h 'riak-2' \
            -e NODE_IP=172.16.228.21 \
            -e SEED_NODE_IP=172.16.228.20 \
            --ip=172.16.228.21 \
            --net=riaknetwork \
            riak


docker run  -d \
            --name riak-3 \
            -h 'riak-3' \
            -e NODE_IP=172.16.228.22 \
            -e SEED_NODE_IP=172.16.228.20 \
            --ip=172.16.228.22 \
            --net=riaknetwork \
            riak


