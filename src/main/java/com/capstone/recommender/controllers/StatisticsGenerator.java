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
            final long numberOfVisits = visits.stream().map(Visit::getUid).count();
            visitsCounts.put(restaurant, numberOfVisits);
        });

        return visitsCounts;
    }

    public Map<Long, Long> uniqueVisitsForRestaurant(Map<Long, List<Visit>> visitsByRestaurants) {

        final Map<Long, Long> uniqueVisitsByRestaurant = new HashMap<>();

        visitsByRestaurants.forEach((restaurant, visits) -> {
            final long visitCount = visits.stream().map(Visit::getUid).distinct().count();
            uniqueVisitsByRestaurant.put(restaurant, visitCount);
        });

        return uniqueVisitsByRestaurant;
    }

    public Map<Long, Map<Integer, Integer>> visitsByMonth(Map<Long, List<Visit>> visits) {
        Map<Long, Map<Integer, Integer>> visitByMonthForRestaurant = new HashMap<>();

        for (Long rid : visits.keySet()) {
            final Map<Integer, List<Visit>> listOfVisitsByMonth = visits.get(rid).stream()
                    .collect(Collectors.groupingBy((elem) -> elem.getDate().getMonthOfYear()));

            final Map<Integer, Integer> numVisitsByMonth = new HashMap<>();

            for (Integer month : listOfVisitsByMonth.keySet()) {
                final List<Visit> visitList = listOfVisitsByMonth.get(month);
                numVisitsByMonth.put(month, visitList.size());
            }

            visitByMonthForRestaurant.put(rid, numVisitsByMonth);
        }

        return visitByMonthForRestaurant;
    }

    public Map<Long, Map<Long, Integer>> frequencyOfVisitLength(Map<Long, List<Visit>> visitsByRestaurants) {

        final Map<Long, Map<Long, Integer>> frequencyOfVisitLengthByRestaurant = new HashMap<>();
        visitsByRestaurants.forEach((restaurant, visits) -> {
            Map<Long, Integer> frequencies = new HashMap<>();

            visits.stream()
                    .map(Visit::getDuration)
                    //.map(StatisticsGenerator::secondsToNearestQuarterHour)
                    .collect(Collectors.groupingBy((val) -> val))
                    .forEach((k, v) -> frequencies.put(k, v.size()));

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
        final Map<Long, Map<Long, Integer>> frequencyOfVisitLengthByRestaurant = frequencyOfVisitLength(visitsByRestaurants);
        final Map<Long, Map<Integer, Integer>> numVisitsByMonthByRestaurant = visitsByMonth(visitsByRestaurants);

        Map<Long, Analytic> analytics = new Hashtable<>();

        for (Long rid : uniqueVisitsByRestaurant.keySet()) {
            final long uniqueVisits = uniqueVisitsByRestaurant.get(rid);
            final long totalVisits = visitsByRestaurant.get(rid);
            final Map<Long, Integer> frequencyOfVisitLengths = frequencyOfVisitLengthByRestaurant.get(rid);
            final Map<Integer, Integer> numVisitsByMonth = numVisitsByMonthByRestaurant.get(rid);

            final Analytic analytic = new Analytic(rid, uniqueVisits, totalVisits, frequencyOfVisitLengths, numVisitsByMonth);
            analytics.put(rid, analytic);
        }

        this.analytics.set(analytics);
    }
}
