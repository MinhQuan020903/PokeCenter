package com.example.pokecenter.customer.lam.Model.checkout_item;

public class CheckoutItem {
    private String productId;
    private String name;
    private String image;
    private int selectedOption;
    private int optionSize;
    private int price;
    private int quantity;
    private String venderId;

    public CheckoutItem() {

    }

    public CheckoutItem(String productId, String name, String image, int selectedOption, int optionSize, int price, int quantity, String venderId) {
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.selectedOption = selectedOption;
        this.optionSize = optionSize;
        this.price = price;
        this.quantity = quantity;
        this.venderId = venderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getOptionSize() {
        return optionSize;
    }

    public void setOptionSize(int optionSize) {
        this.optionSize = optionSize;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }
}
