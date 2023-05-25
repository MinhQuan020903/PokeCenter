package com.example.pokecenter.customer.lam.Model.purchasedProduct;

import android.content.Context;

import java.util.List;

public class PurchasedProductAdapter {

    private Context mContext;
    private List<PurchasedProduct> mPurchasedProducts;

    public PurchasedProductAdapter(Context context, List<PurchasedProduct> purchasedProducts) {
        mContext = context;
        mPurchasedProducts = purchasedProducts;
    }



}
