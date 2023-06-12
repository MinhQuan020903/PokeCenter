package com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminOption;

import java.io.Serializable;

public class AdminOption implements Serializable {
    private String id;
    private long cost;
    private int currentQuantity;
    private int inputQuantity;
    private String optionImage;
    private long price;

    public AdminOption() {
    }

    public AdminOption(String id, long cost, int currentQuantity, int inputQuantity, String optionImage, long price) {
        this.id = id;
        this.cost = cost;
        this.currentQuantity = currentQuantity;
        this.inputQuantity = inputQuantity;
        this.optionImage = optionImage;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public int getInputQuantity() {
        return inputQuantity;
    }

    public void setInputQuantity(int inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    public String getOptionImage() {
        return optionImage;
    }

    public void setOptionImage(String optionImage) {
        this.optionImage = optionImage;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AdminOption{" +
                "id='" + id + '\'' +
                ", cost=" + cost +
                ", currentQuantity=" + currentQuantity +
                ", inputQuantity=" + inputQuantity +
                ", optionImage='" + optionImage + '\'' +
                ", price=" + price +
                '}';
    }
}
