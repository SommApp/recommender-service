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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler implements Runnable{

    private final Map<Long, PartialVisit> visitByToken;
    private final Collection<CompleteVisit> finishedVisits;
    private final AtomicLong tokenGenerator;
    private final ScheduledExecutorService executorService;


    @Inject
    public VisitHandler(){
        this.visitByToken = new ConcurrentHashMap<>();
        this.finishedVisits = new ConcurrentLinkedDeque<>();
        this.tokenGenerator = new AtomicLong();

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
        }

        return false;
    }

    @Override
    public void run() {
        Path path = new Path("hdfs://localhost:54310/user/visits/restaurants");
        try {
            FileSystem fs = FileSystem.get(new Configuration());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.create(path, true)));
            for(CompleteVisit element : finishedVisits) {
                writer.write(element.toString());
            }
System.out.println("Writing out");
            finishedVisits.clear();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
