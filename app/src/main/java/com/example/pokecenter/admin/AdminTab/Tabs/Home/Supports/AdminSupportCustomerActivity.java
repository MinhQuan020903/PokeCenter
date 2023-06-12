package com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityAdminSupportCustomerBinding;
import com.example.pokecenter.databinding.ActivityAdminSupportProductRequestBinding;

public class AdminSupportCustomerActivity extends AppCompatActivity {
    private ActivityAdminSupportCustomerBinding binding;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Product Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityAdminSupportCustomerBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}