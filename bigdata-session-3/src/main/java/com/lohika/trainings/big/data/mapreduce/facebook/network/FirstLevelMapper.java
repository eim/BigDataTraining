package com.lohika.trainings.big.data.mapreduce.facebook.network;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class FirstLevelMapper extends Mapper<Object, Text, IntWritable, Text> {

  private Text text = new Text();
  private IntWritable intWritable = new IntWritable();

  public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    StringTokenizer itr = new StringTokenizer(value.toString());

    String userId = itr.nextToken();
    String friendId = itr.nextToken();


    publishClient(context, userId, friendId);
    publishClient(context, friendId, userId);
  }

  private void publishClient(Context context, String user, String friend) throws IOException, InterruptedException {
    intWritable.set(Integer.parseInt(user));
    text.set(friend);

    context.write(intWritable, text);
  }
}
