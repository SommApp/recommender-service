package com.capstone.recommender.controllers;

import com.capstone.recommender.models.PartialVisit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by sethwiesman on 2/15/15.
 */
public class VisitHandlerTest {

    OutputStream mockOutStream;
    Map<Long, PartialVisit> cache;
    VisitHandler visitHandler;

    @Before
    public void setup() throws IOException {
        mockOutStream = mock(OutputStream.class);
        doNothing().when(mockOutStream).write(any(byte[].class));

        cache = new HashMap<>();

        visitHandler = new VisitHandler(cache, mockOutStream);
    }

    @Test
    public void startVisit() {
        final long uid = 1;
        final long rid = 2;

        final long token = visitHandler.beginVisit(uid, rid);

        assertEquals("Token is the wrong value", 0, token);
        assertEquals("There are no users in the cache when there should be one", 1, cache.size());

        final PartialVisit visit = cache.get(token);
        assertNotNull("The user was not put in the cache", visit);
        assertEquals("Uid was changed when it should have remained the same", uid, visit.getUserId());
        assertEquals("Rid was changed when it should have remained the same", rid, visit.getRestaurantId());
    }

    @Test
    public void endVisit() {
        final long uid = 1;
        final long rid = 2;

        final long token = visitHandler.beginVisit(uid, rid);

        visitHandler.endVisit(token);

        final PartialVisit visit = cache.get(token);
        assertNull("Restaurant was not removed when it should have been", visit);
    }

    @Test
    public void badEndVisit() {
        final long uid = 1;
        final long rid = 2;

        final long token = visitHandler.beginVisit(uid, rid);

        visitHandler.endVisit(token+1);

        final PartialVisit visit = cache.get(token);
        assertNotNull("Restaurant was removed when it should not have been", visit);
    }
}
