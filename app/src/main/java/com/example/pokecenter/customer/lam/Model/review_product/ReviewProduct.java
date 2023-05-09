package com.example.pokecenter.customer.lam.Model.review_product;

public class ReviewProduct {

    private String reviewId;
    private String title;
    private String content;
    private int rate;
    private String customerId;

    public ReviewProduct(String reviewId, String title, String content, int rate, String customerId) {
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
        this.rate = rate;
        this.customerId = customerId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
