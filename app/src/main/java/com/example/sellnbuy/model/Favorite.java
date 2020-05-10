package com.example.sellnbuy.model;

public class Favorite {

    public final static String COLLECTION_NAME = "Favorites";

    private String postId;
    private String userId;

    public Favorite(){}

    public Favorite(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
