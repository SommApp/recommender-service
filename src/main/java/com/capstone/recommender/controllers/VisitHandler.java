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

import java.io.*;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
            return true;
        }

        return false;
    }

    @Override
    public void run() {
        String filename = "hdfs://localhost:54310/user/visits/restaurants";
        Path dest = new Path(filename);
        Configuration conf = new Configuration();

        StringBuilder builder = new StringBuilder();
        for (CompleteVisit elem : finishedVisits) {
            builder.append(elem.toString());
        }

        final String data = builder.toString();

        InputStream in = new BufferedInputStream(new ByteArrayInputStream(data.getBytes()));

        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(filename), conf);
            OutputStream out = fs.append(new Path(filename));
            IOUtils.copyBytes(in, out, 4096, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
