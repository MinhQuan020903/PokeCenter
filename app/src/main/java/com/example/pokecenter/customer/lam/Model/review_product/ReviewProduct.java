package com.example.pokecenter.customer.lam.Model.review_product;

public class ReviewProduct {

    private String reviewId;
    private String title;
    private String content;
    private int rate;
    private String customerName;
    private String customerImage;
    private String createDate;

//    public ReviewProduct() {
//
//    }
//
//    public ReviewProduct(String reviewId, String title, String content, int rate, String customerName, String customerImage, String createDate) {
//        this.reviewId = reviewId;
//        this.title = title;
//        this.content = content;
//        this.rate = rate;
//        this.customerName = customerName;
//        this.customerImage = customerImage;
//        this.createDate = createDate;
//    }

    private ReviewProduct(Builder builder) {
        this.reviewId = builder.reviewId;
        this.title = builder.title;
        this.content = builder.content;
        this.rate = builder.rate;
        this.customerName = builder.customerName;
        this.customerImage = builder.customerImage;
        this.createDate = builder.createDate;
    }

    public static class Builder {
        private String reviewId;
        private String title;
        private String content;
        private int rate;
        private String customerName;
        private String customerImage;
        private String createDate;

        public Builder() {}

        public Builder withReviewId(String reviewId) {
            this.reviewId = reviewId;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withRate(int rate) {
            this.rate = rate;
            return this;
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder withCustomerImage(String customerImage) {
            this.customerImage = customerImage;
            return this;
        }

        public Builder withCreateDate(String createDate) {
            this.createDate = createDate;
            return this;
        }

        public ReviewProduct build() {
            return new ReviewProduct(this);
        }
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
