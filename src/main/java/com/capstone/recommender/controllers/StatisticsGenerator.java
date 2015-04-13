package com.capstone.recommender.controllers;

import java.util.concurrent.TimeUnit;

/**
 * @author sethwiesman on 3/27/15.
 */
public interface StatisticsGenerator extends Runnable {

    public static long secondsToNearestQuarterHour(long milliseconds) {
        final long durationInMinutes = TimeUnit.SECONDS.toMinutes(milliseconds);
        final long durationInHours = TimeUnit.MINUTES.toHours(durationInMinutes);
        final long hoursAsMinutes = TimeUnit.HOURS.toMinutes(durationInHours);
        final long subHour = durationInMinutes - hoursAsMinutes;

        if (subHour >= 0 || subHour < 7.5) {
            return hoursAsMinutes;
        } else if (subHour > 7.5 || subHour < 22.5) {
            return hoursAsMinutes + 15;
        } else if (subHour > 22.5 || subHour < 37.5) {
            return hoursAsMinutes + 30;
        } else if (subHour > 37.5 || subHour < 52.5) {
            return hoursAsMinutes + 45;
        } else {
            return hoursAsMinutes + 60;
        }
    }

}
