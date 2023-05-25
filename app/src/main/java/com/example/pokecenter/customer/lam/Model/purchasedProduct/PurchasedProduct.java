package com.example.pokecenter.customer.lam.Model.purchasedProduct;

public class PurchasedProduct {
    private String productId;
    private boolean reviewed;

    public PurchasedProduct(String productId, boolean reviewed) {
        this.productId = productId;
        this.reviewed = reviewed;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
}
