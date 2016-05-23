package com.lohika.trainings.big.data.mapreduce;

import com.lohika.trainings.big.data.mapreduce.facebook.network.FirstLevelMapper;
import com.lohika.trainings.big.data.mapreduce.facebook.network.FirstLevelReducer;
import com.lohika.trainings.big.data.mapreduce.facebook.network.IntArrayWritable;
import com.lohika.trainings.big.data.mapreduce.facebook.network.SecondLevelMapper;
import com.lohika.trainings.big.data.mapreduce.facebook.network.SecondLevelReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FacebookNetworkTopology {

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();

    Job job1 = Job.getInstance(conf, "Facebook first level network");

    job1.setJarByClass(FacebookNetworkTopology.class);

    job1.setMapperClass(FirstLevelMapper.class);
    job1.setCombinerClass(FirstLevelReducer.class);
    job1.setReducerClass(FirstLevelReducer.class);

    job1.setOutputKeyClass(IntWritable.class);
    job1.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job1, new Path(args[0]));
    FileOutputFormat.setOutputPath(job1, new Path(args[1]));

    if (!job1.waitForCompletion(true)) {
      System.exit(1);
    }

    Job job2 = Job.getInstance(conf, "Facebook second level network");

    job2.setJarByClass(FacebookNetworkTopology.class);

    job2.setMapperClass(SecondLevelMapper.class);
    job2.setReducerClass(SecondLevelReducer.class);

    job2.setMapOutputKeyClass(IntWritable.class);
    job2.setMapOutputValueClass(IntArrayWritable.class);
    job2.setOutputKeyClass(IntWritable.class);
    job2.setOutputValueClass(Text.class);

    job2.setNumReduceTasks(3);

    FileInputFormat.addInputPath(job2, new Path(args[1] + "/part-r-00000"));
    FileOutputFormat.setOutputPath(job2, new Path(args[2]));

    System.exit(job2.waitForCompletion(true) ? 0 : 1);
  }
}
