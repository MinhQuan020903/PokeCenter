package com.example.pokecenter.customer.lam.Model.pokemon;

public class Pokemon {

    private String name;
    private String imageUrl;

    private String type;

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Pokemon(String name, String imageUrl, String type) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getType() { return type; }
}
