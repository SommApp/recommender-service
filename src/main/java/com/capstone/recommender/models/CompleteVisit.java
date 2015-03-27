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
        this.data = pair.uid + "\t" + pair.rid + "\t" + beginVisit + "\t" + duration + "\n";
    }

    @Override
    public String toString() {
        return data;
    }

    public long getDuration() {
        return duration;
    }

    public static long getScore(CompleteVisit visit) {
        final long timeSinceVisit = new Interval(visit.beginVisit, new Instant()).toPeriod().getDays();
        return (long)(visit.duration/Math.log(timeSinceVisit));
    }
}
