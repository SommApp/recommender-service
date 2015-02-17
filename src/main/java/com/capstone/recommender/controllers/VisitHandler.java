/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler {

    private final Cache<Long, PartialVisit> visitByToken;
    private final OutputStream writer;
    private final AtomicLong tokenGenerator;

    @Inject
    public VisitHandler(CacheBuilder<Long, PartialVisit> visitByTokenBuilder, OutputStream writer) {
        this.visitByToken = visitByTokenBuilder
                                .expireAfterWrite(3, TimeUnit.HOURS)
                                .removalListener((removalNotification) -> writeVisit(removalNotification.getValue()))
                                .build();

        //TODO Find a better way of doing this
        this.writer = writer;
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
        final CompleteVisit completeVisit = new CompleteVisit(partialVisit);
        try {
            writer.write(completeVisit.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}