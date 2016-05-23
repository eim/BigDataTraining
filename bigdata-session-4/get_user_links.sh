#!/usr/bin/env bash

#docker exec -it riak-2 apt-get install -y curl
docker exec -it riak-2 curl http://172.16.238.20:8098/buckets/facebook/keys/$1

