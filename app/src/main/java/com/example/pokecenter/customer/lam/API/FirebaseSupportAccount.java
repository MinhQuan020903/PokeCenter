package com.example.pokecenter.customer.lam.API;

import com.example.pokecenter.customer.lam.Model.account.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
                .url(urlDb + "accounts/" + email.replace(".", ",") + ".json")
                .put(body)
                .build();

        // send request and get response
        Response response = client.newCall(request).execute();
    }

    public void addNewAccount(String email, String username, int role, String gender, String phoneNumber) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("accounts");

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("role", role);
        user.put("gender", gender);
        user.put("phoneNumber", phoneNumber);
        user.put("avatar", "https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        user.put("registrationDate", dateFormat.format(Calendar.getInstance().getTime()));

        usersRef.child(email.replace(".", ",")).setValue(user);
    }

    public void addNewVenderUsingApi(String email, String shopName) throws IOException {
        Map<String, Object> venderData = new HashMap<>();
        venderData.put("shopName", shopName);
        venderData.put("followCount", 0);
        venderData.put("totalProduct", 0);

        String userJson = new Gson().toJson(venderData);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(userJson, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "venders/" + email.replace(".", ",") + ".json")
                .patch(body)
                .build();

        // send request and get response
        Response response = client.newCall(request).execute();
    }

    public void addNewVender(String email, String shopName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("venders");

        Map<String, Object> venderData = new HashMap<>();
        venderData.put("shopName", shopName);
        venderData.put("followCount", 0);
        venderData.put("totalProduct", 0);


        usersRef.child(email.replace(".", ",")).setValue(venderData);
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

    public Account fetchingCurrentAccount() throws IOException {

        Account fetchedAccount = new Account();

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + ".json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseBody = response.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> fetchedData = new Gson().fromJson(responseBody, type);

            fetchedAccount.setAvatar((String) fetchedData.get("avatar"));
            fetchedAccount.setUsername((String) fetchedData.get("username"));
            fetchedAccount.setGender((String) fetchedData.get("gender"));
            fetchedAccount.setPhoneNumber((String) fetchedData.get("phoneNumber"));
            fetchedAccount.setRegistrationDate((String) fetchedData.get("registrationDate"));

        }

        return fetchedAccount;
    }

    public void updateAccountInfo(Account account) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("avatar", account.getAvatar());
        updateData.put("username", account.getUsername());
        updateData.put("gender", account.getGender());
        updateData.put("phoneNumber", account.getPhoneNumber());

        String jsonData = new Gson().toJson(updateData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();

    }

    public void updateAccountToVender(String phone) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("phoneNumber", phone);
        updateData.put("role", 1);

        String jsonData = new Gson().toJson(updateData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public boolean checkUserRegisteredForSelling() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Request request = new Request.Builder()
                .url(urlDb + "venders/" + emailWithCurrentUser.replace(".", ",") + ".json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseString = response.body().string();

            return responseString.equals("null") ? false : true;

        }
        return true;
    }

}
