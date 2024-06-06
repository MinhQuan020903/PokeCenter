package com.example.pokecenter.customer.lam.Singleton;

import com.example.pokecenter.customer.lam.Model.account.Account;

public class UserInfo {
    private static UserInfo instance;
    private Account account;

    // Private constructor to prevent instantiation
    private UserInfo() {}

    // Static method to get the single instance of the class
    public static UserInfo getInstance() {
        if (instance == null) {
            synchronized (UserInfo.class) {
                if (instance == null) {
                    instance = new UserInfo();
                }
            }
        }
        return instance;
    }

    // Getter and setter methods for Account
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
