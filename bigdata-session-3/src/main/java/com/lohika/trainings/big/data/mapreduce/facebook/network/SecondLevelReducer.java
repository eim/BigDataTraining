package com.lohika.trainings.big.data.mapreduce.facebook.network;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SecondLevelReducer extends Reducer<IntWritable, IntArrayWritable, IntWritable, Text> {

  private Text text = new Text();
  private Set<IntWritable> secondLevelFriends = new HashSet<IntWritable>();

  public void reduce(IntWritable key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
    text.set("");
    secondLevelFriends.clear();

    for (IntArrayWritable users: values) {
      secondLevelFriends.addAll(Arrays.asList((IntWritable[])users.toArray()));
    }

    secondLevelFriends.remove(key);

    for (IntWritable friend: secondLevelFriends) {
      byte[] friendBytes = friend.toString().getBytes();
      text.append(" ".getBytes(), 0, 1);
      text.append(friendBytes, 0, friendBytes.length);
    }

    context.write(key, text);
  }
}
