package com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct;

import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminOption.AdminOption;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminProduct implements Serializable {
    private String id;
    private String desc;
    private String name;
    private String venderId;
    private ArrayList<String> images;
    private ArrayList<AdminOption> adminOptions;

    public AdminProduct() {
    }

    public AdminProduct(String id, String desc, String name, String venderId, ArrayList<String> images, ArrayList<AdminOption> adminOptions) {
        this.id = id;
        this.desc = desc;
        this.name = name;
        this.venderId = venderId;
        this.images = images;
        this.adminOptions = adminOptions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<AdminOption> getOptions() {
        return adminOptions;
    }

    public void setOptions(ArrayList<AdminOption> adminOptions) {
        this.adminOptions = adminOptions;
    }

    @Override
    public String toString() {
        return "AdminProduct{" +
                "id=" + id + "\n" +
                "desc=" + desc + "\n" +
                "name=" + name + "\n"  +
                "venderId='" + venderId + "\n"  +
                "images=" + images.toString() + "\n" +
                "adminOptions=" + adminOptions.toString() +
                '}';
    }

    public Long getLowestPriceFromOptions() {
        Long lowestPrice = Long.MAX_VALUE;
        for (AdminOption adminOption : adminOptions) {
            if (adminOption.getPrice() < lowestPrice) {
                lowestPrice = adminOption.getPrice();
            }
        }
        return lowestPrice;
    }

    public Long getHighestPriceFromOptions() {
        Long highestPrice = Long.MIN_VALUE;
        for (AdminOption adminOption : adminOptions) {
            if (adminOption.getPrice() > highestPrice) {
                highestPrice = adminOption.getPrice();
            }
        }
        return highestPrice;
    }
}
