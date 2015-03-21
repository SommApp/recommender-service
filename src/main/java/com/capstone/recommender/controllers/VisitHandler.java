/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;

import com.google.inject.Inject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.joda.time.DateTime;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler implements Runnable{

    private final Map<Long, PartialVisit> visitByToken;
    private final Collection<CompleteVisit> finishedVisits;
    private final AtomicLong tokenGenerator;
    private final ScheduledExecutorService executorService;
    private final AtomicLong atomicLong;
    @Inject
    public VisitHandler(){
        this.visitByToken = new ConcurrentHashMap<>();
        this.finishedVisits = new ConcurrentLinkedDeque<>();
        this.tokenGenerator = new AtomicLong();
        this.atomicLong = new AtomicLong();

        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
    }

    public long beginVisit(long userId, long restaurantId) {
        final long token = tokenGenerator.getAndIncrement();
        final PartialVisit visit = new PartialVisit(userId, restaurantId);
        visitByToken.put(token, visit);
        return token;
    }

    public boolean endVisit(long token) {
        final PartialVisit partialVisit = visitByToken.remove(token);
        if (partialVisit != null) {
            finishedVisits.add(new CompleteVisit(partialVisit));
            return true;
        }

        return false;
    }

    @Override
    public void run() {
        Configuration configuration = new Configuration();
        String filename = "/user/visits/restaurants/" + atomicLong.getAndIncrement();

        PrintWriter fw = null;
        try {
           fw = new PrintWriter("/tmp" + filename);

           for (CompleteVisit element : finishedVisits) {
               fw.write(element.toString());
           }

           Process p = Runtime.getRuntime().exec("hadoop fs -put /tmp" + filename + " " + filename);
           p.waitFor();
        } catch (IOException io) {
           io.printStackTrace();
        } catch (InterruptedException e) {
           e.printStackTrace();
        } finally {
            if (fw != null ) {
                fw.close();
            }
        }
    }
}
