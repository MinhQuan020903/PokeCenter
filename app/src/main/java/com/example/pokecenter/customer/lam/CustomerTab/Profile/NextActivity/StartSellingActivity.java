package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.example.pokecenter.customer.lam.Authentication.SplashActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Singleton.UserInfo;
import com.example.pokecenter.databinding.ActivityStartSellingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartSellingActivity extends AppCompatActivity {

    private ActivityStartSellingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStartSellingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Set up store information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.startSellingScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        Account currentAccount = UserInfo.getInstance().getAccount();
        binding.shopPhone.setText(currentAccount.getPhoneNumber());

        binding.saveButton.setOnClickListener(view -> {

            if (!validateInfo()) {
                return;
            }

            updateInfo();
        });

    }

    private void updateInfo() {

        binding.saveButton.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccess = true;

            try {
                new FirebaseSupportAccount().updateAccountToVender(binding.shopPhone.getText().toString());
                new FirebaseSupportAccount().addNewVenderUsingApi(currentEmail, binding.shopName.getText().toString());
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            handler.post(() -> {
                if (finalIsSuccess) {

                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
                    sharedPreferences.edit().putInt("role", 1).commit();
                    startActivity(new Intent(this, SplashActivity.class));
                    finishAffinity();

                } else {
                    Toast.makeText(this, "Failed to connect server!", Toast.LENGTH_SHORT).show();
                }

                binding.saveButton.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            });
        });

    }

    private boolean validateInfo() {
        boolean isValid = true;

        if (binding.shopName.getText().length() == 0) {
            binding.shopName.setError("You have not entered shop name");
            isValid = false;
        }

        if (binding.shopPhone.getText().length() == 0) {
            binding.shopPhone.setError("You have not entered phone number");
            return false;
        }

        if (!Patterns.PHONE.matcher(binding.shopPhone.getText().toString()).matches()) {
            binding.shopPhone.setError("Phone number is invalid");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}