package com.capstone.recommender.controllers;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Visit;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author sethwiesman on 4/14/15.
 */
public class StatisticsGeneratorTest {

    private final AtomicReference<List<Visit>> visitReference = new AtomicReference<>();
    private final AtomicReference<Map<Long, Analytic>> analyticsReference = new AtomicReference<>();
    private Map<Long, List<Visit>> visitsByRestaurants;
    private List<Visit> list;
    private StatisticsGenerator generator;

    private static final int numUsers = 20;
    private static final int numRestaurants = 20;

    @Before
    public void beforeTests() {
        final List<Visit> visits = new ArrayList<>();

        //have every user visit every restaurant twice
        for (int i = 0; i < numUsers; i++) {
            for (int j = numRestaurants ; j >= 0; j--) {
                visits.add(new Visit(i, j, TimeUnit.MINUTES.toSeconds(i * 4)));
                visits.add(new Visit(i, j, TimeUnit.MINUTES.toSeconds(i * 4)));
            }
        }

        visitReference.set(visits);
        generator = StatisticsGenerator.create(visitReference, analyticsReference);
        visitsByRestaurants = visits.stream().collect(Collectors.groupingBy(Visit::getRid));

        list = VisitCreatorTest.generateVisits();
    }

    @Test
    public void minutes() {
        Map<Long, List<Visit>> group = list.stream().collect(Collectors.groupingBy(Visit::getRid));
        Map<Long, Map<Long, Float>> freq = generator.frequencyOfVisitLength(group);

        /**for (Long key: freq.keySet()) {
            System.out.println(key + "  " + freq.toString());
        }*/
    }

    @Test
    public void runTest() {
        generator.run();
        Map<Long, Analytic> analytics = analyticsReference.get();

        //All analytics should be the same
        assertEquals("Incorrect number of analytics", 21L, analytics.keySet().size());

        for (Long key : analytics.keySet()) {
            Analytic analytic = analytics.get(key);
            assertNotNull("Analytics does not contain restaurant " + key, analytic);
            assertEquals("Analytic has the wrong number of visits", 40L, analytic.getTotalVisits());
            assertEquals("Analytic has the wrong number of unique visits", 20L, analytic.getUniqueVisits());
        }
    }

    @Test
    public void seeTotalVisits() {
        Map<Long, Long> map = generator.visitsForRestaurant(visitsByRestaurants);
        for (Long key : map.keySet()) {
            assertEquals(key + " did not receive the correct number of visits", 40L, map.get(key).longValue());
        }
    }

    @Test
    public void seeUniqueVisits() {
        Map<Long, Long> map = generator.uniqueVisitsForRestaurant(visitsByRestaurants);
        for (Long key : map.keySet()) {
            assertEquals(key + " received more visits than there are users", 20L, map.get(key).longValue());
        }
    }

    public void frequencyOfVisits() {
        Map<Long, Map<Long, Float>> map = generator.frequencyOfVisitLength(visitsByRestaurants);
        for (Long key1 : map.keySet()) {
            Map<Long, Float> innerMap = map.get(key1);
            for (Long key2 : innerMap.keySet()) {
                long val = innerMap.get(key2).longValue();
                switch ((int) key2.longValue()) {
                    case 0:
                        assertEquals(4, val);
                        break;
                    case 75:
                    case 60:
                        assertEquals(6, val);
                        break;
                    case 45:
                    case 30:
                    case 15:
                        assertEquals(8, val);
                        break;
                    default:
                        assertTrue("Fail", false);
                }
            }
        }
    }
}
