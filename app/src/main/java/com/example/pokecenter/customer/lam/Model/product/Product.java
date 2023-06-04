package com.example.pokecenter.customer.lam.Model.product;

import com.example.pokecenter.customer.lam.Model.option.Option;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Product implements Serializable {
    private String id;
    private String name;
    private String desc;
    private List<String> images;
    private List<Option> options;
    private String venderId;

    public Product(String id, String name, String desc, List<String> images, List<Option> options, String venderId) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.images = images;
        this.options = options;
        this.venderId = venderId;
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

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public List<String> getAllOptionsName() {
        return new ArrayList<>(options.stream().map(Option::getOptionName).collect(Collectors.toList()));
    }

    public List<String> copyListImage() {
        return new ArrayList<>(images);
    }

    public int getProductSold() {
        AtomicInteger sum = new AtomicInteger();
        options.forEach(option -> sum.addAndGet(option.getInputQuantity() - option.getCurrentQuantity()));

        return sum.get();
    }

    public int getMinPrice() {
        int minPrice = options.get(0).getPrice();
        for (int i = 1; i < options.size(); ++i) {
            minPrice = Math.min(minPrice, options.get(i).getPrice());
        }
        return minPrice;
    }

    public int getMaxPrice() {
        int maxPrice = options.get(0).getPrice();
        for (int i = 1; i < options.size(); ++i) {
            maxPrice = Math.max(maxPrice, options.get(i).getPrice());
        }
        return maxPrice;
    }

}
