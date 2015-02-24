package com.capstone.recommender.controllers.rate;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import javax.validation.constraints.NotNull;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author by sethwiesman on 2/23/15.
 */
public class UserRestaurant implements Comparable<UserRestaurant>, Writable, WritableComparable<UserRestaurant>{

    private long uid;
    private long rid;

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

    public long getUid() {
        return uid;
    }

    public long getRid() {
        return rid;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(uid);
        dataOutput.writeLong(uid);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.uid = dataInput.readLong();
        this.rid = dataInput.readLong();
    }

    @Override
    //hashing method described by Joshua Bloc in Effective Java
    public int hashCode() {
        final int shift = 32;
        final int prime = 31;

        int result = (int) (uid ^ (uid >>> shift));
        result = prime * result + (int) (uid ^ (uid >>> shift));
        return result;
    }

    @Override
    public String toString() {
        return uid + "\t" + rid;
    }
}
