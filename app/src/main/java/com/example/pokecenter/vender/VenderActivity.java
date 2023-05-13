package com.example.pokecenter.vender;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.example.pokecenter.databinding.ActivityVenderBinding;
import com.google.firebase.auth.FirebaseAuth;

public class VenderActivity extends AppCompatActivity {

    private ActivityVenderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenderBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}