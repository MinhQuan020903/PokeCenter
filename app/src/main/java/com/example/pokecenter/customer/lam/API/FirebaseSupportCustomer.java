package com.example.pokecenter.customer.lam.API;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.product.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseSupportCustomer {
    private String urlDb = "https://pokecenter-ae954-default-rtdb.firebaseio.com/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public void addNewAddressUsingApi(Address newAddress) throws IOException {

        // create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        /* create pushData */
        Map<String, Object> pushData = new HashMap<>();
        pushData.put("receiverName", newAddress.getReceiverName());
        pushData.put("receiverPhoneNumber", newAddress.getReceiverPhoneNumber());
        pushData.put("numberStreetAddress", newAddress.getNumberStreetAddress());
        pushData.put("address2", newAddress.getAddress2());
        pushData.put("type", newAddress.getType());
        pushData.put("isDeliveryAddress", newAddress.getDeliveryAddress());

        /* convert pushData to Json string */
        String userJsonData = new Gson().toJson(pushData);

        // create request body
        RequestBody body = RequestBody.create(userJsonData, JSON);

        // create POST request
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses.json")
                .post(body)
                .build();


        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, String> extractedData = new Gson().fromJson(response.body().string(), type);

            newAddress.setId(extractedData.get("name"));

            if (newAddress.getDeliveryAddress() == true) {
                removeOldDeliveryAddress(newAddress.getId());
            }

        }

    }

    private void removeOldDeliveryAddress(String newIdDeliveryAddress) throws IOException {

        String remoteId = "";

        /* set current Delivery Address to false locally*/
        for (Address address : MyAddressesActivity.myAddresses) {
            if (address.getDeliveryAddress() == true && address.getId() != newIdDeliveryAddress) {
                address.setDeliveryAddress(false);
                remoteId = address.getId();
                break;
            }
        }

        System.out.println("ID = " + remoteId);

        if (remoteId == "") {
            return;
        }

        /* set current Delivery Address to false remotely */
        OkHttpClient client = new OkHttpClient();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isDeliveryAddress", false);

        String jsonData = new Gson().toJson(updateData);

        RequestBody body = RequestBody.create(jsonData, JSON);
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String url = urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses/" + remoteId + ".json";

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public List<Address> fetchingAddressesData() throws IOException {
        List<Address> fetchedAddresses = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                // return list rỗng
                return new ArrayList<>();
            }

            Type type = new TypeToken<Object>(){}.getType();
            Map<String, Map<String, Object>> extractedData = new Gson().fromJson(responseString, type);

            extractedData.forEach((key, value) -> {

                fetchedAddresses.add(new Address(
                                key,
                                (String) value.get("receiverName"),
                                (String) value.get("receiverPhoneNumber"),
                                (String) value.get("numberStreetAddress"),
                                (String) value.get("address2"),
                                (String) value.get("type"),
                                (Boolean) value.get("isDeliveryAddress")
                        )
                );
            });
        }

        return fetchedAddresses;

    }

    public void deleteAddress(String id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses/" + id + ".json")
                .delete()
                .build();

        Response response = client.newCall(request).execute();
    }

    public void updateAddress(Address addressNeedsToBeUpdate) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("receiverName", addressNeedsToBeUpdate.getReceiverName());
        updateData.put("receiverPhoneNumber", addressNeedsToBeUpdate.getReceiverPhoneNumber());
        updateData.put("numberStreetAddress", addressNeedsToBeUpdate.getNumberStreetAddress());
        updateData.put("address2", addressNeedsToBeUpdate.getAddress2());
        updateData.put("type", addressNeedsToBeUpdate.getType());
        updateData.put("isDeliveryAddress", addressNeedsToBeUpdate.getDeliveryAddress());

        String userJsonData = new Gson().toJson(updateData);

        // create request body
        RequestBody body = RequestBody.create(userJsonData, JSON);

        // create POST request
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses/" + addressNeedsToBeUpdate.getId() + ".json")
                .patch(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            removeOldDeliveryAddress(addressNeedsToBeUpdate.getId());
        }
    }

    public void fetchingAllProductData() throws IOException {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlDb + "products.json").newBuilder();
        urlBuilder.addQueryParameter("orderBy", "\"price\"");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(urlDb + "products.json")
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return;
            }

            Type productType = new TypeToken<Object>(){}.getType();
            Map<String, Map<String, Object>> extractedData = new Gson().fromJson(responseString, productType);

            extractedData.forEach((key, value) -> {

                Map<String, Map<String, Object>> optionsData = (Map<String, Map<String, Object>>) value.get("options");

                List<Option> options = new ArrayList<>();

                optionsData.forEach((optionKey, optionValue) -> {
                    options.add(new Option(
                            (String) optionValue.get("optionName"),
                            (String) optionValue.get("optionImage"),
                            ((Double) optionValue.get("currentQuantity")).intValue(),
                            ((Double) optionValue.get("inputQuantity")).intValue(),
                            ((Double) optionValue.get("cost")).intValue(),
                            ((Double) optionValue.get("price")).intValue()
                    ));
                });

                ProductData.fetchedProducts.put(key, new Product(
                        (String) value.get("name"),
                        (String) value.get("desc"),
                        (List<String>) value.get("images"),
                        options
                ));
            });

        } else {
            // Handle the error
        }
    }

    public List<String> fetchingTrendingProductId(boolean isDemo) throws IOException {
        List<String> fetchedTrendingProductsId = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "trendingProducts.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<String>>(){}.getType();

            fetchedTrendingProductsId = new Gson().fromJson(responseString, type);
        }

        if (isDemo) {
            return fetchedTrendingProductsId.subList(0, 4);
        }

        return fetchedTrendingProductsId;
    }
}
