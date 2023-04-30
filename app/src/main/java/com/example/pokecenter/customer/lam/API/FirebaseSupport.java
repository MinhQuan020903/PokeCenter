package com.example.pokecenter.customer.lam.API;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseSupport {

    private String urlDb = "https://pokecenter-ae954-default-rtdb.firebaseio.com/";

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void saveUserUsingApi(String email, String username, int role) throws IOException {

        User user = new User(username, role);
        String userJson = new Gson().toJson(user);

        // create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // create request body
        RequestBody body = RequestBody.create(userJson, JSON);

        // create POST request
        Request request = new Request.Builder()
                .url(urlDb + "users/" + email + ".json")
                .post(body)
                .build();

        // send request and get response
        Response response = client.newCall(request).execute();
    }

    public void saveUser(String email, String username, int role) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        User user = new User(username, role);
        usersRef.child(email.replace(".", ",")).setValue(user);
    }


    public int getRoleWithEmail(String email) throws IOException {

        int role = -1;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "users/" + email.replace(".", ",") + "/role.json")
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

    class User {
        private String username;
        private int role;

        public User() {
        }

        public User(String username, int role) {
//            this.email = email;
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }
    }
}
