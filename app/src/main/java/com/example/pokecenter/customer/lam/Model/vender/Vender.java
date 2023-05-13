package com.example.pokecenter.customer.lam.Model.vender;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.pokecenter.customer.lam.Model.product.Product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Vender implements Parcelable {

    private String venderId;

    private String shopName;

    private String avatar;

    private String address;

    private String phoneNumber;

    private int followCount;

    private String registrationDate;

    private int totalProduct;

    private String background;

    public Vender() {

    }

    public Vender(String venderId, String shopName, String avatar, String address, String phoneNumber, int followCount, String registrationDate, int totalProduct, String background) {

        this.venderId = venderId;
        this.shopName = shopName;
        this.avatar = avatar;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.followCount = followCount;
        this.registrationDate = registrationDate;
        this.totalProduct = totalProduct;
        this.background = background;

    }

    protected Vender(Parcel in) {
        venderId = in.readString();
        shopName = in.readString();
        avatar = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        followCount = in.readInt();
        registrationDate = in.readString();
        totalProduct = in.readInt();
        background = in.readString();
    }

    public static final Creator<Vender> CREATOR = new Creator<Vender>() {
        @Override
        public Vender createFromParcel(Parcel in) {
            return new Vender(in);
        }

        @Override
        public Vender[] newArray(int size) {
            return new Vender[size];
        }
    };

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registerDay) {
        this.registrationDate = registerDay;
    }

    public int getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(int totalProduct) {
        this.totalProduct = totalProduct;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(venderId);
        parcel.writeString(shopName);
        parcel.writeString(avatar);
        parcel.writeString(address);
        parcel.writeString(phoneNumber);
        parcel.writeInt(followCount);
        parcel.writeString(registrationDate);
        parcel.writeInt(totalProduct);
        parcel.writeString(background);
    }
}
