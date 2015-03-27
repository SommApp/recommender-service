/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;
import com.google.inject.Inject;

import java.util.Map;
import java.util.Optional;


public class VisitHandler {

    private final Map<Long, PartialVisit> visitByToken;
    private long token;

    @Inject
    public VisitHandler(Map<Long, PartialVisit> visitByToken) {
        this.visitByToken = visitByToken;
        token = 0;
    }

    public long beginVisit(long uid, long rid) {
        final long key = token++;
        final PartialVisit visit = new PartialVisit(uid, rid);
        this.visitByToken.put(key, visit);
        return key;
    }

    public Optional<CompleteVisit> endVisit(long token) {
        final PartialVisit visit = this.visitByToken.remove(token);
        if (visit == null) {
            return Optional.empty();
        }

        final CompleteVisit completeVisit = new CompleteVisit(visit);
        return Optional.of(completeVisit);
    }
}