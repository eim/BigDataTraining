package com.lohika.trainings.big.data.mapreduce;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
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
import java.util.ArrayList;
import java.util.List;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;

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

    String bootstrapUrl = "tcp://172.16.248.20:6666";
    StoreClientFactory factory = null;//new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));
    StoreClient<String, GenericData.Array<GenericRecord>> client = null;
    static final String valueSchemaJson = "{\n" +
        "  \"type\": \"array\",\n" +
        "  \"items\":{\n" +
        "              \"name\":\"Child\",\n" +
        "              \"type\":\"record\",\n" +
        "              \"fields\":[\n" +
        "                  {\"name\":\"id\", \"type\":\"int\"},\n" +
        "                  {\"name\":\"name\", \"type\":\"string\"}\n" +
        "              ]\n" +
        "          }\n" +
        "}";
    static final String recordSchemaJson = "{\n" +
          "              \"name\":\"Child\",\n" +
          "              \"type\":\"record\",\n" +
          "              \"fields\":[\n" +
          "                  {\"name\":\"id\", \"type\":\"int\"},\n" +
          "                  {\"name\":\"name\", \"type\":\"string\"}\n" +
          "              ]\n" +
          "          }";

    Schema valueSchema = null;
    Schema recordSchema = null;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));
      client = factory.getStoreClient("test");
      valueSchema = new Schema.Parser().parse(valueSchemaJson);
      recordSchema = new Schema.Parser().parse(recordSchemaJson);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      factory.close();
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

      List<GenericRecord> genList = new ArrayList<GenericRecord>();
      GenericRecord person1 = null;
      int index = 1;
      for (Text friend: values) {
        person1 = new GenericData.Record(recordSchema);
        person1.put("id",Integer.valueOf(index++));
        person1.put("name",friend.toString());
        genList.add(person1);
      }

      GenericData.Array<GenericRecord> value = new GenericData.Array<GenericRecord>(valueSchema,genList);


      client.put("test",value);

      client.get("test");

//      Location location = new Location(ns, key.toString());
//
//      RiakObject riakObject = new RiakObject();
//      riakObject.setValue(BinaryValue.create(builder.toString()));
//
//      StoreValue store = new StoreValue.Builder(riakObject)
//        .withLocation(location)
//        .withOption(StoreValue.Option.W, new Quorum(3))
//        .build();
//
//      try {
//        riakClient.execute(store);
//      } catch (ExecutionException e) {
//        throw new RuntimeException(e);
//      }
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
