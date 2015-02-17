package com.capstone.recommender.controllers;

import com.capstone.recommender.models.PartialVisit;
import com.google.common.cache.Cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.google.common.cache.CacheBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author by sethwiesman on 2/15/15.
 */
public class VisitHandlerTest {

    OutputStream mockOutStream;
    Cache<Long, PartialVisit> cache;
    VisitHandler visitHandler;

    @Before
    public void setup() throws IOException {
        mockOutStream = mock(OutputStream.class);
        doNothing().when(mockOutStream).write(any(byte[].class));

        cache = CacheBuilder.newBuilder().build();


        VisitCache builder = spy(VisitCache.buildCache(removalNotification -> {}));
        visitHandler = new VisitHandler(builder, mockOutStream);
    }

    @Test
    public void insertVisit() {
        final long uid = 1;
        final long rid = 2;

        final long token = visitHandler.beginVisit(uid, rid);

        assertEquals("Token is the wrong value", 1, token);
        assertEquals("There are no users in the cache when there should be one", 1, cache.size());

        PartialVisit visit = cache.getIfPresent(token);
        assertNotNull("The user was not put in the cache", visit);
        assertEquals("Uid was changed when it should have remained the same", uid, visit.getUserId());
        assertEquals("Rid was changed when it should have remained the same", rid, visit.getRestaurantId());
    }
}
