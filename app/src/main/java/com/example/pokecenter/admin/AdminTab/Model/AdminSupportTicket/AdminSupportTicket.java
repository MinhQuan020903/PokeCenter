package com.example.pokecenter.admin.AdminTab.Model.AdminSupportTicket;

import java.io.Serializable;

public class AdminSupportTicket implements Serializable {
    private String id;
    private String contactMethod;
    private String createDate;
    private String customerId;
    private String desc;
    private String problemName;
    private String status;

    public AdminSupportTicket() {
    }

    public AdminSupportTicket(String id, String contactMethod, String createDate, String customerId, String desc, String problemName, String status) {
        this.id = id;
        this.contactMethod = contactMethod;
        this.createDate = createDate;
        this.customerId = customerId;
        this.desc = desc;
        this.problemName = problemName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
