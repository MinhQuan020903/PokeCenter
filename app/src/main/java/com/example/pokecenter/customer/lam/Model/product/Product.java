package com.example.pokecenter.customer.lam.Model.product;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Product implements Serializable {
    private String id;
    private String name;
    private String desc;
    private List<String> images;
    private List<Option> options;

    public Product(String id, String name, String desc, List<String> images, List<Option> options) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.images = images;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product(Object id, Object name, Object desc, Object images, Object options) {
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

    public List<String> getAllOptionsName() {
        return new ArrayList<>(options.stream().map(Option::getOptionName).collect(Collectors.toList()));
    }

    public List<String> copyListImage() {
        return new ArrayList<>(images);
    }

}
