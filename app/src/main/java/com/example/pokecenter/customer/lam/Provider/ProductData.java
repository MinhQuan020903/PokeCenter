package com.example.pokecenter.customer.lam.Provider;

import android.os.Handler;
import android.os.Looper;

import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.product.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductData {

    public static List<Product> fetchedProducts = new ArrayList<>();

    public static String status;

    public void fetchDataFromSever() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<Product> data;

            try {
                data = new FirebaseSupportCustomer().fetchingAllProductData();
            } catch (IOException e) {
                data = null;
            }

            List<Product> finalData = data;
            handler.post(() -> {
                if (finalData != null) {
                    fetchedProducts = finalData;
                    status = "SUCCESSFUL";
                } else {
                    status = "FAILED";
                }
            });
        });
    }
}
