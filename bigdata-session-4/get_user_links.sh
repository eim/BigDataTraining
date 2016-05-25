#!/usr/bin/env bash

#docker exec -it riak-2 apt-get install -y curl
docker exec -it vold-2 curl http://172.16.248.20:8098/buckets/facebook/keys/$1

