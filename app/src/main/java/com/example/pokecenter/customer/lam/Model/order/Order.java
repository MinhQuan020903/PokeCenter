package com.example.pokecenter.customer.lam.Model.order;

import java.util.Date;
import java.util.List;

public class Order {

    private int totalAmount;
    private String createDateTime;
    private List<DetailOrder> ordersDetail;

    public Order(int totalAmount, String createDateTime, List<DetailOrder> ordersDetail) {
        this.totalAmount = totalAmount;
        this.createDateTime = createDateTime;
        this.ordersDetail = ordersDetail;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public List<DetailOrder> getOrdersDetail() {
        return ordersDetail;
    }

    public void setOrdersDetail(List<DetailOrder> ordersDetail) {
        this.ordersDetail = ordersDetail;
    }
}
