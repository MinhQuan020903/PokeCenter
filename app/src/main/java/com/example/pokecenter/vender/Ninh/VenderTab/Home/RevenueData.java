package com.example.pokecenter.vender.Ninh.VenderTab.Home;

import java.time.LocalDate;
public class RevenueData {
    private LocalDate date;
    private int revenue;

    public RevenueData(LocalDate date, int revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getRevenue() {
        return revenue;
    }
}