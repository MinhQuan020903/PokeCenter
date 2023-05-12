package com.example.pokecenter.customer.lam.Model.account;

public class Account {

    private String avatar;
    private String username;
    private String gender;
    private String phoneNumber;
    private String registrationDate;

    public Account() {
    }

    public Account(String avatar, String username, String gender, String phoneNumber, String registrationDate) {
        this.avatar = avatar;
        this.username = username;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
