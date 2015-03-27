package com.capstone.recommender.models;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class PartialVisit {

    protected final Pair pair;
    protected final DateTime beginVisit;

    public PartialVisit(long userId, long restaurantId) {
        this.pair = new Pair(userId, restaurantId);
        this.beginVisit = new DateTime();
    }

    protected PartialVisit(PartialVisit that) {
        this.pair = that.pair;
        this.beginVisit = that.beginVisit;
    }

    public long getUid() {
        return this.pair.getUid();
    }

    public long getRid() {
        return this.pair.getRid();
    }

    public class Pair implements Comparable{
        protected final long uid;
        protected final long rid;

        public Pair(long uid, long rid) {
            this.uid = uid;
            this.rid = rid;
        }

        public long getUid() {
            return uid;
        }

        public long getRid() {
            return rid;
        }

        @Override
        public int compareTo(Object o) {

            Pair that;
            if (!(o instanceof Pair)) {
                return -1;
            } else {
                that = (Pair)o;
            }

            if (this.uid < that.uid) {
                return -1;
            } else if (this.uid == that.uid) {
                if (this.rid < that.rid) {
                    return -1;
                } else if (this.rid == that.rid) {
                    return 1;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }
    }
}
