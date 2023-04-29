package com.example.pokecenter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.databinding.ActivityCustomerBinding;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerActivity extends AppCompatActivity {

    private ActivityCustomerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        boolean rememberMe = getIntent().getBooleanExtra("rememberMe", false);
        if (!rememberMe) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}