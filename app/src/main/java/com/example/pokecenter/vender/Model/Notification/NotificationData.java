package com.example.pokecenter.vender.Model.Notification;

public class NotificationData {
    private String title;
    private String content;
    private String type;
    private boolean read;
    private String sentDate;

    public NotificationData(String title, String content, String type, boolean read, String sentDate) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.read = read;
        this.sentDate = sentDate;
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

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }
}
