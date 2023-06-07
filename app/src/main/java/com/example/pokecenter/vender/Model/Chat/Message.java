package com.example.pokecenter.vender.Model.Chat;

public class Message {
    private String messageText;
    private String senderId;
    private long sendingTime;

    public Message() {
    }

    public Message(String senderId, String messageText, long sendingTime) {;
        this.messageText = messageText;
        this.senderId = senderId;
        this.sendingTime = sendingTime;
    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(long sendingTime) {
        this.sendingTime = sendingTime;
    }
}

