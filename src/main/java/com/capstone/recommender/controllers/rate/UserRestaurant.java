package com.capstone.recommender.controllers.rate;

import javax.validation.constraints.NotNull;

/**
 * @author by sethwiesman on 2/23/15.
 */
public class UserRestaurant implements Comparable<UserRestaurant> {

    public final long uid;
    public final long rid;

    public UserRestaurant(long uid, long rid) {
        this.uid = uid;
        this.rid = rid;
    }

    @Override
    public int compareTo(@NotNull UserRestaurant that) {

        final long uidDifference = this.uid - that.uid;

        if (uidDifference != 0) {
            return (int)uidDifference;
        }

        return (int)(this.rid - that.uid);
    }
}
