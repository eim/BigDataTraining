#!/usr/bin/env bash
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -ls /homework.out
docker exec -it hadoop-master /usr/local/hadoop/bin/hdfs dfs -cat /homework.out/part-r-00000
