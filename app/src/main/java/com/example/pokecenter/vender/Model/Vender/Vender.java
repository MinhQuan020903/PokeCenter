package com.example.pokecenter.vender.Model.Vender;

import java.io.Serializable;

public class Vender implements Serializable {
    private String venderId;

    private String shopName;

    private int followCount;


    public Vender(){}
    public Vender(String venderId, String shopName, int followCount) {
        this.venderId = venderId;
        this.shopName = shopName;
        this.followCount = followCount;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }
}
