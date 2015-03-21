/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler {

    private final Map<Long, PartialVisit> visitByToken;
    private final AtomicLong tokenGenerator;
    private File file;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    private static String fileStart = "~/user/restaurant/visit/";

    public VisitHandler() {
        this.visitByToken = new ConcurrentHashMap<>();
        this.tokenGenerator = new AtomicLong();
        this.file = new File(fileStart + tokenGenerator.getAndIncrement() + ".txt");
        try {
            file.createNewFile();
            this.fileWriter = new FileWriter(file.getName(), true);
            this.bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            bufferedWriter.write(completeVisit.toString());
            System.out.println(completeVisit.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}