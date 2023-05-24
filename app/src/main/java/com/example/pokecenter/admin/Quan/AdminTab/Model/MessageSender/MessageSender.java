package com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender;

import java.io.Serializable;

public class MessageSender implements Serializable {
    private int senderAvatar;
    private int senderRole;
    private String senderName;

    private String messageSentTime;
    private String messageLatestSentence;
    private int messageUnseenSentenceCount;

    public MessageSender() {
    }

    public MessageSender(int senderAvatar, int senderRole, String senderName, String messageSentTime, String messageLatestSentence, int messageUnseenSentenceCount) {
        this.senderAvatar = senderAvatar;
        this.senderRole = senderRole;
        this.senderName = senderName;
        this.messageSentTime = messageSentTime;
        this.messageLatestSentence = messageLatestSentence;
        this.messageUnseenSentenceCount = messageUnseenSentenceCount;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageSentTime() {
        return messageSentTime;
    }

    public void setMessageSentTime(String messageSentTime) {
        this.messageSentTime = messageSentTime;
    }

    public String getMessageLatestSentence() {
        return messageLatestSentence;
    }

    public void setMessageLatestSentence(String messageLatestSentence) {
        this.messageLatestSentence = messageLatestSentence;
    }

    public int getMessageUnseenSentenceCount() {
        return messageUnseenSentenceCount;
    }

    public void setMessageUnseenSentenceCount(int messageUnseenSentenceCount) {
        this.messageUnseenSentenceCount = messageUnseenSentenceCount;
    }

    public int getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(int senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public int getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(int senderRole) {
        this.senderRole = senderRole;
    }
}
