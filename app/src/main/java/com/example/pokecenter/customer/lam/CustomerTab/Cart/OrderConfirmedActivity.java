package com.example.pokecenter.customer.lam.CustomerTab.Cart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerOrdersActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.databinding.ActivityOrderConfirmedBinding;

public class OrderConfirmedActivity extends AppCompatActivity {

    private ActivityOrderConfirmedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.light_canvas));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivityOrderConfirmedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goToOrdersButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CustomerOrdersActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        binding.continueShoppingButton.setOnClickListener(view -> {
            startActivity(new Intent(this, CustomerActivity.class));
            finishAffinity();
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CustomerActivity.class));
        finishAffinity();
    }
}