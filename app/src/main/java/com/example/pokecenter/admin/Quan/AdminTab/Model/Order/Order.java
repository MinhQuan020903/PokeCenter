package com.example.pokecenter.admin.Quan.AdminTab.Model.Order;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String id;
    private String createDate;
    private String customerId;
    private String venderId;

    private ArrayList<OrderDetail> details;
    private long totalAmount;

    public Order() {
    }

    public Order(String id, String createDate, String customerId, String venderId, ArrayList<OrderDetail> details, long totalAmount) {
        this.id = id;
        this.createDate = createDate;
        this.customerId = customerId;
        this.venderId = venderId;
        this.details = details;
        this.totalAmount = totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ArrayList<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<OrderDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("");
        ret.append("Order{" +
                "id='" + id + '\'' +
                ", createDate='" + createDate + '\'' +
                ", customerId='" + customerId + '\'' +
                ", venderId='" + venderId + '\'' +
                ", totalAmount=" + totalAmount);
        if (details != null) {
            for (OrderDetail orderDetail : details) {
                ret.append(orderDetail.toString());
            }
        }
        return ret.toString();
    }
}
