package com.capstone.recommender.controllers;

import org.apache.mahout.cf.taste.recommender.Recommender;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.capstone.recommender.models.Visit;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author sethwiesman on 4/17/15.
 */
public class EngineGeneratorTest {

    List<Visit> list;
    EngineGenerator generator;
    AtomicReference<List<Visit>> reference = new AtomicReference<>();
    AtomicReference<Recommender> recommenderAtomicReference = new AtomicReference<>();
    @Before
    public void before() {
        list = VisitCreator.generateVisits();
        reference.set(list);
        generator = EngineGenerator.create(reference, recommenderAtomicReference);
        generator.run();
    }

    @Test
    public void getVisitsByUidTest() {
        Map<Long, List<Visit>> map = generator.getVisitsByUid(list);
        Set<Long> set = new HashSet<>();
        for (long i = 1; i <= 20; i++){
            set.add(i);
        }

        assertEquals("Incorrect number of uids", map.keySet(), set);

        for (Long key : map.keySet()) {
            for (Visit visit : map.get(key)) {
                assertEquals("Uids not correctly partitioned", key.longValue(), visit.getUid());
            }
        }

    }

}
