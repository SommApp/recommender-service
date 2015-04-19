package com.capstone.recommender.models;

import java.util.Map;

/**
 * @author  sethwiesman on 2/5/15.
 */
public class Analytic {

    public Map<Long, Float> getFrequencyOfVisitLength() {
        return frequencyOfVisitLength;
    }

    public void setFrequencyOfVisitLength(Map<Long, Float> frequencyOfVisitLength) {
        this.frequencyOfVisitLength = frequencyOfVisitLength;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(long totalVisits) {
        this.totalVisits = totalVisits;
    }

    public long getUniqueVisits() {
        return uniqueVisits;
    }

    public void setUniqueVisits(long uniqueVisits) {
        this.uniqueVisits = uniqueVisits;
    }

    private long restaurantId;
    private long uniqueVisits;
    private long totalVisits;
    private Map<Long, Float> frequencyOfVisitLength;

    public Map<String, Float> getNumVisitsByDay() {
        return numVisitsByDay;
    }

    public void setNumVisitsByDay(Map<String, Float> numVisitsByDay) {
        this.numVisitsByDay = numVisitsByDay;
    }

    private Map<String, Float> numVisitsByDay;

    public Analytic(long restaurantId, long uniqueVisits, long totalVisits,
                    Map<Long, Float> frequencyOfVisitLength, Map<String, Float> numVisitsByDay) {
        this.restaurantId = restaurantId;
        this.uniqueVisits = uniqueVisits;
        this.totalVisits = totalVisits;
        this.frequencyOfVisitLength = frequencyOfVisitLength;
        this.numVisitsByDay = numVisitsByDay;
    }
}
