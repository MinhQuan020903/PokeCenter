package com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer;

import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.customer.lam.Model.address.Address;

import java.util.ArrayList;

public class Customer extends User {
    private ArrayList<Order> orderHistory;

    public Customer() {
    }

    public Customer(ArrayList<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public Customer(ArrayList<Address> addresses, String address, String avatar, String gender, String phoneNumber, String registrationDate, int role, String username, String email, ArrayList<Order> orderHistory) {
        super(addresses, address, avatar, gender, phoneNumber, registrationDate, role, username, email);
        this.orderHistory = orderHistory;
    }

    public ArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(ArrayList<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Customer {addresses=");
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

    public String toString1() {
        StringBuilder ret = new StringBuilder("\n ====>");
        if (orderHistory != null) {
            for (Order order : orderHistory) {
                ret.append(order.toString() + "\n");
            }
            return ret.toString();
        } else return "";
    }
}
