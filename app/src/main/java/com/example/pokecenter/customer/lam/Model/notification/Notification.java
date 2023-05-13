package com.example.pokecenter.customer.lam.Model.notification;

public class Notification {
    private String id;
    private String title;
    private String content;
    private String sentDate;
    private String type;
    private boolean read;

    public Notification(String id, String title, String content, String sentDate, String type, boolean read) {
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

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
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
