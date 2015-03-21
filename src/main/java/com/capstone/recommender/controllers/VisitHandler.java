/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler implements Runnable{

    private final Map<Long, PartialVisit> visitByToken;
    private final AtomicLong tokenGenerator;
    private final AtomicLong fileTokenGenerator;
    private final ArrayList<CompleteVisit> completeVisits;
    private final ScheduledExecutorService executorService;

    public VisitHandler() {
        this.visitByToken = new ConcurrentHashMap<>();
        this.tokenGenerator = new AtomicLong();
        this.fileTokenGenerator = new AtomicLong();
        this.completeVisits = new ArrayList<>();
        this.executorService = new ScheduledThreadPoolExecutor(1);
        this.executorService.scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
    }

    public long beginVisit(long uid, long rid) {
        final long token = tokenGenerator.getAndIncrement();
        final PartialVisit visit = new PartialVisit(uid, rid);
        this.visitByToken.put(token, visit);
        return token;
    }

    public boolean endVisit(long token) {
        final PartialVisit visit = this.visitByToken.remove(token);
        if (visit == null) {
            return false;
        }

        final CompleteVisit completeVisit = new CompleteVisit(visit);
        this.completeVisits.add(completeVisit);
        return true;
    }

    @Override
    public void run() {
        final long token = fileTokenGenerator.getAndIncrement();
        FileOutputStream out = null;

        try {
            out = new FileOutputStream("/user/rest/visit/" + token + ".txt");
            final PrintStream stream = new PrintStream(out);
            completeVisits.stream().forEach(stream::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}