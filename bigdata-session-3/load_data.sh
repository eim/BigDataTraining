#!/usr/bin/env bash
docker cp datasets/facebook_combined.txt hadoop-master:/tmp/facebook_combined.txt
docker cp datasets/payments.log hadoop-master:/tmp/payments.log

docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -rm /facebook_combined.txt
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -put /tmp/facebook_combined.txt /

docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -rm /payments.log
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -put /tmp/payments.log /

docker exec -it hadoop-master rm -rf /tmp/big-data.mapreduce-1.0-SNAPSHOT.jar
docker cp build/libs/big-data.mapreduce-1.0-SNAPSHOT.jar hadoop-master:/tmp
