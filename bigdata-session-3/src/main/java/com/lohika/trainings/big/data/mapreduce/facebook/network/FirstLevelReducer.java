package com.lohika.trainings.big.data.mapreduce.facebook.network;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FirstLevelReducer extends Reducer<IntWritable, Text, IntWritable, Text> {

  private Text text = new Text();

  public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    text.set("");

    for (Text friend: values) {
      byte[] friendBytes = friend.getBytes();
      text.append(" ".getBytes(), 0, 1);
      text.append(friendBytes, 0, friend.getLength());
    }

    context.write(key, text);
  }
}
