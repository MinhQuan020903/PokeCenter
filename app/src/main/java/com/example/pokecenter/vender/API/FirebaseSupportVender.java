package com.example.pokecenter.vender.API;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Model.notification.Notification;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.order.DetailOrder;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.vender.Model.Vender.Vender;
import com.example.pokecenter.vender.Model.VenderOrder.VenderDetailOrder;
import com.example.pokecenter.vender.Model.VenderOrder.VenderOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
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
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, String> extractedData = new Gson().fromJson(response.body().string(), type);
            newProduct.setId(extractedData.get("name"));
        }

        //Post Options
        for (int i = 0; i < newProduct.getOptions().size(); i++) {
            pushData = new HashMap<>();
            pushData.put("currentQuantity", newProduct.getOptions().get(i).getInputQuantity());
            pushData.put("inputQuantity", newProduct.getOptions().get(i).getInputQuantity());
            pushData.put("optionImage", newProduct.getOptions().get(i).getOptionImage());
            pushData.put("price", newProduct.getOptions().get(i).getPrice());
            /* convert pushData to Json string */
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("products/" + newProduct.getId() + "/options/");
            usersRef.child(newProduct.getOptions().get(i).getOptionName()).setValue(pushData);
        }
    }

    public List<VenderOrder> fetchingVenderOrdersData() throws IOException {
        List<VenderOrder> fetchedVenderOrders = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the Firebase Realtime Database endpoint
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pokecenter-ae954-default-rtdb.firebaseio.com/orders.json").newBuilder();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        urlBuilder.addQueryParameter("orderBy", "\"venderId\"")
                .addQueryParameter("equalTo", "\"" + emailWithCurrentUser.replace(".", ",") + "\"");

        String url = urlBuilder.build().toString();

        // Create an HTTP GET request
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<Map<String, VenderOrder>>() {
            }.getType();
            Map<String, VenderOrder> fetchedData = new Gson().fromJson(responseString, type);

            for (Map.Entry<String, VenderOrder> entry : fetchedData.entrySet()) {
                String orderKey = entry.getKey();
                VenderOrder venderOrder = entry.getValue();

                // Set the order key if necessary
                //venderOrder.setOrderKey(orderKey);

                fetchedVenderOrders.add(venderOrder);
            }
        }

        return fetchedVenderOrders;
    }

    public List<VenderOrder> fetchingOrdersData2() throws IOException {
        List<VenderOrder> fetchedOrders = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the Firebase Realtime Database endpoint for orders
        HttpUrl.Builder ordersUrlBuilder = HttpUrl.parse("https://pokecenter-ae954-default-rtdb.firebaseio.com/orders.json").newBuilder();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ordersUrlBuilder.addQueryParameter("orderBy", "\"venderId\"")
                .addQueryParameter("equalTo", "\"" + emailWithCurrentUser.replace(".", ",") + "\"");

        String ordersUrl = ordersUrlBuilder.build().toString();

        // Create an HTTP GET request for orders
        Request ordersRequest = new Request.Builder()
                .url(ordersUrl)
                .build();

        Response ordersResponse = client.newCall(ordersRequest).execute();

        if (ordersResponse.isSuccessful()) {
            String ordersResponseString = ordersResponse.body().string();

            if (ordersResponseString.equals("null")) {
                return new ArrayList<>();
            }

            Type ordersType = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> fetchedOrdersData = new Gson().fromJson(ordersResponseString, ordersType);

            // Fetching product data separately
            String productsUrl = "https://pokecenter-ae954-default-rtdb.firebaseio.com/products.json";

            // Create an HTTP GET request for products
            Request productsRequest = new Request.Builder()
                    .url(productsUrl)
                    .build();

            Response productsResponse = client.newCall(productsRequest).execute();

            if (productsResponse.isSuccessful()) {
                String productsResponseString = productsResponse.body().string();

                if (!productsResponseString.equals("null")) {
                    Type productsType = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
                    Map<String, Map<String, Object>> fetchedProductsData = new Gson().fromJson(productsResponseString, productsType);

                    fetchedOrdersData.forEach((key, value) -> {
                        List<Map<String, Object>> detailOrderData = (List<Map<String, Object>>) value.get("details");

                        List<VenderDetailOrder> details = new ArrayList<>();
                        detailOrderData.forEach(detailOrder -> {
                            String productId = (String) detailOrder.get("productId");
                            int selectedOption = ((Double) detailOrder.get("selectedOption")).intValue();
                            int quantity = ((Double) detailOrder.get("quantity")).intValue();

                            // Retrieve product details using productId
                            Map<String, Object> productData = fetchedProductsData.get(productId);

                            if (productData != null) {
                                String createDate = (String) value.get("createDate");
                                String name = (String) productData.get("name");
                                List<Option> options = new ArrayList<>();

                                Map<String, Map<String, Object>> optionsData = (Map<String, Map<String, Object>>) productData.get("options");
                                if (optionsData != null) {
                                    for (Map.Entry<String, Map<String, Object>> entry : optionsData.entrySet()) {
                                        String optionName = entry.getKey();
                                        Map<String, Object> optionData = entry.getValue();
                                        int price = ((Double) optionData.get("price")).intValue();

                                        Option option = new Option( price);
                                        options.add(option);
                                    }
                                }
                                int price = options.get(selectedOption).getPrice();
                                details.add(new VenderDetailOrder(productId, selectedOption, quantity, createDate, name, price));
                            }
                        });

                        fetchedOrders.add(new VenderOrder(
                                ((Double) value.get("totalAmount")).intValue(),
                                (String) value.get("createDate"),
                                details
                        ));
                    });
                }
            }
        }

        return fetchedOrders;
    }
    public void updateTotalProduct(int totalProduct) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, Integer> pushData = new HashMap<>();
        pushData.put("totalProduct", totalProduct);

        String jsonData = new Gson().toJson(pushData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "venders/" + FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",") + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public List<String> fetchingAllCategoryTag() throws IOException {
        List<String> fetchedCategoryTag = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "category.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> fetchedData = new Gson().fromJson(responseString, type);

            fetchedCategoryTag.addAll(fetchedData.keySet());
        }

        Collections.reverse(fetchedCategoryTag);

        return fetchedCategoryTag;
    }
    public void updatePokemonAfterAddProduct(String productId, List<String> myPokemon) throws IOException {
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("pokemons");

// Push the productId and Pokemon list to the "pokemons" structure in Firebase
        for ( String s: myPokemon) {
            Ref.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<String> existingPokemonList = new ArrayList<>();
                        Object value = dataSnapshot.getValue();

                        if (value instanceof List) {
                            existingPokemonList = (List<String>) value;
                        } else if (value instanceof String) {
                            existingPokemonList.add((String) value);
                        }

                        existingPokemonList.add(productId);

                        Ref.child(s).setValue(existingPokemonList)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Data successfully updated in Firebase
                                            notifyResult(true);
                                        } else {
                                            // Failed to update data in Firebase
                                            notifyResult(false);
                                        }
                                    }
                                });
                    } else {
                        // Handle the case where the product ID doesn't exist in Firebase
                        List<String> newPokemonList = new ArrayList<>();
                        newPokemonList.add(productId);

                        Ref.child(s).setValue(newPokemonList)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Data successfully added to Firebase
                                            notifyResult(true);
                                        } else {
                                            // Failed to add data to Firebase
                                            notifyResult(false);
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                    notifyResult(false);
                }

                private void notifyResult(boolean success) {
                    // Notify the result using a callback or any other mechanism
                    if (success) {
                        // Data was successfully updated/added in Firebase
                    } else {
                        // Failed to update/add data in Firebase
                    }
                }
            });

        }

    }
    public void updateCategoryAfterAddProduct(String productId, List<String> myCategory) throws IOException {
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("category");

// Push the productId and Pokemon list to the "pokemons" structure in Firebase
        for ( String s: myCategory) {
            Ref.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<String> existingPokemonList = new ArrayList<>();
                        Object value = dataSnapshot.getValue();

                        if (value instanceof List) {
                            existingPokemonList = (List<String>) value;
                        } else if (value instanceof String) {
                            existingPokemonList.add((String) value);
                        }

                        existingPokemonList.add(productId);

                        Ref.child(s).setValue(existingPokemonList)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Data successfully updated in Firebase
                                            notifyResult(true);
                                        } else {
                                            // Failed to update data in Firebase
                                            notifyResult(false);
                                        }
                                    }
                                });
                    } else {
                        // Handle the case where the product ID doesn't exist in Firebase
                        List<String> newPokemonList = new ArrayList<>();
                        newPokemonList.add(productId);

                        Ref.child(s).setValue(newPokemonList)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Data successfully added to Firebase
                                            notifyResult(true);
                                        } else {
                                            // Failed to add data to Firebase
                                            notifyResult(false);
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                    notifyResult(false);
                }

                private void notifyResult(boolean success) {
                    // Notify the result using a callback or any other mechanism
                    if (success) {
                        // Data was successfully updated/added in Firebase
                    } else {
                        // Failed to update/add data in Firebase
                    }
                }
            });

        }

    }
    public Account fetchingCurrentAccount( String id) throws IOException {

        Account fetchedAccount = new Account();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + id + ".json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseBody = response.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> fetchedData = new Gson().fromJson(responseBody, type);

            fetchedAccount.setAvatar((String) fetchedData.get("avatar"));
            fetchedAccount.setUsername((String) fetchedData.get("username"));
//            fetchedAccount.setGender((String) fetchedData.get("gender"));
//            fetchedAccount.setPhoneNumber((String) fetchedData.get("phoneNumber"));
//            fetchedAccount.setRegistrationDate((String) fetchedData.get("registrationDate"));
            fetchedAccount.setId(id);
            fetchedAccount.setRole((Integer) fetchedData.get("role"));
        }

        return fetchedAccount;
    }
    public Vender fetchingVenderById(String venderId) throws IOException {
        Vender fetchedVender = new Vender();

        fetchedVender.setVenderId(venderId);

        OkHttpClient client = new OkHttpClient();
        Request request1 = new Request.Builder()
                .url(urlDb + "venders/" + venderId + ".json")
                .build();

        Response response1 = client.newCall(request1).execute();

        if (response1.isSuccessful()) {
            String responseString = response1.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> fetchedVenderData = new Gson().fromJson(responseString, type);
            fetchedVender.setShopName((String) fetchedVenderData.get("shopName"));
            fetchedVender.setFollowCount(((Double) fetchedVenderData.get("followCount")).intValue());
            fetchedVender.setRevenue(((Double) fetchedVenderData.get("revenue")).intValue());

        } else {
            return null;
        }

        return fetchedVender;
    }

}
