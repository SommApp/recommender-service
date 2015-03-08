package com.capstone.recommender.controllers.rate;

import com.capstone.recommender.models.CompleteVisit;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import java.io.IOException;
import java.util.Optional;

/**
 * @author by sethwiesman on 2/23/15.
 */
public class RateMapper extends Mapper<LongWritable, Text, UserRestaurant, DoubleWritable> {

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

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

    public static class Formula {

        private final DateTime now;

        public Formula(DateTime now)  {
            this.now = now;
        }

        public long calculateScore(CompleteVisit visit) {
            final Days days = Days.daysBetween(visit.getBeginVisit(), now);
            final long daysSinceVisit = days.getDays();
            int delta = 0;

            if (visit.getDurationInMilliseconds() == daysSinceVisit) {
                delta = 1;
            }

            final double score = (visit.getDurationInMilliseconds() + delta);
            return (long)(score / Math.log(daysSinceVisit));
        }
    }
}
