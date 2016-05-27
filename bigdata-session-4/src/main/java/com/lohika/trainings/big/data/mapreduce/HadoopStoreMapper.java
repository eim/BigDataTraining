package com.lohika.trainings.big.data.mapreduce;

/**
 * Created by eugene on 5/27/16.
 */
import org.apache.hadoop.io.Text;
import voldemort.store.readonly.mr.AbstractHadoopStoreBuilderMapper;

public class HadoopStoreMapper extends AbstractHadoopStoreBuilderMapper<Text, Text> {

    @Override
    public Object makeKey(Text key, Text value) {
        return value.toString();
    }

    @Override
    public Object makeValue(Text key, Text value) {
        return value.toString();
    }
}
