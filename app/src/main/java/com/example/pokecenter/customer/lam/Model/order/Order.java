package com.example.pokecenter.customer.lam.Model.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Order {
    private String id;

    private int totalAmount;
    private Date createDateTime;
    private List<DetailOrder> ordersDetail;
    private String status;
    private Date deliveryDate;
    private boolean isExpand;

    public Order(String id, int totalAmount, Date createDateTime, List<DetailOrder> ordersDetail, String status) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.createDateTime = createDateTime;
        this.ordersDetail = ordersDetail;
        this.status = status;
        isExpand = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public String getCreateDateTimeString() {
        return dateFormat.format(createDateTime);
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public List<DetailOrder> getOrdersDetail() {
        return ordersDetail;
    }

    public void setOrdersDetail(List<DetailOrder> ordersDetail) {
        this.ordersDetail = ordersDetail;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public void toggleExpand() {
        isExpand = !isExpand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
