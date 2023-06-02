package com.example.pokecenter.vender.Provider;

import android.os.Handler;
import android.os.Looper;

import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Model.VenderOrder.VenderOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderData {

    public static Map<String, VenderOrder> fetchedOrders = new HashMap<>();

    public static List<String> trendingOrdersId = new ArrayList<>();

    public static String status;

    public static void fetchDataFromSever() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                 new FirebaseSupportVender().fetchingVenderOrdersData();
            } catch (IOException e) {
                isSuccessful = false;
            }
            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {
                    status = "SUCCESSFUL";
                } else {
                    status = "FAILED";
                    fetchDataFromSever();
                }
            });
        });
    }

    public static List<VenderOrder> getListOrders() {
        return new ArrayList<>(fetchedOrders.values());
    }
}
