package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.databinding.ActivityVenderInformationBinding;
import com.squareup.picasso.Picasso;

import java.io.PipedInputStream;

public class VenderInformationActivity extends AppCompatActivity {

    private ActivityVenderInformationBinding binding;

    Vender receiveVender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenderInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        receiveVender = (Vender) getIntent().getSerializableExtra("vender object");



        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        setUpdataVenderInfo();
    }

    private void setUpdataVenderInfo() {

        Picasso.get().load(receiveVender.getBackground()).into(binding.backgroundImage);
        Picasso.get().load(receiveVender.getAvatar()).into(binding.venderAvatar);
        binding.shopName.setText(receiveVender.getShopName());
        binding.registrationDate.setText("Registration: " + receiveVender.getRegistrationDate());
        binding.totalProduct.setText(String.valueOf(receiveVender.getTotalProduct()));
        binding.followCount.setText(String.valueOf(receiveVender.getFollowCount()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}