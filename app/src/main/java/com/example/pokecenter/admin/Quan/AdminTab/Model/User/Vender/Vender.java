package com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender;

import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Admin.Admin;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.product.Product;

import java.io.Serializable;
import java.util.ArrayList;

public class Vender extends User {

    private String shopName;
    private int followCount;
    private int totalProduct;
    private ArrayList<AdminProduct> productList;
    private ArrayList<Order> orderHistory;

    public Vender() {
    }

    public Vender(String shopName, int followCount, int totalProduct, ArrayList<AdminProduct> productList, ArrayList<Order> orderHistory) {
        this.shopName = shopName;
        this.followCount = followCount;
        this.totalProduct = totalProduct;
        this.productList = productList;
        this.orderHistory = orderHistory;
    }

    public Vender(ArrayList<Address> addresses, String address, String avatar, String gender, String phoneNumber, String registrationDate, int role, String username, String email, String shopName, int followCount, int totalProduct, ArrayList<AdminProduct> productList, ArrayList<Order> orderHistory) {
        super(addresses, address, avatar, gender, phoneNumber, registrationDate, role, username, email);
        this.shopName = shopName;
        this.followCount = followCount;
        this.totalProduct = totalProduct;
        this.productList = productList;
        this.orderHistory = orderHistory;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(int totalProduct) {
        this.totalProduct = totalProduct;
    }

    public ArrayList<AdminProduct> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<AdminProduct> productList) {
        this.productList = productList;
    }

    public ArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(ArrayList<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Vender {addresses=");
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
