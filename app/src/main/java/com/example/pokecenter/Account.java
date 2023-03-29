package com.example.pokecenter;


public class Account {
    public String username;
    public String password;
    public int role;
    // 0. customer
    // 1. vender
    // 2. admin

    public Account(String username, String password, int role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
