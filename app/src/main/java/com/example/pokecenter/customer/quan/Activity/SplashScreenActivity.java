package com.example.pokecenter.customer.quan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.pokecenter.MainActivity;
import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivitySplashScreenBinding;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    //Define ProgressBar value at the beginning of the activity
    private Integer progressInt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // Define the delay time in milliseconds
        int delayMillis = 2000; // 2 seconds

        // Create a new Runnable object that will run after the delay
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Create a new Intent object to start the next activity
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                // Add animation in activity transition
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish(); // Optional: finish the current activity to prevent the user from returning to it with the back button

            }
        };

        // Use the Handler object to post the Runnable object with the specified delay
        handler.postDelayed(runnable, delayMillis);
    }


}