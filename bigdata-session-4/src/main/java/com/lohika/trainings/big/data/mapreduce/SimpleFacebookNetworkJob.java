package com.lohika.trainings.big.data.mapreduce;

import org.apache.avro.Schema;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyValueOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

//import voldemort.client.ClientConfig;
//import voldemort.client.SocketStoreClientFactory;
//import voldemort.client.StoreClient;
//import voldemort.client.StoreClientFactory;

public class SimpleFacebookNetworkJob extends Configured implements Tool {
  @Override
  public int run(String[] args) throws Exception {
//    Configuration conf = getConf();
//    conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", " ");

//    JobConf conf = new JobConf(SimpleFacebookNetworkJob.class);

    Configuration conf = getConf();
    conf.setBoolean(MRJobConfig.MAPREDUCE_JOB_USER_CLASSPATH_FIRST, true); // !!!! Necessary
    Job job = Job.getInstance(conf,"Hadoop descriptor job.");

//    Job job = Job.getInstance(getConf(),"Job name");

    job.setJarByClass(SimpleFacebookNetworkJob.class);
    job.setInputFormatClass(KeyValueTextInputFormat.class);

    job.setMapperClass(TokenizerMapper.class);
//    job.setCombinerClass(FirstLevelFriendNetReducer.class);
    job.setReducerClass(FirstLevelFriendNetReducer.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);

    job.setOutputKeyClass(AvroKey.class);
    job.setOutputValueClass(AvroValue.class);

    job.setOutputFormatClass(AvroKeyValueOutputFormat.class);

    job.setNumReduceTasks(10);

    AvroJob.setOutputKeySchema(job, Schema.create(Schema.Type.STRING));
    AvroJob.setOutputValueSchema(job, Schema.create(Schema.Type.STRING));

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    return (job.waitForCompletion(true) ? 0 : 1);
  }

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
    extends Reducer<Text, Text, AvroKey<CharSequence>, AvroValue<CharSequence>> {

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      StringBuilder sb = new StringBuilder();
      for (Text friend: values) {
        if (sb.length() > 0) sb.append(" ");
        sb.append(friend.toString());
      }
      context.write(new AvroKey<CharSequence>(key.toString()), new AvroValue<CharSequence>(sb.toString()));
    }
  }

  public static void main(String[] args) throws Exception {
//    Configuration conf = new Configuration();
//    conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", " ");
//
//    Job job = Job.getInstance(conf, "Simple Facebook network");
//
//    job.setJarByClass(SimpleFacebookNetworkJob.class);
//    job.setInputFormatClass(KeyValueTextInputFormat.class);
//
//    job.setMapperClass(TokenizerMapper.class);
////    job.setCombinerClass(FirstLevelFriendNetReducer.class);
//    job.setReducerClass(FirstLevelFriendNetReducer.class);
//
//    job.setMapOutputKeyClass(Text.class);
//    job.setMapOutputValueClass(Text.class);
//
//    job.setOutputKeyClass(AvroKey.class);
//    job.setOutputValueClass(AvroValue.class);
//
//    job.setNumReduceTasks(10);
//
//    AvroJob.setOutputKeySchema(job, Schema.create(Schema.Type.STRING));
//    AvroJob.setOutputValueSchema(job, Schema.create(Schema.Type.STRING));
//
//    FileInputFormat.addInputPath(job, new Path(args[0]));
//    FileOutputFormat.setOutputPath(job, new Path(args[1]));

//    System.exit(job.waitForCompletion(true) ? 0 : 1);

    int res = ToolRunner.run(new SimpleFacebookNetworkJob(), args);
    System.exit(res);
  }
}
