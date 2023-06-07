package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.pokecenter.databinding.ActivityCustomerOrdersBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerOrdersActivity extends AppCompatActivity {

    private ActivityCustomerOrdersBinding binding;
    private RecyclerView rcvOrders;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomerActivity.setEnableBottomNavigation(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /* Set color to title */
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding = ActivityCustomerOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rcvOrders = binding.rcvOrders;
        fetchingAndSetupData();

    }

    private void fetchingAndSetupData() {

        orderAdapter = new OrderAdapter(this, new ArrayList<>());
        rcvOrders.setAdapter(orderAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<Order> fetchedOrders = new ArrayList<>();
            boolean isSuccess = true;

            try {
                fetchedOrders = new FirebaseSupportCustomer().fetchingOrdersData();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            List<Order> finalFetchedOrders = fetchedOrders;
            handler.post(() -> {

                if (finalIsSuccess) {

                    if (finalFetchedOrders.size() > 0) {

                        binding.informText.setVisibility(View.INVISIBLE);
                        orderAdapter.setData(finalFetchedOrders);

                    } else {
                        binding.rcvOrders.setVisibility(View.INVISIBLE);
                    }

                } else {

                    binding.informText.setText("Failed to load Orders data");
                    binding.rcvOrders.setVisibility(View.INVISIBLE);
                }

                binding.progressBar.setVisibility(View.INVISIBLE);

                CustomerActivity.setEnableBottomNavigation(true);
            });
        });

    }

    @Override
    public boolean onSupportNavigateUp() {

        if (getIntent().getStringExtra("from").equals("OrderConfirmedActivity")) {
            startActivity(new Intent(this, CustomerActivity.class));
        }

        finish();
        return true;
    }
}