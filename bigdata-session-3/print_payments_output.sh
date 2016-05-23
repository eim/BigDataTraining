#!/usr/bin/env bash
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -ls /payments.out
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -cat /payments.out/part-r-00000
