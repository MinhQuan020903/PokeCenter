package com.example.pokecenter.admin.Quan.AdminTab.Model.Order;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private String selectedOptionImage;
    private String productName;
    private String productId;
    private int quantity;
    private int selectedOption;
    private String selectedOptionName;
    private Long selectedOptionPrice;

    public OrderDetail() {
    }

    public OrderDetail(String productId, int quantity, int selectedOption) {
        this.productId = productId;
        this.quantity = quantity;
        this.selectedOption = selectedOption;
    }

    public OrderDetail(String selectedOptionImage, String productName, String productId, int quantity, int selectedOption, String selectedOptionName,  Long selectedOptionPrice) {
        this.selectedOptionImage = selectedOptionImage;
        this.productName = productName;
        this.productId = productId;
        this.quantity = quantity;
        this.selectedOption = selectedOption;
        this.selectedOptionName = selectedOptionName;
        this.selectedOptionPrice = selectedOptionPrice;
    }

    public Long getSelectedOptionPrice() {
        return selectedOptionPrice;
    }

    public void setSelectedOptionPrice(Long selectedOptionPrice) {
        this.selectedOptionPrice = selectedOptionPrice;
    }

    public String getSelectedOptionImage() {
        return selectedOptionImage;
    }

    public void setSelectedOptionImage(String selectedOptionImage) {
        this.selectedOptionImage = selectedOptionImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSelectedOptionName() {
        return selectedOptionName;
    }

    public void setSelectedOptionName(String selectedOptionName) {
        this.selectedOptionName = selectedOptionName;
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

    @Override
    public String toString() {
        return "OrderDetail{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", selectedOption=" + selectedOption +
                '}';
    }
}
