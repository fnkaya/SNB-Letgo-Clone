package com.example.sellnbuy.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Channel {

    public static final String COLLECTION_NAME = "Channels";
    public static final String SENDER_ID = "senderId";
    public static final String TIME = "time";

    private String senderId;
    private String receiverId;
    private String receiverName;
    private String postId;
    private Timestamp time;

    public Channel(){}

    public Channel(String senderId, String receiverId, String receiverName, String postId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.postId = postId;
        this.time = new Timestamp(new Date());
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
