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

//import voldemort.client.ClientConfig;
//import voldemort.client.SocketStoreClientFactory;
//import voldemort.client.StoreClient;
//import voldemort.client.StoreClientFactory;

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
//
//    String bootstrapUrl = "tcp://172.16.248.20:6666";
//    static final String STORE_NAME = "test";
//    StoreClientFactory factory = null;
//    StoreClient<Integer, String> client = null;
//
//    @Override
//    protected void setup(Context context) throws IOException, InterruptedException {
//      factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));
//      client = factory.getStoreClient(STORE_NAME);
//    }
//
//    @Override
//    protected void cleanup(Context context) throws IOException, InterruptedException {
//      factory.close();
//    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      StringBuilder sb = new StringBuilder();
      for (Text friend: values) {
        if (sb.length() > 0) sb.append(" ");
        sb.append(friend.toString());
      }
//      client.put(Integer.parseInt(key.toString()),sb.toString());
      context.write(key,new Text(sb.toString()));
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

    job.setNumReduceTasks(10);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
