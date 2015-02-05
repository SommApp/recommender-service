package com.capstone.recommender.visits;

import com.capstone.recommender.models.PartialVisit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * @author  sethwiesman on 2/2/15.
 */
public class VisitTracker {

    private final LoadingCache<Long, PartialVisit> visitByTag;

    public VisitTracker() {
        visitByTag = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.HOURS)
                .build(
                        new CacheLoader<Long, PartialVisit>() {
                            @Override
                            public PartialVisit load(Long aLong) throws Exception {
                                return null;
                            }
                        }
                );
    }

}
