package com.capstone.recommender.models;

import org.joda.time.Instant;
import org.joda.time.Interval;

public class CompleteVisit extends PartialVisit {

    private final long duration;

    public CompleteVisit(PartialVisit partialVisit) {
        super(partialVisit);
        this.duration = new Interval(beginVisit, new Instant()).toDurationMillis();
    }

    public long getDurationInMilliseconds() {
        return duration;
    }
}
