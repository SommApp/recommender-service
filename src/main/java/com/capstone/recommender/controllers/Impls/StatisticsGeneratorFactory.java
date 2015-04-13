package com.capstone.recommender.controllers.Impls;

import com.capstone.recommender.controllers.StatisticsGenerator;
import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Visit;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 3/27/15.
 */
public class StatisticsGeneratorFactory {

    public StatisticsGenerator create(AtomicReference<List<Visit>> visitsReference,
                                      AtomicReference<Map<Long, Analytic>> analytics) {
        return new StatisticsGeneratorImpl(visitsReference, analytics);
    }

    private class StatisticsGeneratorImpl implements StatisticsGenerator {

        private final AtomicReference<List<Visit>> completeVisitsReference;
        private final AtomicReference<Map<Long, Analytic>> analytics;

        public StatisticsGeneratorImpl(AtomicReference<List<Visit>> completeVisitsReference,
                                       AtomicReference<Map<Long,Analytic>> analytics) {
            this.completeVisitsReference = completeVisitsReference;
            this.analytics = analytics;
        }

        public Map<Long, Long> visitsForRestaurant(Map<Long, List<Visit>> visitsByRestaurants) {

            final Map<Long, Long> visitsByRestaurant = new HashMap<>();
            visitsByRestaurants.forEach((restaurant, visits) -> {
                final long visitCount = visits.stream().map(Visit::getUid).count();
                visitsByRestaurant.put(restaurant, visitCount);
            });

            return visitsByRestaurant;
        }

        public Map<Long, Long> uniqueVisitsForRestaurant(Map<Long, List<Visit>> visitsByRestaurants) {

            final Map<Long, Long> uniqueVisitsByRestaurant = new HashMap<>();

            visitsByRestaurants.forEach((restaurant, visits) -> {
                final long visitCount = visits.stream().map(Visit::getUid).distinct().count();
                uniqueVisitsByRestaurant.put(restaurant, visitCount);
            });

            return uniqueVisitsByRestaurant;
        }

        public Map<Long, Map<Long, Integer>> frequencyOfVisitLength(Map<Long, List<Visit>> visitsByRestaurants) {

            final Map<Long, Map<Long, Integer>> frequencyOfVisitLengthByRestaurant = new HashMap<>();

            visitsByRestaurants.forEach((restaurant, visits) -> {
                Map<Long, Integer> frequencies = new HashMap<>();

                visits.stream().map(Visit::getDuration)
                        .map(StatisticsGenerator::secondsToNearestQuarterHour)
                        .collect(Collectors.groupingBy(Long::longValue))
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

            Map<Long, Analytic> analytics = new Hashtable<>();

            for (Long rid : uniqueVisitsByRestaurant.keySet()) {
                final long uniqueVisits = uniqueVisitsByRestaurant.get(rid);
                final long totalVisits = visitsByRestaurant.get(rid);
                final Map<Long, Integer> frequencyOfVisitLengths = frequencyOfVisitLengthByRestaurant.get(rid);

                final Analytic analytic = new Analytic(rid, uniqueVisits, totalVisits, frequencyOfVisitLengths);
                analytics.put(analytic.getRestaurantId(), analytic);
            }

            this.analytics.set(analytics);
        }
    }

}
