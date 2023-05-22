package com.example.pokecenter.vender.Model.Option;

import java.io.Serializable;

public class VenderOption implements Serializable {
    private String optionName;
    private String optionImage;
    private int currentQuantity;
    private int inputQuantity;
    private int cost;
    private int price;

    public VenderOption(String optionName, String optionImage, int currentQuantity, int inputQuantity, int cost, int price) {
        this.optionName = optionName;
        this.optionImage = optionImage;
        this.currentQuantity = currentQuantity;
        this.inputQuantity = inputQuantity;
        this.cost = cost;
        this.price = price;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
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

