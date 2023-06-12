package com.example.pokecenter.vender.VenderTab.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityVenderNotificationBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Model.Notification.NotificationData;
import com.example.pokecenter.vender.Model.Notification.PushNotification;
import com.example.pokecenter.vender.Model.Notification.RetrofitInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenderNotificationActivity extends AppCompatActivity {
    ActivityVenderNotificationBinding binding;
    private InputMethodManager inputMethodManager;
    private String token;

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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        //Get new FCM registration token
                        token = task.getResult();

                        //Log and toast
                        Log.e("TAG", token);
                    }
                });

        // Notify in app
        binding.btnNotifyInApp.setOnClickListener(view -> {
            try {
                 new FirebaseSupportVender()
                         .updateRegistrationToken(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                token);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        binding.btnNotifyInLockscreen.setOnClickListener(view -> {
            if (!token.isEmpty()) {
                PushNotification notification = new PushNotification(
                        new NotificationData("Tín đẹp trai", "Tín đẹp trai đã gởi thông báo cho bạn", "orders", false, "12-06-2022"),
                        token );

                sendNotification(notification);
            }
        });

        setContentView(binding.getRoot());
    }

    public void sendNotification (PushNotification notification) {
        RetrofitInstance.getApi().postNotification(notification).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d("TAG", "Response: " + response.body() != null ? response.body().string() : "");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e("TAG", response.errorBody() != null ? response.errorBody().toString() : "");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
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