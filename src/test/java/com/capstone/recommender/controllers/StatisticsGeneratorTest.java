package com.capstone.recommender.controllers;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

/**
 * @author sethwiesman on 4/14/15.
 */
public class StatisticsGeneratorTest {

    @Test
    public void roundToZero() {
        for (int i = 0; i < 7.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 0", 0L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundToFifteen() {
        for (int i = 8; i < 22.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 15", 15L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundToThirty() {
        for (int i = 23; i < 37.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 30", 30L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundToFortyFive() {
        for (int i = 38; i < 52.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 45", 45L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundTo60() {
        for (int i = 53; i < 67; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 60", 60L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundTo75() {
        for (int i = 68; i < 82.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 75", 75L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundToNinety() {
        for (int i = 83; i < 97.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 90", 90L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundTo105() {
        for (int i = 98; i < 112.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded to 105", 105L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }

    @Test
    public void roundTo120() {
        for (int i = 113; i < 127.5; i++) {
            final long minutes = TimeUnit.MINUTES.toSeconds(i);
            assertEquals("Should have rounded " + i + " to 120", 120L,
                    StatisticsGenerator.secondsToNearestQuarterHour(minutes));
        }
    }
}
