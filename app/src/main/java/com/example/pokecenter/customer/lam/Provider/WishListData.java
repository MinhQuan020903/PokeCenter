package com.example.pokecenter.customer.lam.Provider;

import android.os.Handler;
import android.os.Looper;

import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishListData {

    public static Map<String, Boolean> fetchedWishList = new HashMap<>();
    public static boolean hasData = false;

    public static void fetchDataFromSever() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                fetchedWishList = new FirebaseSupportCustomer().fetchingWishList();
            } catch (IOException e) {
                isSuccessful = false;
            }
            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                hasData = finalIsSuccessful;
            });
        });
    }

    public static List<String> getWishListId() {
        return new ArrayList<>(fetchedWishList.keySet());
    }

}
