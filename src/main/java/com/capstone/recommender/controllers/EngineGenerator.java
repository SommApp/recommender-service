package com.capstone.recommender.controllers;

import com.capstone.recommender.models.Visit;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;

import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.*;

import org.apache.mahout.cf.taste.impl.recommender.knn.ConjugateGradientOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 3/27/15.
 */
public class EngineGenerator implements Runnable {
    public static EngineGenerator create(AtomicReference<List<Visit>> visitReference,
                                  AtomicReference<Recommender> recommenderReference) {
        return new EngineGenerator(visitReference, recommenderReference);
    }

    private final AtomicReference<List<Visit>> completeVisitsReference;
    private final AtomicReference<Recommender> recommenderReference;

    private EngineGenerator(AtomicReference<List<Visit>> completeVisitReference,
                                AtomicReference<Recommender> recommenderReference) {
        this.completeVisitsReference = completeVisitReference;
        this.recommenderReference = recommenderReference;
    }

    public Map<Long, List<Visit>> getVisitsByUid(List<Visit> visits) {
        return visits.stream().collect(Collectors.groupingBy(Visit::getUid));
    }

    @Override
    public void run() {
        final FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();
        final Map<Long, List<Visit>> listOfVisitsByUsers = getVisitsByUid(completeVisitsReference.get());

        int index = 0;
        for (Long user : listOfVisitsByUsers.keySet()) {
            final List<Visit> listOfVisits = listOfVisitsByUsers.get(user);

            final Map<Long, List<Visit>> visitsByRestaurant = listOfVisits.stream()
                    .collect(Collectors.groupingBy(Visit::getRid));

            final int numberOfRestaurantsVisited = visitsByRestaurant.keySet().size();
            final PreferenceArray preferencesForUser = new GenericUserPreferenceArray(numberOfRestaurantsVisited * 2);

            preferencesForUser.setUserID(index, user);

            for (Long restaurant : visitsByRestaurant.keySet()) {
                final List<Visit> restaurants = visitsByRestaurant.get(restaurant);
                final long score = restaurants.stream().map(Visit::getScore).reduce(0L, Long::sum);
                if (score == 0) {
                    continue;
                }
                preferencesForUser.setItemID(index, restaurant);
                preferencesForUser.setValue(index, score);
            }

            preferences.put(user, preferencesForUser);
            index += 1;
        }
        try{
            final DataModel dataModel = new GenericDataModel(preferences);
            final UserSimilarity similarity = new TanimotoCoefficientSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, dataModel);

            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            recommenderReference.set(new CachingRecommender(recommender));
        } catch (TasteException e) {
            e.printStackTrace();
        }
    }

}
