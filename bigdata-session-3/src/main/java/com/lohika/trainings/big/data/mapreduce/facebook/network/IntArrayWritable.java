package com.lohika.trainings.big.data.mapreduce.facebook.network;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;

public class IntArrayWritable extends ArrayWritable {
  public IntArrayWritable() {
    super(IntWritable.class);
  }
}
