package com.capstone.recommender.controllers;

import com.capstone.recommender.controllers.Impls.EngineGeneratorFactory;

import com.capstone.recommender.controllers.Impls.StatisticsGeneratorFactory;
import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.CompleteVisit;

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

    private final AtomicReference<List<CompleteVisit>> completeVisitsReference;
    private final AtomicReference<Recommender> recommenderReference;
    private final AtomicReference<Map<Long,Analytic>> analyticsReference;

    private final ScheduledExecutorService executorService;

    public RecommendationEngine(EngineGeneratorFactory engineGeneratorFactory,
                                StatisticsGeneratorFactory statisticsGeneratorFactory) {

        this.completeVisitsReference = new AtomicReference<>(new ArrayList<>());
        this.recommenderReference = new AtomicReference<>();
        this.analyticsReference = new AtomicReference<>(new HashMap<>());

        EngineGenerator engineGenerator = engineGeneratorFactory
                .create(completeVisitsReference, recommenderReference);

        StatisticsGenerator statisticsGenerator = statisticsGeneratorFactory
                .create(completeVisitsReference, analyticsReference);

        this.executorService = new ScheduledThreadPoolExecutor(2);
        this.executorService.scheduleAtFixedRate(engineGenerator, 1,  5, TimeUnit.MINUTES);
        this.executorService.scheduleAtFixedRate(statisticsGenerator, 1, 5, TimeUnit.MINUTES);
    }

    public List<RecommendedItem> getRecommendations(int uid) {
        try {
            return this.recommenderReference.get().recommend(40, uid);
        } catch (TasteException e) {
            return null;
        }
    }

    public void addVisit(CompleteVisit visit) {
        this.completeVisitsReference.get().add(visit);
    }

    public Analytic getAnalytics(long rid) {
        return analyticsReference.get().get(rid);
    }
}
