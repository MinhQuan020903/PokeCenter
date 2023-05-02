package com.example.pokecenter.customer.lam.API;

import com.example.pokecenter.customer.lam.Model.address.Address;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseSupportAccount {

    private String urlDb = "https://pokecenter-ae954-default-rtdb.firebaseio.com/";

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void addNewAccountUsingApi(String email, String username, int role) throws IOException {

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("role", role);

        String userJson = new Gson().toJson(user);

        // create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // create request body
        RequestBody body = RequestBody.create(userJson, JSON);

        // create POST request
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + email + ".json")
                .post(body)
                .build();

        // send request and get response
        Response response = client.newCall(request).execute();
    }

    public void addNewAccount(String email, String username, int role) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("accounts");

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("role", role);

        usersRef.child(email.replace(".", ",")).setValue(user);
    }


    public int getRoleWithEmail(String email) throws IOException {

        int role = -1;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + email.replace(".", ",") + "/role.json")
                .build();

        Response response = client.newCall(request).execute();

        /*
        execute() and enqueue() are both methods in the OkHttpClient class that can be used to send HTTP requests,
        but they differ in how they handle the response.

        execute() sends the request synchronously and blocks the current thread until a response is received or an exception is thrown.
        This means that the program will not continue executing until the response is received

        On the other hand, enqueue() sends the request asynchronously and returns immediately without blocking the current thread.
        Instead, it uses a callback mechanism to handle the response when it is received.
        This allows the program to continue executing while the request is being sent and the response is being processed.
         */

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            role = Integer.parseInt(responseBody);
        }


        return role;
    }

}
