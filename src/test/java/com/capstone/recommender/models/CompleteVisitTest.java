package com.capstone.recommender.models;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author by sethwiesman on 2/23/15.
 */
public class CompleteVisitTest {

    private final long uid;
    private final long rid;

    private PartialVisit partialVisit;
    private CompleteVisit completeVisit;

    public CompleteVisitTest() {
        uid = 1;
        rid = 1;

        partialVisit = new PartialVisit(uid, rid);
        completeVisit = new CompleteVisit(partialVisit);
    }


    @Test
    public void successfulParse() {

        final long uid = 1;
        final long rid = 2;
        final DateTime now = new DateTime();

        final String line = uid + "\t"
                            + rid + "\t"
                            + now.toString()
                            + "\t" + Hours.ONE.toStandardDuration().getMillis() + "\n";

        Optional<CompleteVisit> visitOptional = CompleteVisit.parse(line);
        assertTrue("Failed to parse valid line", visitOptional.isPresent());

        CompleteVisit visit = visitOptional.get();
        assertEquals("Failed to match uid", uid, visit.getUserId());
        assertEquals("Failed to match rid", rid, visit.getRestaurantId());
        assertEquals("Failed to match date", now, visit.getBeginVisit());
        assertEquals("Failed to match duration", Hours.ONE.toStandardDuration().getMillis(), visit.getDurationInMilliseconds());
    }


    @Test
    public void failParseOnUid() {
        final String uid = "1+";
        final long rid = 2;
        final DateTime now = new DateTime();

        final String line = uid + "\t"
                + rid + "\t"
                + now.toString()
                + "\t" + Hours.ONE.toStandardDuration().getMillis() + "\n";

        Optional<CompleteVisit> visitOptional = CompleteVisit.parse(line);
        assertFalse("Parsed invalid line", visitOptional.isPresent());
    }

    @Test
    public void failParseOnRid() {
        final long uid = 1;
        final String rid = "8*7";
        final DateTime now = new DateTime();

        final String line = uid + "\t"
                + rid + "\t"
                + now.toString()
                + "\t" + Hours.ONE.toStandardDuration().getMillis() + "\n";

        Optional<CompleteVisit> visitOptional = CompleteVisit.parse(line);
        assertFalse("Parsed invalid line", visitOptional.isPresent());
    }

    @Test
    public void failParseOnDate() {
        final long uid = 1;
        final long rid = 2;
        final DateTime now = new DateTime();

        final String line = uid + "\t"
                + rid + "\t"
                + now.toString() + "asdf"
                + "\t" + Hours.ONE.toStandardDuration().getMillis() + "\n";

        Optional<CompleteVisit> visitOptional = CompleteVisit.parse(line);
        assertFalse("Parsed invalid line", visitOptional.isPresent());
    }

    @Test
    public void failParseOnDuration() {
        final long uid = 1;
        final long rid = 2;
        final DateTime now = new DateTime();

        final String line = uid + "\t"
                + rid + "\t"
                + now.toString()
                + "\t" + Hours.ONE.toStandardDuration().getMillis() + "\n*231"+ "\n";

        Optional<CompleteVisit> visitOptional = CompleteVisit.parse(line);
        assertFalse("Parsed invalid line", visitOptional.isPresent());
    }
}
