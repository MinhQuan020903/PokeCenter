package com.example.pokecenter.customer.lam.Model.review_product;

public class ReviewProduct {

    private String reviewId;
    private String title;
    private String content;
    private int rate;
    private String customerName;
    private String customerImage;

    public ReviewProduct() {

    }

    public ReviewProduct(String reviewId, String title, String content, int rate, String customerName, String customerImage) {
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
        this.rate = rate;
        this.customerName = customerName;
        this.customerImage = customerImage;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }
}
