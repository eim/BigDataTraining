package com.lohika.trainings.big.data.mapreduce;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;
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
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

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

    private RiakClient riakClient;
    private final Namespace ns = new Namespace("default", "facebook");

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      try {
        riakClient = RiakClient.newClient("172.16.238.20");
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        riakClient.shutdown();
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      StringBuilder builder = new StringBuilder();

      for (Text friend: values) {
        builder.append(" ");
        builder.append(friend.toString());
      }

      Location location = new Location(ns, key.toString());

      RiakObject riakObject = new RiakObject();
      riakObject.setValue(BinaryValue.create(builder.toString()));

      StoreValue store = new StoreValue.Builder(riakObject)
        .withLocation(location)
        .withOption(StoreValue.Option.W, new Quorum(3))
        .build();

      try {
        riakClient.execute(store);
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
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
