package com.capstone.recommender.controllers;

import com.capstone.recommender.controllers.rate.RateMapper;
import com.capstone.recommender.controllers.rate.RateReducer;
import com.capstone.recommender.controllers.rate.UserRestaurant;

import com.capstone.recommender.models.CompleteVisit;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;

import org.joda.time.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * @author by sethwiesman on 2/23/15.
 */
public class RateMapReduceTest {


    MapDriver<LongWritable, Text, UserRestaurant, DoubleWritable> mapDriver;
    ReduceDriver<UserRestaurant, DoubleWritable, Text, LongWritable> reduceDriver;

    final long uid = 1;
    final long rid = 2;
    final DateTime now = new DateTime();
    final DateTime lastYear = now.minus(Years.ONE);
    final Days days = Days.daysBetween(lastYear, now);

    final String line = uid + "\t"
            + rid + "\t"
            + lastYear.toString() + "\t"
            + Hours.ONE.toStandardDuration().getMillis() + "\n";

    final CompleteVisit visit = CompleteVisit.parse(line).get();
    final RateMapper.Formula formula = new RateMapper.Formula(now);

    @Before
    public void setup() {
        RateMapper mapper = new RateMapper();
        mapDriver = MapDriver.newMapDriver(mapper);

        RateReducer reducer = new RateReducer();
        reduceDriver = ReduceDriver.newReduceDriver(reducer);

    }

    @Test
    public void testMapper() {
        final long score  = (long)(visit.getDurationInMilliseconds() / Math.log(days.getDays()));
        assertEquals("Score is not as expected", score, formula.calculateScore(visit));
    }

    @Test
    public void testReducer() throws IOException {
        final List<DoubleWritable> list = Arrays.asList(new DoubleWritable(1), new DoubleWritable(2), new DoubleWritable(3));
        final long sum = (long)list.stream().mapToDouble(DoubleWritable::get).sum();

        final UserRestaurant key = new UserRestaurant(1, 2);

        reduceDriver.withInput(key, list);
        reduceDriver.withOutput(new Text(key.toString()), new LongWritable(sum));
        reduceDriver.run();
    }
}
