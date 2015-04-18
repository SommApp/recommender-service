package com.capstone.recommender.models;

import java.util.Map;

/**
 * @author  sethwiesman on 2/5/15.
 */
public class Analytic {

    public Map<Long, Integer> getFrequencyOfVisitLength() {
        return frequencyOfVisitLength;
    }

    public void setFrequencyOfVisitLength(Map<Long, Integer> frequencyOfVisitLength) {
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
    private Map<Long, Integer> frequencyOfVisitLength;

    public Map<String, Integer> getNumVisitsByMonth() {
        return numVisitsByMonth;
    }

    public void setNumVisitsByMonth(Map<String, Integer> numVisitsByMonth) {
        this.numVisitsByMonth = numVisitsByMonth;
    }

    private Map<String, Integer> numVisitsByMonth;

    public Analytic(long restaurantId, long uniqueVisits, long totalVisits,
                    Map<Long, Integer> frequencyOfVisitLength, Map<String, Integer> numVisitsByMonth) {
        this.restaurantId = restaurantId;
        this.uniqueVisits = uniqueVisits;
        this.totalVisits = totalVisits;
        this.frequencyOfVisitLength = frequencyOfVisitLength;
        this.numVisitsByMonth = numVisitsByMonth;
    }
}
