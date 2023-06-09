package com.example.pokecenter.customer.lam.Model.address;

import java.io.Serializable;

public class Address implements Serializable {
    private String id;
    private String receiverName;
    private String receiverPhoneNumber;
    private String numberStreetAddress;
    private String address2;
    private String type;
    private Boolean isDeliveryAddress;

    public Address(String id, String receiverName, String receiverPhoneNumber, String numberStreetAddress, String address2, String type, Boolean isDeliveryAddress) {
        this.id = id;
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.numberStreetAddress = numberStreetAddress;
        this.address2 = address2;
        this.type = type;
        this.isDeliveryAddress = isDeliveryAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getNumberStreetAddress() {
        return numberStreetAddress;
    }

    public void setNumberStreetAddress(String address1) {
        this.numberStreetAddress = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDeliveryAddress() {
        return isDeliveryAddress;
    }

    public void setDeliveryAddress(Boolean deliveryAddress) {
        isDeliveryAddress = deliveryAddress;
    }
}
