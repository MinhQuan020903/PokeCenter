package com.example.pokecenter.vender.API;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FirebaseSupportVender {

    private String urlDb = "https://pokecenter-ae954-default-rtdb.firebaseio.com/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public void addNewProduct(Product newProduct) throws IOException {

        // create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        String venderId = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");

        /* create pushData */
        Map<String, Object> pushData = new HashMap<>();
        pushData.put("name", newProduct.getName());
        pushData.put("desc", newProduct.getDesc());
        pushData.put("venderId", venderId);
        pushData.put("images", newProduct.getImages());
        /* convert pushData to Json string */
        String userJsonData = new Gson().toJson(pushData);

        // create request body
        RequestBody body = RequestBody.create(userJsonData, JSON);

        // create POST request

        Request request = new Request.Builder()
                .url(urlDb + "/products.json")
                .post(body)
                .build();


        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, String> extractedData = new Gson().fromJson(response.body().string(), type);
            newProduct.setId(extractedData.get("name"));
        }

        //Post Options
        for (int i=0;i<newProduct.getOptions().size();i++) {
            pushData = new HashMap<>();
            pushData.put("currentQuantity", newProduct.getOptions().get(i).getInputQuantity());
            pushData.put("inputQuantity", newProduct.getOptions().get(i).getInputQuantity());
            pushData.put("optionImage", newProduct.getOptions().get(i).getOptionImage());
            pushData.put("price", newProduct.getOptions().get(i).getPrice());
            /* convert pushData to Json string */
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("products/"+newProduct.getId().toString()+"/options/");
            usersRef.child(newProduct.getOptions().get(i).getOptionName()).setValue(pushData);
        }

    }
}
