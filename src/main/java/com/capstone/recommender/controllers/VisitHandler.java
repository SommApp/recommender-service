/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler {

    private final Map<Long, PartialVisit> visitByToken;
    private final AtomicLong tokenGenerator;
    private final ArrayList<CompleteVisit> completeVisits;

    public VisitHandler() {
        this.visitByToken = new ConcurrentHashMap<>();
        this.tokenGenerator = new AtomicLong();
        this.completeVisits = new ArrayList<>();
    }

    public long beginVisit(long uid, long rid) {
        final long token = tokenGenerator.getAndIncrement();
        final PartialVisit visit = new PartialVisit(uid, rid);
        this.visitByToken.put(token, visit);
        return token;
    }

    public boolean endVisit(long token) {
        final PartialVisit visit = this.visitByToken.remove(token);
        if (visit == null) {
            return false;
        }

        final CompleteVisit completeVisit = new CompleteVisit(visit);
        this.completeVisits.add(completeVisit);
        return true;
    }
}