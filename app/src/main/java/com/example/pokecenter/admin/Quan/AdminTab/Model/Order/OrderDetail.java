package com.example.pokecenter.admin.Quan.AdminTab.Model.Order;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private String productId;
    private int quantity;
    private int selectedOption;

    public OrderDetail() {
    }

    public OrderDetail(String productId, int quantity, int selectedOption) {
        this.productId = productId;
        this.quantity = quantity;
        this.selectedOption = selectedOption;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }
}
