package com.lohika.trainings.big.data.mapreduce.facebook.network;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import java.util.StringTokenizer;

public class SecondLevelMapper extends Mapper<Object, Text, IntWritable, IntArrayWritable> {
  private IntArrayWritable arrayWritable = new IntArrayWritable();
  private List<IntWritable> firstLevelFriends = new LinkedList<IntWritable>();

  public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    StringTokenizer itr = new StringTokenizer(value.toString());

    firstLevelFriends.clear();

    // Skip initial user, because we need only his relations
    itr.nextToken();

    while (itr.hasMoreTokens()) {
      firstLevelFriends.add(new IntWritable(Integer.parseInt(itr.nextToken())));
    }

    arrayWritable.set(firstLevelFriends.toArray(new IntWritable[firstLevelFriends.size()]));

    for (IntWritable friendId: firstLevelFriends) {
      context.write(friendId, arrayWritable);
    }
  }
}
