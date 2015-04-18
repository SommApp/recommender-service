package com.capstone.recommender.models;

/**
 * @author sethwiesman on 4/18/15.
 */
public class RecommendedItem {

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    private long itemId;

    public RecommendedItem(long itemId) {
        this.itemId = itemId;
    }

}
