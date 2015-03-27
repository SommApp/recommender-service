package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author sethwiesman on 3/21/15.
 */
public class VisitHandlerTest {

    private Map<Long, PartialVisit> partialVisitByToken;
    private VisitHandler visitHandler;

    @Before
    public void setup() {
        this.partialVisitByToken = new HashMap<>();
        this.visitHandler = new VisitHandler(partialVisitByToken);
    }

    @Test
    public void testStartVisit() {
        final long firstToken = visitHandler.beginVisit(1, 2);
        final long secondToken = visitHandler.beginVisit(3, 4);

        assertEquals("The first token returned was not 0", 0, firstToken);
        assertEquals("The token was not properly incremented", 1, secondToken);

        final PartialVisit firstTestVisit = partialVisitByToken.get(firstToken);
        assertNotNull("Failed to the first visit with the correct token", firstTestVisit);
        assertEquals("Failed to assign the correct uid to the first visit", 1, firstTestVisit.getUid());
        assertEquals("Failed to assign the correct rid to the first visit", 2, firstTestVisit.getRid());

        final PartialVisit secondTestVisit = partialVisitByToken.get(secondToken);
        assertNotNull("Failed to the first visit with the correct token", secondTestVisit);
        assertEquals("Failed to assign the correct uid to the first visit", 3, secondTestVisit.getUid());
        assertEquals("Failed to assign the correct rid to the first visit", 4, secondTestVisit.getRid());
    }

    @Test
    public void testEndVisit() {
        final long token = 1;
        final long uid = 2;
        final long rid = 3;

        final PartialVisit visit = new PartialVisit(uid, rid);
        this.partialVisitByToken.put(token, visit);

        final Optional<CompleteVisit> completeVisitOptional = visitHandler.endVisit(token);
        assertTrue("Failed to find visit that exists", completeVisitOptional.isPresent());

        final CompleteVisit completeVisit = completeVisitOptional.get();
        assertEquals("Uid changed during removal", uid, completeVisit.getUid());
        assertEquals("Rid changed during removal", rid, completeVisit.getRid());
    }


    @Test
    public void testEndVisitWithBadToken() {
        final long token = 1;
        final long uid = 2;
        final long rid = 3;

        final PartialVisit visit = new PartialVisit(uid, rid);
        this.partialVisitByToken.put(token, visit);

        final Optional<CompleteVisit> completeVisitOptional = visitHandler.endVisit(token+1);
        assertFalse("Found a visit that does not exist", completeVisitOptional.isPresent());
    }
}
