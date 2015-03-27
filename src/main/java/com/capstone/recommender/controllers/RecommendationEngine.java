package com.capstone.recommender.controllers;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.CompleteVisit;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 3/21/15.
 */

public class RecommendationEngine {

    private final AtomicReference<List<CompleteVisit>> completeVisitsReference;
    private final AtomicReference<Recommender> recommenderAtomicReference;

    private final ScheduledExecutorService executorService;

    public RecommendationEngine() {
        this.completeVisitsReference = new AtomicReference<>(new ArrayList<>());
        this.recommenderAtomicReference = new AtomicReference<>();

        EngineGenerator engineGenerator = new EngineGenerator();
        StatisticsGenerator statisticsGenerator = new StatisticsGenerator();

        this.executorService = new ScheduledThreadPoolExecutor(2);
        this.executorService.scheduleAtFixedRate(engineGenerator, 1,  5, TimeUnit.MINUTES);
        this.executorService.scheduleAtFixedRate(statisticsGenerator, 1, 5, TimeUnit.MINUTES);
    }

    public List<RecommendedItem> getRecommendations(int uid) {
        try {
            return this.recommenderAtomicReference.get().recommend(40, uid);
        } catch (TasteException e) {
            return null;
        }
    }

    public void addVisit(CompleteVisit visit) {
        this.completeVisitsReference.get().add(visit);
    }

    private class EngineGenerator implements Runnable{
        @Override
        public void run() {
            final FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();
            final Map<Long, List<CompleteVisit>> listOfVisitsByUsers = completeVisitsReference.get().stream()
                    .collect(Collectors.groupingBy(CompleteVisit::getUid));

            int index = 0;

            for (Long user : listOfVisitsByUsers.keySet()) {
                final List<CompleteVisit> listOfVisits = listOfVisitsByUsers.get(user);
                final Map<Long, List<CompleteVisit>> visitsByRestaurant = listOfVisits.stream()
                        .collect(Collectors.groupingBy(CompleteVisit::getRid));

                final PreferenceArray preferencesForUser = new GenericUserPreferenceArray(visitsByRestaurant.keySet().size());
                preferencesForUser.setUserID(index, user);
                for (Long restaurant : visitsByRestaurant.keySet()) {
                    final List<CompleteVisit> listOfRestaurants = visitsByRestaurant.get(restaurant);
                    final long score = listOfRestaurants.stream()
                            .map(CompleteVisit::getScore)
                            .reduce(0L, (accumulator, _item) -> accumulator + _item);

                    preferencesForUser.setItemID(index, restaurant);
                    preferencesForUser.setValue(index, score);
                }

                preferences.put(user, preferencesForUser);
                index++;
            }

            try {
                final DataModel dataModel = new GenericDataModel(preferences);
                final UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
                final UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, userSimilarity, dataModel);
                final Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
                recommenderAtomicReference.set(recommender);
            } catch (TasteException e) {
                e.printStackTrace();
            }
        }
    }

    private class StatisticsGenerator implements Runnable {

        List<Analytic> analytics = null;

        public Map<Long, Long> uniqueVisitsForRestaurant(Map<Long, List<CompleteVisit>> visitsByRestaurants) {

            final Map<Long, Long> uniqueVisitsByRestaurant = new HashMap<>();
            for(Long key : visitsByRestaurants.keySet()) {
                final List<CompleteVisit> visits = visitsByRestaurants.get(key);
                final long visitCount = visits.stream().map(CompleteVisit::getUid).distinct().count();
                uniqueVisitsByRestaurant.put(key, visitCount);
            }

            return uniqueVisitsByRestaurant;
        }

        public Map<Long, Long> visitsForRestaurant(Map<Long, List<CompleteVisit>> visitsByRestaurants) {

            final Map<Long, Long> visitsByRestaurant = new HashMap<>();
            for(Long key : visitsByRestaurants.keySet()) {
                final List<CompleteVisit> visits = visitsByRestaurants.get(key);
                final long visitCount = visits.stream().map(CompleteVisit::getUid).count();
                visitsByRestaurant.put(key, visitCount);
            }

            return visitsByRestaurant;
        }

        public Map<Long, Map<Long, Integer>> frequencyOfVisitLength(Map<Long, List<CompleteVisit>> visitsByRestaurants) {

            final Map<Long, Map<Long, Integer>> frequencyOfVisitLengthByRestaurant = new HashMap<>();
            for (Long key : frequencyOfVisitLengthByRestaurant.keySet()) {
                final List<CompleteVisit> visits = visitsByRestaurants.get(key);
                final List<Long> visitTimes = visits.stream().map(CompleteVisit::getDuration)
                        .map(this::millisecondsToNearestQuarterHour)
                        .collect(Collectors.toList());

                Map<Long, Integer> frequencies = new HashMap<>();
                visitTimes.stream().collect(Collectors.groupingBy(Long::longValue))
                        .forEach((k, v) -> frequencies.put(k, v.size()));

                frequencyOfVisitLengthByRestaurant.put(key, frequencies);
            }

            return frequencyOfVisitLengthByRestaurant;
        }

        private long millisecondsToNearestQuarterHour(long milliseconds) {
            return 900000L * ((milliseconds + 450000L) / 900000L);
        }

        @Override
        public void run() {

            final Map<Long, List<CompleteVisit>> visitsByRestaurants = completeVisitsReference.get().stream()
                    .collect(Collectors.groupingBy(CompleteVisit::getRid));

            final Map<Long, Long> uniqueVisitsByRestaurant = uniqueVisitsForRestaurant(visitsByRestaurants);
            final Map<Long, Long> visitsByRestaurant = visitsForRestaurant(visitsByRestaurants);
            final Map<Long, Map<Long, Integer>> frequencyOfVisitLengthByRestaurant = frequencyOfVisitLength(visitsByRestaurants);

            List<Analytic> analytics = new ArrayList<>();

            for (Long rid : uniqueVisitsByRestaurant.keySet()) {
                final long uniqueVisits = uniqueVisitsByRestaurant.get(rid);
                final long totalVisits = visitsByRestaurant.get(rid);
                final Map<Long, Integer> frequencyOfVisitLengths = frequencyOfVisitLengthByRestaurant.get(rid);

                final Analytic analytic = new Analytic(rid, uniqueVisits, totalVisits, frequencyOfVisitLengths);
                analytics.add(analytic);
            }

            this.analytics = analytics;
        }
    }
}
