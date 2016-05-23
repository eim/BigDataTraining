#!/usr/bin/env bash

docker exec -it 'riak-1' rm -rf /tmp/big-data.keyvalue-storage-1.0-SNAPSHOT.jar
docker cp build/libs/big-data.keyvalue-storage-1.0-SNAPSHOT.jar 'riak-1':/tmp
docker cp ./facebook_combined.txt 'riak-1':/tmp
docker exec -it 'riak-1' /usr/local/hadoop/bin/hdfs dfs -rm /facebook_combined.txt
docker exec -it 'riak-1' /usr/local/hadoop/bin/hdfs dfs -put /tmp/facebook_combined.txt /

docker exec -it 'riak-1' /usr/local/hadoop/bin/hdfs dfs -rm -r /facebook_first_level.out
docker exec -it 'riak-1' /usr/local/hadoop/bin/hadoop jar /tmp/big-data.keyvalue-storage-1.0-SNAPSHOT.jar com.lohika.trainings.big.data.mapreduce.SimpleFacebookNetworkJob /facebook_combined.txt /facebook_first_level.out
