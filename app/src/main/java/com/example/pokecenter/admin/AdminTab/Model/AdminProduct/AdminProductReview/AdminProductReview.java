package com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductReview;

import java.io.Serializable;

public class AdminProductReview implements Serializable {
    private String id;
    private String content;
    private String createDate;
    private String customerId;
    private String productId;
    private int rate;
    private String title;

    public AdminProductReview() {
    }


    public AdminProductReview(String id, String content, String createDate, String customerId, String productId, int rate, String title) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.customerId = customerId;
        this.productId = productId;
        this.rate = rate;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
