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
        String filename = "hdfs://localhost:9000user/visits/restaurants/" + atomicLong.getAndIncrement();

        FileSystem fs = null;
        BufferedWriter br = null;

        StringBuilder builder = new StringBuilder();
        for (CompleteVisit elem : finishedVisits) {
            builder.append(elem.toString());
        }

        final String data = builder.toString();

        try {
            fs = FileSystem.get(new URI("hdfs://localhost/9000"), configuration);
            Path file = new Path(filename);
            OutputStream os = fs.create(file, () -> System.out.println("*"));
            br = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            br.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (fs != null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
