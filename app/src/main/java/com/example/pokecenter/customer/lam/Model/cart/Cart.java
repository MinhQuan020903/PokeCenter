package com.example.pokecenter.customer.lam.Model.cart;

import com.example.pokecenter.customer.lam.Model.product.Product;

public class Cart {
    private Product product;
    private int quantity;

    private boolean isChecked = false;

    private int selectedOption;

    public Cart(Product product, int quantity, int selectedOption) {
        this.product = product;
        this.quantity = quantity;
        this.selectedOption = selectedOption;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
