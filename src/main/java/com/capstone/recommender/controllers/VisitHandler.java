/**
 * @author  sethwiesman 2/10/15.
 */

package com.capstone.recommender.controllers;

import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.PartialVisit;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VisitHandler {

    private final Map<Long, PartialVisit> visitByToken;
    private final AtomicLong tokenGenerator;
    private Connection connection;
    private PreparedStatement preparedStatement;

    public VisitHandler() {
        this.visitByToken = new ConcurrentHashMap<>();
        this.tokenGenerator = new AtomicLong();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost/feedback?user=root&password=toor");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.preparedStatement = connection.prepareStatement("INSERT INTO Visits (uid, rid, date, duration) VALUES (?, ?, ?, ?)");
        } catch (SQLException e) {
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
            preparedStatement.setString(1, String.valueOf(completeVisit.getUserId()));
            preparedStatement.setString(2, String.valueOf(completeVisit.getRestaurantId()));
            preparedStatement.setString(3, String.valueOf(completeVisit.getBeginVisit()));
            preparedStatement.setString(4, String.valueOf(completeVisit.getDurationInMilliseconds()));
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}