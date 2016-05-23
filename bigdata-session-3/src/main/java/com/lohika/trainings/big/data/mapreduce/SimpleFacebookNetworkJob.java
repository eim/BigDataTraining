package com.lohika.trainings.big.data.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class SimpleFacebookNetworkJob {
  public static class TokenizerMapper
    extends Mapper<Text, Text, Text, Text>{

    public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
      publishClient(context, key, value);
      publishClient(context, value, key);

    }

    private void publishClient(Context context, Text user, Text friend) throws IOException, InterruptedException {
      context.write(user, friend);
    }
  }

  public static class FirstLevelFriendNetReducer
    extends Reducer<Text, Text, Text, Text> {


    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      Text text = new Text();
      text.set("");

      for (Text friend: values) {
        byte[] friendBytes = friend.getBytes();
        text.append(" ".getBytes(), 0, 1);
        text.append(friendBytes, 0, friend.getLength());
      }

      context.write(key, text);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", " ");

    Job job = Job.getInstance(conf, "Simple Facebook network");

    job.setJarByClass(SimpleFacebookNetworkJob.class);
    job.setInputFormatClass(KeyValueTextInputFormat.class);

    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(FirstLevelFriendNetReducer.class);
    job.setReducerClass(FirstLevelFriendNetReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
