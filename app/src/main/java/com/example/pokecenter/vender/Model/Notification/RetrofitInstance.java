package com.example.pokecenter.vender.Model.Notification;

import com.example.pokecenter.vender.API.NotificationAPI;
import com.example.pokecenter.vender.Util.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;

    public static NotificationAPI getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NotificationAPI.class);
    }
}
