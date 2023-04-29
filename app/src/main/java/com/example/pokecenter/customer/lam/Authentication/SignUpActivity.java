package com.example.pokecenter.customer.lam.Authentication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}