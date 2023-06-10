package com.example.pokecenter.vender.VenderTab.Home.Parcel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.customer.lam.Model.order.OrderAdapter;
import com.example.pokecenter.databinding.ActivityReceiverOrderBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Model.VenderOrder.ReceiveOrderAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ReceiveOrderActivity extends AppCompatActivity {

    private ActivityReceiverOrderBinding binding;
    private List<Order> myReceiveOrders;
    private RecyclerView rcvOrders;
    private ReceiveOrderAdapter receiveOrderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiverOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Receiver Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetupData();
    }

    private void SetupData() {

        receiveOrderAdapter = new ReceiveOrderAdapter(this, new ArrayList<>());
        rcvOrders = binding.rcvOrders;
        rcvOrders.setAdapter(receiveOrderAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            boolean isSuccess = true;

            try {
                myReceiveOrders = new FirebaseSupportVender().fetchingOrdersWithStatus("Order placed");
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            handler.post(() -> {

                if (finalIsSuccess) {

                    if (myReceiveOrders.size() > 0) {

                        binding.informText.setVisibility(View.INVISIBLE);
                        receiveOrderAdapter.setData(myReceiveOrders);

                    } else {

                        binding.informText.setText("You don't have any Orders yet.");
                        binding.informText.setVisibility(View.VISIBLE);
                        binding.rcvOrders.setVisibility(View.INVISIBLE);
                    }

                } else {

                    binding.informText.setText("Failed to load Orders data");
                    binding.informText.setVisibility(View.VISIBLE);
                    binding.rcvOrders.setVisibility(View.INVISIBLE);
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}