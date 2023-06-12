package com.example.pokecenter.admin.AdminTab.Model.User;

import com.example.pokecenter.customer.lam.Model.address.Address;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    protected ArrayList<Address> addresses;
    protected String address;
    protected String avatar;
    protected String gender;
    protected String phoneNumber;
    protected String registrationDate;
    protected int role;
    protected String username;
    protected String email;

    public User() {
        addresses = new ArrayList<>();
        address = "";
        avatar = "";
        gender = "";
        phoneNumber = "";
        registrationDate = "";
        role = -1;
        username = "";
        email = "";
    }

    public User(ArrayList<Address> addresses, String address, String avatar, String gender, String phoneNumber, String registrationDate, int role, String username, String email) {
        this.addresses = addresses;
        this.address = address;
        this.avatar = avatar;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.role = role;
        this.username = username;
        this.email = email;
    }


    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        StringBuilder ret = new StringBuilder("User{addresses=");
        if (addresses != null) {
            for (Address a : addresses) {
                ret.append(a.getAddress2()).append(", ");
            }
        }
        ret.append("address='").append(address).append('\'')
                .append(", avatar='").append(avatar).append('\'')
                .append(", gender='").append(gender).append('\'')
                .append(", phoneNumber='").append(phoneNumber).append('\'')
                .append(", registrationDate='").append(registrationDate).append('\'')
                .append(", role=").append(role)
                .append(", username='").append(username).append('\'')
                .append(", email='").append(email).append('\'')
                .append('}');
        return ret.toString();
    }

}
