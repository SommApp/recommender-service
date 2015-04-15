package com.capstone.recommender.models;

import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author sethwiesman on 4/14/15.
 */
public class VisitTest {

    private static final long uid = 1;
    private static final long rid = 2;
    private static final long duration = 120;
    private static final long timeSinceEpoch = 123456789;

    @Test
    public void normalConstructor() throws InterruptedException {
        final DateTime before = new DateTime();
        Thread.sleep(100);
        final Visit visit = new Visit(uid, rid, duration);
        Thread.sleep(100);
        final DateTime after = new DateTime();

        assertEquals("Uid changed unexpectedly", uid, visit.getUid());
        assertEquals("Rid changed unexpectedly", rid, visit.getRid());
        assertEquals("Duration changed unexpectedly", duration, visit.getDuration());

        assertTrue("object was created sooner than expected", visit.getDate().isAfter(before));
        assertTrue("object was created latter than expected", visit.getDate().isBefore(after));
    }

    @Test
    public void extendedConstructor() {
        final Visit visit = new Visit(uid, rid, duration, timeSinceEpoch);
        assertEquals("Uid changed unexpectedly", uid, visit.getUid());
        assertEquals("Rid changed unexpectedly", rid, visit.getRid());
        assertEquals("Duration changed unexpectedly", duration, visit.getDuration());
        assertEquals("DateTime changed unexpectedly", new DateTime(timeSinceEpoch), visit.getDate());
    }

}
