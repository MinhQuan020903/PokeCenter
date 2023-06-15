package com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.ProductRequest.AdminSupportProductRequestActivity;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.SupportTicket.AdminSupportCustomerActivity;
import com.example.pokecenter.databinding.ActivityAdminSupportSelectionBinding;

public class AdminSupportSelectionActivity extends AppCompatActivity {

    private ActivityAdminSupportSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        binding = ActivityAdminSupportSelectionBinding.inflate(getLayoutInflater());

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });

        binding.cvAdminRequestProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSupportSelectionActivity.this, AdminSupportProductRequestActivity.class);
                startActivity(intent);
            }
        });

        binding.cvAdminSupportCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSupportSelectionActivity.this, AdminSupportCustomerActivity.class);
                startActivity(intent);
            }
        });
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}