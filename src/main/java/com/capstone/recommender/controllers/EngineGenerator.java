package com.capstone.recommender.controllers;

import com.capstone.recommender.models.RecommendedItem;
import com.capstone.recommender.models.Visit;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;

import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.*;

import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 3/27/15.
 */
public class EngineGenerator implements Runnable {
    public static EngineGenerator create(AtomicReference<List<Visit>> visitReference,
                                  AtomicReference<Map<Long, Set<RecommendedItem>>> recommenderReference) {
        return new EngineGenerator(visitReference, recommenderReference);
    }

    private final AtomicReference<List<Visit>> completeVisitsReference;
    private final AtomicReference<Map<Long, Set<RecommendedItem>>> recommenderReference;

    private EngineGenerator(AtomicReference<List<Visit>> completeVisitReference,
                                AtomicReference<Map<Long, Set<RecommendedItem>>> recommenderReference) {
        this.completeVisitsReference = completeVisitReference;
        this.recommenderReference = recommenderReference;
    }

    public Map<Long, List<Visit>> getVisitsByUid(List<Visit> visits) {
        return visits.stream().collect(Collectors.groupingBy(Visit::getUid));
    }

    public Set<Long> findAllRestaurants(List<Visit> visits) {
        return visits.stream().map(Visit::getRid).collect(Collectors.toSet());
    }

    @Override
    public void run() {
        Map<Long, Set<RecommendedItem>> recommendations = new HashMap<>();
        List<Visit> visits = completeVisitsReference.get();

        Set<Long> restaurants = findAllRestaurants(visits);
        Map<Long, List<Visit>> visitsByUid = visits.stream().collect(Collectors.groupingBy(Visit::getUid));

        for (Long uid : visitsByUid.keySet()) {
            Set<Long> rids = findAllRestaurants(visitsByUid.get(uid));
            Set<Long> recs = new HashSet<>(restaurants);
            recs.removeAll(rids);

            recommendations.put(uid, recs.stream().map(RecommendedItem::new).collect(Collectors.toSet()));
            /**
            System.out.print(uid);
            for (Long key : recs) {
                System.out.print("\t" + key);
            }
            System.out.println();*/
        }

        recommenderReference.set(recommendations);
        System.out.println("************* updated ************");
    }

}
