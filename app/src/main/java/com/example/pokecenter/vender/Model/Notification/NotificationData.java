package com.example.pokecenter.vender.Model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationData {
    private String id;
    private String title;
    private String content;
    private String type;
    private boolean read;
    private Date sentDate;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public NotificationData(String id, String title, String content, String type, boolean read, Date sentDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.read = read;
        this.sentDate = sentDate;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getSentDateString() {
        return dateFormat.format(sentDate);
    }
}
