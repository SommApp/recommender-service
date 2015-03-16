package com.capstone.recommender.controllers.rate;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.capstone.recommender.RecommenderConstants;
import com.capstone.recommender.controllers.rate.score.mapreduce.RateMapper;
import com.capstone.recommender.controllers.rate.score.mapreduce.RateReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
/**
 * @author by sethwiesman on 3/8/15.
 */
public class RecommendationEngine implements Runnable{

    private final ScoreGenerator scoreGenerator;
    private final ScheduledExecutorService executorService;
    final AtomicReference<Optional<String>> outputFile;

    public RecommendationEngine()  {
        this.outputFile = new AtomicReference<>(Optional.<String>empty());
        this.scoreGenerator = new ScoreGenerator(outputFile);

        this.executorService = new ScheduledThreadPoolExecutor(1);
        this.executorService.scheduleWithFixedDelay(this, 0, 24, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        this.scoreGenerator.runMapReduce();
    }

    private static class ScoreGenerator  {
        private final static String outputPrefix = "ratings/users/";

        final AtomicReference<Optional<String>> outputFile;
        final DateTimeFormatter formatter;
        final Configuration conf;

        public ScoreGenerator(AtomicReference<Optional<String>> outputFile) {
            this.formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
            this.conf = new Configuration();
            this.outputFile = outputFile;
        }

        public void runMapReduce() {
            final String newOutputFile = generateOutputFileName();

            try {
                Job job = new Job(conf);
                job.setJarByClass(ScoreGenerator.class);
                job.setMapperClass(RateMapper.class);
                job.setReducerClass(RateReducer.class);
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(LongWritable.class);


                job.setMapOutputKeyClass(UserRestaurant.class);
                job.setMapOutputValueClass(DoubleWritable.class);

                FileInputFormat.addInputPath(job, new Path(RecommenderConstants.VISIT_FILE));
                FileOutputFormat.setOutputPath(job, new Path(newOutputFile));

                boolean jobWasSuccessful = job.waitForCompletion(true);

                if (jobWasSuccessful) {
                    outputFile.set(Optional.of(newOutputFile));
                }

            } catch (IOException ioe) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public String generateOutputFileName() {
            DateTime now = new DateTime();
            StringBuilder fileName = new StringBuilder(outputPrefix);
            return fileName + formatter.print(now);
        }

    }
}

