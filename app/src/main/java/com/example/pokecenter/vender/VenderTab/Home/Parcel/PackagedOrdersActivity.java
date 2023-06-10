package com.example.pokecenter.vender.VenderTab.Home.Parcel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.databinding.ActivityPackagedOrdersBinding;
import com.example.pokecenter.databinding.ActivityReceiverOrderBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Model.VenderOrder.ReceiveOrderAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PackagedOrdersActivity extends AppCompatActivity {

    private ActivityPackagedOrdersBinding binding;

    private List<Order> myPackagedOrders;
    private RecyclerView rcvOrders;
    private ReceiveOrderAdapter packagedOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPackagedOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Packaged Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetupData();
    }

    private void SetupData() {
        packagedOrderAdapter = new ReceiveOrderAdapter(this, new ArrayList<>());
        rcvOrders = binding.rcvOrders;
        rcvOrders.setAdapter(packagedOrderAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            boolean isSuccess = true;

            try {
                myPackagedOrders = new FirebaseSupportVender().fetchingOrdersWithStatus("Packaged");
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            handler.post(() -> {

                if (finalIsSuccess) {

                    if (myPackagedOrders.size() > 0) {

                        binding.informText.setVisibility(View.INVISIBLE);
                        packagedOrderAdapter.setData(myPackagedOrders);

                    } else {

                        binding.informText.setText("You don't have any Packaged orders yet.");
                        binding.informText.setVisibility(View.VISIBLE);
                        binding.rcvOrders.setVisibility(View.INVISIBLE);
                    }

                } else {

                    binding.informText.setText("Failed to load Packaged data");
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