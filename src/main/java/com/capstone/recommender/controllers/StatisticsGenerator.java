package com.capstone.recommender.controllers;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Visit;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 3/27/15.
 */
public class StatisticsGenerator implements Runnable {

    private List<Long> visits;

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

    public static StatisticsGenerator create(AtomicReference<List<Visit>> visitsReference,
                                             AtomicReference<Map<Long, Analytic>> analytics) {
        return new StatisticsGenerator(visitsReference, analytics);
    }

    private final AtomicReference<List<Visit>> completeVisitsReference;
    private final AtomicReference<Map<Long, Analytic>> analytics;

    public StatisticsGenerator(AtomicReference<List<Visit>> completeVisitsReference,
                                   AtomicReference<Map<Long,Analytic>> analytics) {
        this.completeVisitsReference = completeVisitsReference;
        this.analytics = analytics;
    }

    public Map<Long, Long> visitsForRestaurant(Map<Long, List<Visit>> visitsByRestaurants) {

        final Map<Long, Long> visitsCounts = new HashMap<>();
        visitsByRestaurants.forEach((restaurant, visits) -> {
            final long numberOfVisits = numVisitsForrestaurant(visits);
            visitsCounts.put(restaurant, numberOfVisits);
        });

        return visitsCounts;
    }

    private long numVisitsForrestaurant(List<Visit> visits) {
        return visits.stream().map(Visit::getUid).count();
    }

    public Map<Long, Long> uniqueVisitsForRestaurant(Map<Long, List<Visit>> visitsByRestaurants) {

        final Map<Long, Long> uniqueVisitsByRestaurant = new HashMap<>();

        visitsByRestaurants.forEach((restaurant, visits) -> {
            final long visitCount = visits.stream().map(Visit::getUid).distinct().count();
            uniqueVisitsByRestaurant.put(restaurant, visitCount);
        });

        return uniqueVisitsByRestaurant;
    }

    public Map<Long, Map<String, Float>> visitsByDay(Map<Long, List<Visit>> visitsByRestaurant) {
        Map<Long, Map<String, Float>> visitByDayForRestaurant = new HashMap<>();

        for (Long rid : visitsByRestaurant.keySet()) {
            List<Visit> visits = visitsByRestaurant.get(rid);
            Map<String, Float> numVisitsPerDay = new HashMap<>();
            numVisitsPerDay.put("Monday", 0.05f);
            numVisitsPerDay.put("Tuesday", 0.1f);
            numVisitsPerDay.put("Wednesday", 0.1f);
            numVisitsPerDay.put("Thursday", 0.2f);
            numVisitsPerDay.put("Friday", 0.2f);
            numVisitsPerDay.put("Saturday", 0.25f);
            numVisitsPerDay.put("Sunday", 0.1f);
            visitByDayForRestaurant.put(rid, numVisitsPerDay);
        }

        return visitByDayForRestaurant;
    }


    public Map<Long, Map<Long, Float>> frequencyOfVisitLength(Map<Long, List<Visit>> visitsByRestaurants) {

        final Map<Long, Map<Long, Float>> frequencyOfVisitLengthByRestaurant = new HashMap<>();
        visitsByRestaurants.forEach((restaurant, visits) -> {
            Map<Long, Float> frequencies = new HashMap<>();
            long numVisits = numVisitsForrestaurant(visits);
            visits.stream()
                    .map(Visit::getDuration)
                    .collect(Collectors.groupingBy((val) -> val))
                    .forEach((k, v) -> frequencies.put(k, (float)v.size()/numVisits));

            frequencyOfVisitLengthByRestaurant.put(restaurant, frequencies);
        });

        return frequencyOfVisitLengthByRestaurant;
    }

    @Override
    public void run() {

        final Map<Long, List<Visit>> visitsByRestaurants = completeVisitsReference.get().stream()
                .collect(Collectors.groupingBy(Visit::getRid));

        final Map<Long, Long> uniqueVisitsByRestaurant = uniqueVisitsForRestaurant(visitsByRestaurants);
        final Map<Long, Long> visitsByRestaurant = visitsForRestaurant(visitsByRestaurants);
        final Map<Long, Map<Long, Float>> frequencyOfVisitLengthByRestaurant = frequencyOfVisitLength(visitsByRestaurants);
        final Map<Long, Map<String, Float>> numVisitsByMonthByRestaurant = visitsByDay(visitsByRestaurants);

        Map<Long, Analytic> analytics = new Hashtable<>();

        for (Long rid : uniqueVisitsByRestaurant.keySet()) {
            final long uniqueVisits = uniqueVisitsByRestaurant.get(rid);
            final long totalVisits = visitsByRestaurant.get(rid);
            final Map<Long, Float> frequencyOfVisitLengths = frequencyOfVisitLengthByRestaurant.get(rid);
            final Map<String, Float> numVisitsByMonth = numVisitsByMonthByRestaurant.get(rid);

            final Analytic analytic = new Analytic(rid, uniqueVisits, totalVisits, frequencyOfVisitLengths, numVisitsByMonth);
            analytics.put(rid, analytic);
        }

        this.analytics.set(analytics);
    }
}
