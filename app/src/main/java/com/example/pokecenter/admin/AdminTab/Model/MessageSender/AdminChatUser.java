package com.example.pokecenter.admin.AdminTab.Model.MessageSender;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminChatUser implements Serializable {
    private String email;
    private String avatar;
    private String name;
    private int role;
    private ArrayList<Message> messageList;

    public AdminChatUser() {
    }

    public AdminChatUser(String email, String avatar, String name, int role, ArrayList<Message> messageList) {
        this.email = email;
        this.avatar = avatar;
        this.name = name;
        this.role = role;
        this.messageList = messageList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public ArrayList<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }
}
