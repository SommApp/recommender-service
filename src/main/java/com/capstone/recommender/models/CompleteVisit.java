package com.capstone.recommender.models;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class CompleteVisit extends PartialVisit {

    private final long duration;
    private final String data;

    public CompleteVisit(PartialVisit partialVisit) {
        super(partialVisit);
        this.duration = new Interval(beginVisit, new Instant()).toDurationMillis();
        this.data = toString();
    }

    public static Optional<CompleteVisit> parse(String line) {
        final String[] tokens = line.trim().split("\t\n");

        if (tokens.length != 4) {
            return Optional.empty();
        }

        long uid;
        try {
            uid = Long.parseLong(tokens[0]);
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }

        long rid;
        try {
            rid = Long.parseLong(tokens[1]);
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }

        DateTime dateTime;
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss.SSSZZ");
            dateTime = formatter.parseDateTime(tokens[2]);
        } catch (Exception e) {
            return Optional.empty();
        }

        long duration;
        try {
            duration = Long.parseLong(tokens[3]);
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }

        return Optional.of(new CompleteVisit(uid, rid, dateTime, duration));
    }

    private CompleteVisit(long uid, long rid, DateTime date, long duration) {
        super(uid, rid, date);
        this.duration = duration;
        this.data = toString();
    }

    public long getDurationInMilliseconds() {
        return duration;
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

    @Override
    public String toString() {
        return userId + "\t" + restaurantId + "\t" + beginVisit + "\t" + duration + "\n";
    }

}
