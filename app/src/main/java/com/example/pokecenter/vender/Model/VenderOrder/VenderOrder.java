package com.example.pokecenter.vender.Model.VenderOrder;

import com.example.pokecenter.customer.lam.Model.order.DetailOrder;

import java.util.List;

public class VenderOrder {
    private int totalAmount;
    private String createDate;
    private List<VenderDetailOrder> details;
    String customerId;
    String venderId;
    private boolean isExpand;

    public VenderOrder(int totalAmount, String createDate, List<VenderDetailOrder> details, String customerId, String venderId) {
        this.totalAmount = totalAmount;
        this.createDate = createDate;
        this.details = details;
        this.customerId = customerId;
        this.venderId = venderId;
    }
    public VenderOrder(int totalAmount, String createDate, List<VenderDetailOrder> details) {
        this.totalAmount = totalAmount;
        this.createDate = createDate;
        this.details = details;
    }
    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<VenderDetailOrder> getDetails() {
        return details;
    }

    public void setDetails(List<VenderDetailOrder> details) {
        this.details = details;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getVenderId() {
        return venderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
