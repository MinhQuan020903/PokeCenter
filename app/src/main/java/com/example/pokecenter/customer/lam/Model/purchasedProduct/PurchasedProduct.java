package com.example.pokecenter.customer.lam.Model.purchasedProduct;

public class PurchasedProduct {
    private String productId;
    private boolean reviewed;
    private String purchasedDate;
    private int selectedOption;

    public PurchasedProduct(String productId, boolean reviewed, String purchasedDate, int selectedOption) {
        this.productId = productId;
        this.reviewed = reviewed;
        this.purchasedDate = purchasedDate;
        this.selectedOption = selectedOption;
    }

    public String getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(String purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
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