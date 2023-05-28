package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;

import java.util.ArrayList;

public interface FirebaseCallback<T> {
    void onCallback(T user);
}
