package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.Suport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityCustomerSupportBinding;

public class CustomerSupportActivity extends AppCompatActivity {

    private ActivityCustomerSupportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Support Center");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityCustomerSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getSupport.setOnClickListener(view -> {
            startActivity(new Intent(this, CustomerGetSupportActivity.class));
        });

        binding.submitForm.setOnClickListener(view -> {
            startActivity(new Intent(this, CustomerSupportInFindingProductActivity.class));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}