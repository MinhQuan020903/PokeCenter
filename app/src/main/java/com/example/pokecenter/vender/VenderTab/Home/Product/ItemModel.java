package com.example.pokecenter.vender.VenderTab.Home.Product;

import java.util.ArrayList;

public class ItemModel {
    private String ItemName, ItemDesc, ItemId;
    private ArrayList<String> ImageUrls;

    // we need empty constructor
    public ItemModel() {
    }

    public ItemModel(String itemName, String itemDesc, String itemId, ArrayList<String> imageUrls) {
        ItemName = itemName;
        ItemDesc = itemDesc;
        ItemId = itemId;
        ImageUrls = imageUrls;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemDesc() {
        return ItemDesc;
    }

    public void setItemDesc(String itemDesc) {
        ItemDesc = itemDesc;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public ArrayList<String> getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        ImageUrls = imageUrls;
    }
}
