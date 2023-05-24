package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;

import java.util.ArrayList;

public interface FirebaseCallback {
    void onCallback(ArrayList<User> usersList);
}
