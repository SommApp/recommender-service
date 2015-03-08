package com.capstone.recommender.controllers.rate;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author by sethwiesman on 2/23/15.
 */
public class RateReducer extends Reducer<UserRestaurant, DoubleWritable, Text, LongWritable> {

    @Override
    public void reduce(UserRestaurant key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        long score = 0;

        for(DoubleWritable value : values) {
            score += (long)value.get();
        }

        context.write(new Text(key.toString()), new LongWritable(score));
    }
}
