package com.capstone.recommender.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;



/**
 * @author by sethwiesman on 2/15/15.
 */
public class PartialVisitTest {

    final PartialVisit partialVisit;
    final long uid;
    final long restaurantId;

    public PartialVisitTest() {
        uid = 123;
        restaurantId = 456;
        partialVisit = new PartialVisit(uid, restaurantId);
    }

    @Test
    public void uidTest(){
        assertEquals("Uid was changed", uid, partialVisit.getUserId());
    }

    @Test
    public void restaurantIdTest() {
        assertEquals("Restaurant id was changed", restaurantId, partialVisit.getRestaurantId());
    }
}
