package com.capstone.recommender.controllers.Impls;

import com.capstone.recommender.controllers.EngineGenerator;
import com.capstone.recommender.models.Visit;

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
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 3/27/15.
 */

public class EngineGeneratorFactory {

    public EngineGenerator create(AtomicReference<List<Visit>> visitReference,
                                  AtomicReference<Recommender> recommenderReference) {
        return new EngineGeneratorImpl(visitReference, recommenderReference);
    }

    private class EngineGeneratorImpl implements EngineGenerator {

        private final AtomicReference<List<Visit>> completeVisitsReference;
        private final AtomicReference<Recommender> recommenderReference;

        private EngineGeneratorImpl(AtomicReference<List<Visit>> completeVisitReference,
                                    AtomicReference<Recommender> recommenderReference) {
            this.completeVisitsReference = completeVisitReference;
            this.recommenderReference = recommenderReference;
        }

        @Override
        public void run() {
            final FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();
            final Map<Long, List<Visit>> listOfVisitsByUsers = completeVisitsReference.get().stream()
                    .collect(Collectors.groupingBy(Visit::getUid));

            final AtomicInteger indexReference = new AtomicInteger(0);

            listOfVisitsByUsers.forEach((user, listOfVisits) -> {
                final int index = indexReference.incrementAndGet();

                final Map<Long, List<Visit>> visitsByRestaurant = listOfVisits.stream()
                        .collect(Collectors.groupingBy(Visit::getRid));

                final int numberOfRestaurantsVisited = visitsByRestaurant.keySet().size();
                final PreferenceArray preferencesForUser = new GenericUserPreferenceArray(numberOfRestaurantsVisited);

                preferencesForUser.setUserID(index, user);

                visitsByRestaurant.forEach((restaurant, restaurants) -> {
                    final long score = restaurants.stream().map(Visit::getScore).reduce(0L, Long::sum);
                    preferencesForUser.setItemID(index, restaurant);
                    preferencesForUser.setValue(index, score);
                });

                preferences.put(user, preferencesForUser);
            });

            try {
                final DataModel dataModel = new GenericDataModel(preferences);
                final UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
                final UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, userSimilarity, dataModel);
                final Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
                recommenderReference.set(recommender);
            } catch (TasteException e) {
                e.printStackTrace();
            }
        }
    }
}