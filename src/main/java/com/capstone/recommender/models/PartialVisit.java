package com.capstone.recommender.models;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class PartialVisit implements Comparable<PartialVisit> {

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

    public long getUserId() {
        return userId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public DateTime getBeginVisit() {
        return beginVisit;
    }

    @Override
    public int compareTo(@NotNull PartialVisit that) {

        if (this.userId < that.userId) {
            return -1;
        } else if (this.userId > that.userId) {
            return 1;
        }


        if (this.restaurantId < that.restaurantId) {
            return -1;
        } else if (this.restaurantId > that.restaurantId) {
            return 1;
        }

        final int beginVisitCompare = this.beginVisit.compareTo(that.beginVisit);
        if (beginVisitCompare != 0) {
            return beginVisitCompare;
        }

        return 0;
    }
}
