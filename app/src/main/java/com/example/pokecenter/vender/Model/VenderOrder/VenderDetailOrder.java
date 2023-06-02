package com.example.pokecenter.vender.Model.VenderOrder;

public class VenderDetailOrder {
    private String productId;
    private int selectedOption;
    private int quantity;
    private String createDate;
    private String name;
    private int price;
    public VenderDetailOrder(String productId, int selectedOption, int quantity) {
        this.productId = productId;
        this.selectedOption = selectedOption;
        this.quantity = quantity;
    }
    public VenderDetailOrder(String productId, int selectedOption, int quantity, String createDate, String name, int price)
    {
        this.productId = productId;
        this.selectedOption = selectedOption;
        this.quantity = quantity;
        this.createDate = createDate;
        this.name = name;
        this.price = price;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public void setPrice(int price){
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
