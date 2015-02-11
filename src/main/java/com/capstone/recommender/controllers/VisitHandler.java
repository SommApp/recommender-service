package com.capstone.recommender.controllers;

import com.capstone.recommender.models.PartialVisit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author  sethwiesman 2/10/15.
 */
public class VisitHandler {

    private final Cache<Long, PartialVisit> visitByToken;
    private final AtomicLong tokenGenerator;

    public VisitHandler(CacheBuilder<Long, PartialVisit> visitByTokenBuilder) {
        this.visitByToken = visitByTokenBuilder
                                .expireAfterWrite(3, TimeUnit.HOURS)
                                .removalListener((removalNotification) -> writeVisit(removalNotification.getValue()))
                                .build();

        this.tokenGenerator = new AtomicLong();
    }

    public long beginVisit(long userId, long restaurantId) {
        final long token = tokenGenerator.getAndIncrement();
        final PartialVisit visit = new PartialVisit(userId, restaurantId);

        visitByToken.put(token, visit);

        return token;
    }

    public void endVisit(long token) {
        visitByToken.invalidate(token);
    }

    protected void writeVisit(PartialVisit partialVisit) {
        
    }

}