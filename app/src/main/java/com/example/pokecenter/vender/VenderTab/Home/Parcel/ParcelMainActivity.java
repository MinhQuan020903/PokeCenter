package com.example.pokecenter.vender.VenderTab.Home.Parcel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.databinding.ActivityParcelMainBinding;

import java.util.List;

public class ParcelMainActivity extends AppCompatActivity {
    ActivityParcelMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParcelMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Parcel Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.receiveOrder.setOnClickListener(view -> {
            startActivity(new Intent(this, ReceiveOrderActivity.class));
        });

        binding.packagedOrder.setOnClickListener(view -> {
            startActivity(new Intent(this, PackagedOrdersActivity.class));
        });
        binding.CompleteOrder.setOnClickListener(view -> {
            startActivity(new Intent(this, CompletedOrderActivity.class));
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}