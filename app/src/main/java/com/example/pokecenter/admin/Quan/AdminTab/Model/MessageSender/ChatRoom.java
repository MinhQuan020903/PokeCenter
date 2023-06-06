package com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender;

import java.util.ArrayList;

public class ChatRoom {
    private AdminChatUser user1;
    private AdminChatUser user2;
    private ArrayList<Message> messages;

    public ChatRoom(AdminChatUser user1, AdminChatUser user2, ArrayList<Message> messages) {
        this.user1 = user1;
        this.user2 = user2;
        this.messages = messages;
    }

    public ChatRoom() {
    }

    public AdminChatUser getUser1() {
        return user1;
    }

    public void setUser1(AdminChatUser user1) {
        this.user1 = user1;
    }

    public AdminChatUser getUser2() {
        return user2;
    }

    public void setUser2(AdminChatUser user2) {
        this.user2 = user2;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
