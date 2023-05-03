package com.example.pokecenter.customer.lam.Model.product;

import java.io.Serializable;

public class Option implements Serializable {
    private String optionImage;
    private int currentQuantity;
    private int inputQuantity;
    private int cost;
    private int price;

    public Option(String optionImage, int currentQuantity, int inputQuantity, int cost, int price) {
        this.optionImage = optionImage;
        this.currentQuantity = currentQuantity;
        this.inputQuantity = inputQuantity;
        this.cost = cost;
        this.price = price;
    }

    public String getOptionImage() {
        return optionImage;
    }

    public void setOptionImage(String optionImage) {
        this.optionImage = optionImage;
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
