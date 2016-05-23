#!/usr/bin/env bash
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -rm -r /payments.out
docker exec -it hadoop-master /usr/local/hadoop/bin/hadoop jar /tmp/big-data.mapreduce-1.0-SNAPSHOT.jar com.lohika.trainings.big.data.mapreduce.CustomerPaymentsJob /payments.log /payments.out
