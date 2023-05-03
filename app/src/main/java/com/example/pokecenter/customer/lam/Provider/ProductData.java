package com.example.pokecenter.customer.lam.Provider;

import android.os.Handler;
import android.os.Looper;

import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.product.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductData {

    public static Map<String, Product> fetchedProducts = new HashMap<>();

    public static List<String> trendingProductsId = new ArrayList<>();

    public static String status;

    public void fetchDataFromSever() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                new FirebaseSupportCustomer().fetchingAllProductData();
            } catch (IOException e) {
                isSuccessful = false;
            }
            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {
                    status = "SUCCESSFUL";
                } else {
                    status = "FAILED";
                }
            });
        });
    }
}
