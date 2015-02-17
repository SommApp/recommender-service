package com.capstone.recommender.controllers;

import com.capstone.recommender.models.PartialVisit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author by sethwiesman on 2/17/15.
 */
public class VisitCache implements Cache<Long, PartialVisit>{

    private final Cache<Long, PartialVisit> cache;

    public static VisitCache buildCache(RemovalListener<Long, PartialVisit> removalListener) {
        return VisitCache(re)
    }

    private VisitCache(RemovalListener<Long, PartialVisit> removalListener) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.HOURS)
                .removalListener(removalListener)
                .build();
    }

    @Nullable
    @Override
    public PartialVisit getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    @Override
    public PartialVisit get(Long aLong, Callable<? extends PartialVisit> callable) throws ExecutionException {
        return cache.get(aLong, callable);
    }

    @Override
    public ImmutableMap<Long, PartialVisit> getAllPresent(Iterable<?> iterable) {
        return cache.getAllPresent(iterable);
    }

    @Override
    public void put(Long aLong, PartialVisit partialVisit) {
        cache.put(aLong, partialVisit);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends PartialVisit> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(Object o) {
        cache.invalidate(o);
    }

    @Override
    public void invalidateAll(Iterable<?> iterable) {
        cache.invalidateAll(iterable);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @Override
    public ConcurrentMap<Long, PartialVisit> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }
}
