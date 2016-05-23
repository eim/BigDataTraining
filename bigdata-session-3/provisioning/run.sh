#!/usr/bin/env bash
docker network create --driver=bridge --subnet=172.16.238.0/24 --gateway=172.16.238.1 hadoop

docker run  -d \
            --name hadoop-master \
            -h 'hadoop-master' \
            -e NAMENODE_HOSTNAME=172.16.238.10 \
            -e RM_HOSTNAME=172.16.238.10 \
            -e HOSTNAME=172.16.238.10 \
            -e HADOOP_NONE_ROLE=master \
            --ip=172.16.238.10 \
            --net=hadoop \
            --add-host=hadoop-master:172.16.238.10 \
            --add-host=hadoop-slave-1:172.16.238.11 \
            --add-host=hadoop-slave-2:172.16.238.12 \
            -p "8088:8088" \
            -p "50070:50070" \
            -p "9000:9000" \
            -p "2122:2122" \
            hadoop

docker run  -d \
            --name hadoop-slave-1 \
            -h 'hadoop-slave-1' \
            -e HOSTNAME=172.16.238.11 \
            -e NAMENODE_HOSTNAME=172.16.238.10 \
            -e RM_HOSTNAME=172.16.238.10 \
            -e NM_HOSTNAME=172.16.238.11 \
            -e HADOOP_NONE_ROLE=slave \
            --ip=172.16.238.11 \
            --net=hadoop \
            --add-host=hadoop-master:172.16.238.10 \
            --add-host=hadoop-slave-1:172.16.238.11 \
            --add-host=hadoop-slave-2:172.16.238.12 \
            -p "50075:50075" \
            -p "8042:8042" \
            -p "19888:19888" \
            hadoop

docker run  -d \
            --name hadoop-slave-2 \
            -h 'hadoop-slave-2' \
            -e HOSTNAME=172.16.238.12 \
            -e NAMENODE_HOSTNAME=172.16.238.10 \
            -e RM_HOSTNAME=172.16.238.10 \
            -e NM_HOSTNAME=172.16.238.12 \
            -e HADOOP_NONE_ROLE=slave \
            --ip=172.16.238.12 \
            --net=hadoop \
            --add-host=hadoop-master:172.16.238.10 \
            --add-host=hadoop-slave-1:172.16.238.11 \
            --add-host=hadoop-slave-2:172.16.238.12 \
            -p "50076:50075" \
            -p "8043:8042" \
            -p "19889:19888" \
            hadoop


