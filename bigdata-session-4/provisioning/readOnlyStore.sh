#!/usr/bin/env bash
hadoop-build-readonly-store.sh --input /facebook_first_level.out/
        --output test --tmpdir tmp-build --mapper com.lohika.trainings.big.data.mapreduce.HadoopStoreMapper \
        --jar /tmp/big-data.keyvalue-storage-1.0-SNAPSHOT.jar --cluster config/cluster.xml \
        --storename test --storedefinitions config/stores.xml \
        --chunksize 1073741824 --replication 2