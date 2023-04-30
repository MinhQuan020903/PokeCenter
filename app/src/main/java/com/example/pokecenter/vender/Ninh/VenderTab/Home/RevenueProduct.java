package com.example.pokecenter.vender.Ninh.VenderTab.Home;

public class RevenueProduct {
    private String name;
    private int revenue;
    public RevenueProduct(String a, int b){
        name=a;
        revenue=b;
    }

    public String getName() {
        return name;
    }

    public int getRevenue() {
        return revenue;
    }
}
