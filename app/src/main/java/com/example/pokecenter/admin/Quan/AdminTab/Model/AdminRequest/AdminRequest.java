package com.example.pokecenter.admin.Quan.AdminTab.Model.AdminRequest;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminRequest implements Serializable {
    private String id;
    private String customerId;
    private String desc;
    private String name;
    private String createDate;
    private String status;
    private ArrayList<String> additionalImages;

    public AdminRequest() {
    }

    public AdminRequest(String id, String customerId, String desc, String name, String createDate, String status, ArrayList<String> additionalImages) {
        this.id = id;
        this.customerId = customerId;
        this.desc = desc;
        this.name = name;
        this.createDate = createDate;
        this.status = status;
        this.additionalImages = additionalImages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(ArrayList<String> additionalImages) {
        this.additionalImages = additionalImages;
    }
}
