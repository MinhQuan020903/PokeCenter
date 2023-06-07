package com.example.pokecenter.vender.Model.ChatRoom;

import com.example.pokecenter.customer.lam.Model.account.Account;

public class ChatRoom {
    private String id;
    private String senderId;
    private Account senderAccount;
    private String lastMessage;
    private long lastMessageTimeStamp;
    public ChatRoom(){}

    public ChatRoom(String id, String senderId,Account account, String lastMessage, long lastMessageTimeStamp) {
        this.id = id;
        this.senderId = senderId;
        this.senderAccount = account;
        this.lastMessage = lastMessage;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(long lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }
}
