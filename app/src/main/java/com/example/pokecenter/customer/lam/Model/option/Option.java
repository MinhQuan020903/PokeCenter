package com.example.pokecenter.customer.lam.Model.option;

import java.io.Serializable;

public class Option implements Serializable {
    private String optionName;
    private String optionImage;
    private int currentQuantity;
    private int inputQuantity;
    private int price;

    public Option(String optionName, String optionImage, int currentQuantity, int inputQuantity, int price) {
        this.optionName = optionName;
        this.optionImage = optionImage;
        this.currentQuantity = currentQuantity;
        this.inputQuantity = inputQuantity;
        this.price = price;
    }

    public Option( int price) {
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

    public void decreaseCurrentQuantity(int x) {
        this.currentQuantity -= x;
    }

    public int getInputQuantity() {
        return inputQuantity;
    }

    public void setInputQuantity(int inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
