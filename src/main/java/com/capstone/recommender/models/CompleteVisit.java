package com.capstone.recommender.models;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;

import org.apache.log4j.Logger;

public class CompleteVisit extends PartialVisit {

    private static Logger logger = Logger.getLogger(CompleteVisit.class);

    private final long duration;

    public CompleteVisit(PartialVisit partialVisit) {
        super(partialVisit);
        this.duration = new Interval(beginVisit, new Instant()).toDurationMillis();
    }

    public static Optional<CompleteVisit> parse(String line) {
        final String[] tokens = line.trim().split("\\t");

        if (tokens.length != 4) {
            logger.warn("Incorrect number of tokens on line, actual number: " + tokens.length);
            Arrays.asList(tokens).stream().forEachOrdered(System.err::println);
            return Optional.empty();
        }

        long uid;
        try {
            uid = Long.parseLong(tokens[0]);
        } catch (NumberFormatException nfe) {
            logger.warn("Failed to parse long: " + tokens[0]);
            return Optional.empty();
        }

        long rid;
        try {
            rid = Long.parseLong(tokens[1]);
        } catch (NumberFormatException nfe) {
            logger.warn("Failed to parse long: " + tokens[1]);
            return Optional.empty();
        }

        DateTime dateTime;
        try {
            //DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss.SSSZZ");
            dateTime = ISODateTimeFormat.dateTime().parseDateTime(tokens[2]);
        } catch (Exception e) {
            logger.warn("Failed to parse DateTime: " + tokens[2]);
            return Optional.empty();
        }

        long duration;
        try {
            duration = Long.parseLong(tokens[3]);
        } catch (NumberFormatException nfe) {
            logger.warn("Failed to parse long: " + tokens[3]);
            return Optional.empty();
        }

        return Optional.of(new CompleteVisit(uid, rid, dateTime, duration));
    }

    private CompleteVisit(long uid, long rid, DateTime date, long duration) {
        super(uid, rid, date);
        this.duration = duration;
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
