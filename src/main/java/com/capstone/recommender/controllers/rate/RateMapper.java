package com.capstone.recommender.controllers.rate;

import com.capstone.recommender.models.CompleteVisit;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.IOException;
import java.util.Optional;

/**
 * @author by sethwiesman on 2/23/15.
 */
public class RateMapper extends Mapper<Object, Text, UserRestaurant, DoubleWritable> {

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        final Optional<CompleteVisit> possibleVisit = CompleteVisit.parse(value.toString());

        if (!possibleVisit.isPresent()) {
            return;
        }

        final CompleteVisit visit = possibleVisit.get();
        final Period minus = new Period(visit.getBeginVisit(), new DateTime());
        int delta = 0;
        if (visit.getDurationInMilliseconds() == minus.getMillis()) {
            delta = 1;
        }

        final double score = (visit.getDurationInMilliseconds() + delta) / Math.log(minus.getMillis());
        context.write(new UserRestaurant(visit.getUserId(), visit.getRestaurantId()), new DoubleWritable(score));

    }
}
