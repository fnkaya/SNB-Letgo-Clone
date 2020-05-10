package com.example.sellnbuy.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Message {

    public static final String COLLECTION_NAME = "messages";

    private String from;
    private String text;
    private Timestamp time;

    public Message() {
    }

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
        this.time = new Timestamp(new Date());

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

}
