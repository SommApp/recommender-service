package com.capstone.recommender.models;

import org.joda.time.DateTime;

public class PartialVisit {

    protected final long userId;
    protected final long restaurantId;
    protected final DateTime beginVisit;

    public PartialVisit(long userId, long restaurantId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.beginVisit = new DateTime();
    }

    protected PartialVisit(PartialVisit that) {
        this.userId = that.getUserId();
        this.restaurantId = that.getRestaurantId();
        this.beginVisit = that.getBeginVisit();
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public DateTime getBeginVisit() {
        return beginVisit;
    }

    public long getUserId() {
        return userId;
    }
}
