package com.example.pokecenter.customer.lam.Model.product;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String name;
    private String desc;
    private List<String> images;
    private List<Option> options;

    public Product(String name, String desc, List<String> images, List<Option> options) {
        this.name = name;
        this.desc = desc;
        this.images = images;
        this.options = options;
    }

    public Product(Object name, Object desc, Object images, Object options) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
