package com.capstone.recommender.controllers;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Visit;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author sethwiesman on 3/21/15.
 */

public class RecommendationEngine {

    private final AtomicReference<List<Visit>> visitsReference;
    private final AtomicReference<Recommender> recommenderReference;
    private final AtomicReference<Map<Long,Analytic>> analyticsReference;

    private final ScheduledExecutorService executorService;

    public RecommendationEngine() {

        this.visitsReference = new AtomicReference<>(new ArrayList<>());
        this.analyticsReference = new AtomicReference<>(new HashMap<>());

        this.recommenderReference = new AtomicReference<>();

        EngineGenerator engineGenerator = EngineGenerator.create(visitsReference, recommenderReference);
        StatisticsGenerator statisticsGenerator = StatisticsGenerator.create(visitsReference, analyticsReference);

        this.executorService = new ScheduledThreadPoolExecutor(2);
        this.executorService.scheduleAtFixedRate(engineGenerator, 1,  5, TimeUnit.MINUTES);
        this.executorService.scheduleAtFixedRate(statisticsGenerator, 1, 5, TimeUnit.MINUTES);
    }

    public List<RecommendedItem> getRecommendations(int uid) {
        try {
            return this.recommenderReference.get().recommend(uid, 20);
        } catch (TasteException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public void addVisit(Visit visit) {
        this.visitsReference.get().add(visit);
    }

    public Analytic getAnalytics(long rid) {
        return analyticsReference.get().get(rid);
    }
}
