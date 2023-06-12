package com.example.pokecenter.admin.AdminTab.Model.MessageSender;

import java.io.Serializable;

public class Message implements Serializable {
    private String id;
    private String content;
    private String sentTime;
    private boolean hasSeen;

    public Message() {
    }

    public Message(String id, String content, String sentTime, boolean hasSeen) {
        this.id = id;
        this.content = content;
        this.sentTime = sentTime;
        this.hasSeen = hasSeen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public boolean isHasSeen() {
        return hasSeen;
    }

    public void setHasSeen(boolean hasSeen) {
        this.hasSeen = hasSeen;
    }
}
