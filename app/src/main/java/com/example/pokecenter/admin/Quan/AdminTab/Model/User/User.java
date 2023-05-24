package com.example.pokecenter.admin.Quan.AdminTab.Model.User;

import java.io.Serializable;

public class User implements Serializable {
    private String avatar;
    private String gender;
    private String phoneNumber;
    private String registrationDate;
    private int role;
    private String username;
    private String email;

    public User() {
    }

    public User(String avatar,String gender, String phoneNumber, String registrationDate, int role,  String username) {
        this.avatar = avatar;
        this.username = username;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
