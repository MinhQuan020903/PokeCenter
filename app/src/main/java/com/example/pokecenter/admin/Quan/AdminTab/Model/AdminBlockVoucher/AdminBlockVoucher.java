package com.example.pokecenter.admin.Quan.AdminTab.Model.AdminBlockVoucher;

import java.io.Serializable;

public class AdminBlockVoucher implements Serializable {
    private String id;
    private String name;
    private int currentQuantity;
    private String startDate;
    private String endDate;
    private int value;

    public AdminBlockVoucher() {
    }

    public AdminBlockVoucher(String name, int currentQuantity, String startDate, String endDate, int value) {
        this.name = name;
        this.currentQuantity = currentQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
