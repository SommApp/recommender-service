package com.capstone.recommender.controllers.Impls;

import com.capstone.recommender.controllers.EngineGenerator;
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

    public EngineGenerator create(AtomicReference<List<CompleteVisit>> completeVisitReference,
                                  AtomicReference<Recommender> recommenderReference) {
        return new EngineGeneratorImpl(completeVisitReference, recommenderReference);
    }

    private class EngineGeneratorImpl implements EngineGenerator {

        private final AtomicReference<List<CompleteVisit>> completeVisitsReference;
        private final AtomicReference<Recommender> recommenderReference;

        private EngineGeneratorImpl(AtomicReference<List<CompleteVisit>> completeVisitReference,
                                    AtomicReference<Recommender> recommenderReference) {
            this.completeVisitsReference = completeVisitReference;
            this.recommenderReference = recommenderReference;
        }

        @Override
        public void run() {
            final FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();
            final Map<Long, List<CompleteVisit>> listOfVisitsByUsers = completeVisitsReference.get().stream()
                    .collect(Collectors.groupingBy(CompleteVisit::getUid));

            final AtomicInteger indexReference = new AtomicInteger(0);

            listOfVisitsByUsers.forEach((user, listOfVisits) -> {
                final int index = indexReference.incrementAndGet();

                final Map<Long, List<CompleteVisit>> visitsByRestaurant = listOfVisits.stream()
                        .collect(Collectors.groupingBy(CompleteVisit::getRid));

                final int numberOfRestaurantsVisited = visitsByRestaurant.keySet().size();
                final PreferenceArray preferencesForUser = new GenericUserPreferenceArray(numberOfRestaurantsVisited);

                preferencesForUser.setUserID(index, user);

                visitsByRestaurant.forEach((restaurant, restaurants) -> {
                    final long score = restaurants.stream().map(CompleteVisit::getScore).reduce(0L, Long::sum);
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