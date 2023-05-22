package com.example.pokecenter.customer.lam.Authentication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.pokecenter.admin.AdminActivity;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.ActivitySplashScreenBinding;
import com.example.pokecenter.vender.VenderTab.VenderProfileFragment;
import com.example.pokecenter.vender.VenderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    //Define ProgressBar value at the beginning of the activity
    private Integer progressInt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Fetch Products Data */
        ProductData.fetchDataFromSever();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            WishListData.fetchDataFromSever();
        }

        //Create a Timer for the progress bar to run
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressInt++;
                binding.progressBar.setProgress(progressInt);
                if(binding.progressBar.getProgress() >= 100) {
                    timer.cancel();
                }

            }
        }, 0, 10);

        Handler handler = new Handler();

        // Create a new Runnable object that will run after the delay
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser == null) {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                } else {

                    SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());

                    executor.execute(() -> {

                                Account fetchedAccountInfo = null;
                                boolean isSuccessful = true;

                                try {
                                    fetchedAccountInfo = new FirebaseSupportAccount().fetchingCurrentAccount();
                                } catch (IOException e) {
                                    isSuccessful = false;
                                }

                                boolean finalIsSuccessful = isSuccessful;
                                Account finalFetchedAccountInfo = fetchedAccountInfo;
                                handler.post(() -> {
                                    if (finalIsSuccessful) {
                                        goToNextActivityWith(sharedPreferences.getInt("role", -1));
                                        CustomerProfileFragment.currentAccount = finalFetchedAccountInfo;
                                        VenderProfileFragment.currentVender = finalFetchedAccountInfo;
                                    } else {
                                        Toast.makeText(SplashActivity.this, "Failed to connect server", Toast.LENGTH_LONG).show();
                                    }
                                });
                            });

                }

                // Add animation in activity transition
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        // Use the Handler object to post the Runnable object with the specified delay
        handler.postDelayed(runnable, 1100);
    }

    void goToNextActivityWith(int role) {
        switch (role) {
            case 0:
                startActivity(new Intent(this, CustomerActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, VenderActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, AdminActivity.class));
                break;
        }
        finishAffinity();
    }
}