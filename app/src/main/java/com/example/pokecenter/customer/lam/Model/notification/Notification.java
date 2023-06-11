package com.example.pokecenter.customer.lam.Model.notification;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification {
    private String id;
    private String title;
    private String content;
    private Date sentDate;
    private String type;
    private boolean read;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public Notification() {
    }

    public Notification(String id, String title, String content, Date sentDate, String type, boolean read) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sentDate = sentDate;
        this.type = type;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentDateString() {
        return dateFormat.format(sentDate);
    }
    public Date getSentDate() {
        return sentDate;
    }
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
