package com.lohika.trainings.big.data.mapreduce;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eugene on 5/19/16.
 */
public class SiteWritable implements Writable {

    private DoubleWritable sum;
    private Text site;

    public SiteWritable() {
        this(new DoubleWritable(), new Text());
    }

    public SiteWritable(DoubleWritable insum, Text site) {
        this.sum = insum;
        this.site = site;
    }

    public DoubleWritable getSum() {
        return sum;
    }

    public void setSum(DoubleWritable sum) {
        this.sum = sum;
    }

    public Text getSite() {
        return site;
    }

    public void setSite(Text site) {
        this.site = site;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        sum.write(out);
        site.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        sum.readFields(in);
        site.readFields(in);
    }
}
