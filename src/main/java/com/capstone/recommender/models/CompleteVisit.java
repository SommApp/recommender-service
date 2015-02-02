package com.capstone.recommender.models;

import org.joda.time.Instant;
import org.joda.time.Interval;

public class CompleteVisit extends PartialVisit {

    private final Interval duration;

    public CompleteVisit(PartialVisit partialVisit) {
        super(partialVisit);
        this.duration = new Interval(beginVisit, new Instant());
    }

    public long getDurationInMilliseconds() {
        return duration.toDurationMillis();
    }
}
