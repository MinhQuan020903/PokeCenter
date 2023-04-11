package com.example.pokecenter.customer.lam.Model.product;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Product implements Parcelable {
    private String name;
    private String defaultImageUrl;
    private int price;
    private float starCount;
    private int stock;
    private ArrayList<String> additionalImageUrl;

    public Product(String name, String defaultImageUrl, int price, float starCount, int stock, ArrayList<String> additionalImageUrl) {
        this.name = name;
        this.defaultImageUrl = defaultImageUrl;
        this.price = price;
        this.starCount = starCount;
        this.stock = stock;
        this.additionalImageUrl = additionalImageUrl;
    }

    protected Product(Parcel in) {
        name = in.readString();
        defaultImageUrl = in.readString();
        price = in.readInt();
        starCount = in.readFloat();
        stock = in.readInt();
        additionalImageUrl = in.createStringArrayList();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultImageUrl() {
        return defaultImageUrl;
    }

    public void setDefaultImageUrl(String defaultImageUrl) {
        this.defaultImageUrl = defaultImageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getStarCount() {
        return starCount;
    }

    public void setStarCount(float starCount) {
        this.starCount = starCount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ArrayList<String> getAdditionalImageUrl() {
        return additionalImageUrl;
    }

    public void setAdditionalImageUrl(ArrayList<String> additionalImageUrl) {
        this.additionalImageUrl = additionalImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(defaultImageUrl);
        parcel.writeInt(price);
        parcel.writeFloat(starCount);
        parcel.writeInt(stock);
        parcel.writeStringList(additionalImageUrl);
    }
}
