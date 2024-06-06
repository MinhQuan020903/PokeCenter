package com.example.pokecenter.customer.lam.Model.account;

import java.io.Serializable;

public class Account implements Serializable {

    private String avatar;
    private String username;
    private String gender;
    private String phoneNumber;
    private String registrationDate;
    private int role;
    private String id;

    public Account() {}

//    public Account(String avatar, String username, int role, String id) {
//        this.id = id;
//        this.avatar = avatar;
//        this.username = username;
//        this.role = role;
//    }

//    public Account(String avatar, String username, String gender, String phoneNumber, String registrationDate) {
//        this.avatar = avatar;
//        this.username = username;
//        this.gender = gender;
//        this.phoneNumber = phoneNumber;
//        this.registrationDate = registrationDate;
//    }

    private Account(Builder builder) {
        this.avatar = builder.avatar;
        this.username = builder.username;
        this.gender = builder.gender;
        this.phoneNumber = builder.phoneNumber;
        this.registrationDate = builder.registrationDate;
        this.role = builder.role;
        this.id = builder.id;
    }

    public static class Builder {
        private String avatar;
        private String username;
        private String gender;
        private String phoneNumber;
        private String registrationDate;
        private int role;
        private String id;

        public Builder() {}

        public Builder withAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withRegistrationDate(String registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder withRole(int role) {
            this.role = role;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Account build() {
            return new Account(this);
        }
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

    public int getRole() {
        return role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
