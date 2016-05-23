package com.lohika.trainings.big.data.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.parquet.writable.BigDecimalWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeWorkJSONJob {

  public static class HWJSONMapper extends Mapper<Object, Text, IntWritable, SiteWritable> {

    private IntWritable userId = new IntWritable();
    private SiteWritable payment = new SiteWritable();
    private Pattern linePattern =
        Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} (\\d+) ([\\d.]+) (.+)");

    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      Matcher matcher = linePattern.matcher(value.toString());

      if (!matcher.find()) {
        return;
      }

      userId.set(Integer.parseInt(matcher.group(1)));
      payment.setSum(new DoubleWritable((new BigDecimal(matcher.group(2)).doubleValue())));
      payment.setSite(new Text(matcher.group(3)));

      context.write(userId, payment);
    }
  }

  public static class HWJSONReducer extends Reducer<IntWritable, SiteWritable, Text, NullWritable> {


    private SiteWritable paymentWritable = new SiteWritable();
    private Text sites = new Text();
    private StringBuilder sb = new StringBuilder();

    public void reduce(IntWritable key, Iterable<SiteWritable> values, Context context)
        throws IOException, InterruptedException {
      BigDecimal sum = BigDecimal.ZERO;

      for (SiteWritable payment : values) {
        sum = sum.add(BigDecimal.valueOf(payment.getSum().get()));
        sb.append(new String(payment.getSite().getBytes()));
      }

      paymentWritable.setSum(new DoubleWritable(sum.doubleValue()));
      paymentWritable.setSite(new Text(sb.toString()));


      JSONObject output = new JSONObject();
      try {
        output.append("id", Integer.valueOf(key.get()));
        output.append("total", Double.valueOf(paymentWritable.getSum().get()));
        StringBuilder sb =
            new StringBuilder("[").append(paymentWritable.getSite().toString()).append("]");
        output.append("stores", sb.toString());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      context.write(new Text(output.toString()), null);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();

    Job job = Job.getInstance(conf, "Customer payments");
    job.setJarByClass(HomeWorkJSONJob.class);

    job.setMapperClass(HWJSONMapper.class);
    job.setCombinerClass(HWJSONReducer.class);
    job.setReducerClass(HWJSONReducer.class);

    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(SiteWritable.class);


    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
