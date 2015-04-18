package com.capstone.recommender.controllers;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Visit;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author sethwiesman on 3/21/15.
 */

public class RecommendationEngine {

    private final AtomicReference<List<Visit>> visitsReference;
    private final AtomicReference<Map<Long, Set<com.capstone.recommender.models.RecommendedItem>>> recommenderReference;
    private final AtomicReference<Map<Long,Analytic>> analyticsReference;

    private final ScheduledExecutorService executorService;

    public RecommendationEngine() {

        this.visitsReference = new AtomicReference<>(new ArrayList<>());
        this.analyticsReference = new AtomicReference<>(new HashMap<>());

        this.recommenderReference = new AtomicReference<>(new HashMap<>());

        EngineGenerator engineGenerator = EngineGenerator.create(visitsReference, recommenderReference);
        StatisticsGenerator statisticsGenerator = StatisticsGenerator.create(visitsReference, analyticsReference);

        this.executorService = new ScheduledThreadPoolExecutor(2);
        this.executorService.scheduleAtFixedRate(engineGenerator, 1,  2, TimeUnit.MINUTES);
        this.executorService.scheduleAtFixedRate(statisticsGenerator, 1, 2, TimeUnit.MINUTES);
    }

    public Set<com.capstone.recommender.models.RecommendedItem> getRecommendations(int uid) {
        return this.recommenderReference.get().get(uid);
    }

    public void addVisit(Visit visit) {
        this.visitsReference.get().add(visit);
    }

    public Analytic getAnalytics(long rid) {
        return analyticsReference.get().get(rid);
    }
}
