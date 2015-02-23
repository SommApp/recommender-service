package com.capstone.recommender.models;

import org.joda.time.Instant;
import org.joda.time.Interval;

import javax.validation.constraints.NotNull;

public class CompleteVisit extends PartialVisit {

    private final long duration;
    private final String data;

    public CompleteVisit(PartialVisit partialVisit) {
        super(partialVisit);
        this.duration = new Interval(beginVisit, new Instant()).toDurationMillis();
        this.data = userId + "\t" + restaurantId + "\t" + beginVisit + "\t" + duration + "\n";
    }

    public long getDurationInMilliseconds() {
        return duration;
    }

    @Override
    public String toString() {
        return data;
    }

    public int compareTo(@NotNull CompleteVisit that) {

        if (super.compareTo(that) == 0) {
            return 0;
        }

        if (this.duration < that.duration) {
            return -1;
        } else if (this.duration > that.duration) {
            return 1;
        }

        return 0;
    }

}
