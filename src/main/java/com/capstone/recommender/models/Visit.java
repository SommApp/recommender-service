package com.capstone.recommender.models;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;


public class Visit  {

    private final long uid;
    private final long rid;
    private final long duration;
    private final DateTime visitDate;

    public long getDuration() {
        return duration;
    }

    public long getRid() {
        return rid;
    }

    public long getUid() {
        return uid;
    }

    public DateTime getDate() { return visitDate; }

    public Visit(long uid, long rid, long duration) {
        this.uid = uid;
        this.rid = rid;
        this.duration = duration;
        this.visitDate = new DateTime();
    }

    public Visit(long uid, long rid, long duration, long timeSinceEpoch) {
        this.uid = uid;
        this.rid = rid;
        this.duration = duration;
        this.visitDate = new DateTime(timeSinceEpoch);
    }

    @Override
    public String toString() {
        return uid + " " + rid + " " + duration;
    }
    public static long getScore(Visit visit) {
        final long timeSinceVisit = new Interval(visit.visitDate, new Instant()).toPeriod().getDays();
        if (timeSinceVisit == 0) {
            return 1;
        }
        return (visit.duration/(timeSinceVisit));
    }
}
