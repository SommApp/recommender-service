package com.capstone.recommender.controllers;

import java.util.concurrent.TimeUnit;

/**
 * @author sethwiesman on 3/27/15.
 */
public interface StatisticsGenerator extends Runnable {

    public static long secondsToNearestQuarterHour(long seconds) {
        final long minutes = TimeUnit.SECONDS.toMinutes(seconds);

        long subHour = minutes;
        while (subHour > 60) {
            subHour -= 60;
        }

        long roundedMinutes;
        if (subHour < 7.5) {
            roundedMinutes = 0;
        } else if (subHour > 7.5 && subHour < 22.5) {
            roundedMinutes = 15;
        } else if (subHour > 22.5 && subHour < 37.5) {
            roundedMinutes = 30;
        } else if (subHour > 37.5 && subHour < 52.5){
            roundedMinutes = 45;
        } else {
            roundedMinutes = 60;
        }

        return minutes - subHour + roundedMinutes;
    }

}
