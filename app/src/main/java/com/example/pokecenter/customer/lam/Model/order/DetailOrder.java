package com.example.pokecenter.customer.lam.Model.order;

public class DetailOrder {

    private String productId;
    private int selectedOption;
    private int quantity;

    public DetailOrder(String productId, int selectedOption, int quantity) {
        this.productId = productId;
        this.selectedOption = selectedOption;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
