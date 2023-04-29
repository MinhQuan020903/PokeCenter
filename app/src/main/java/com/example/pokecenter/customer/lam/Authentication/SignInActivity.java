package com.example.pokecenter.customer.lam.Authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivitySignInBinding;
import com.example.pokecenter.databinding.ActivitySignUpBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        binding.signUpTextView.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}