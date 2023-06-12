package com.example.pokecenter.vender.VenderTab.Home;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityVenderNotificationBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;

import java.io.IOException;

public class VenderNotificationActivity extends AppCompatActivity {
    ActivityVenderNotificationBinding binding;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenderNotificationBinding.inflate(getLayoutInflater());

        getWindow().setStatusBarColor(getColor(R.color.light_primary));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //venderOrders = OrderData.getListOrders();
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Notify in app
        binding.btnNotifyInApp.setOnClickListener(view -> {
            try {
                String token = new FirebaseSupportVender().getTokenWithEmail("ngoctin0809@gmail.com");
            } catch (IOException e) {
                throw new RuntimeException(e);
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