package com.example.pokecenter.customer.lam.API;

import android.os.Handler;
import android.os.Looper;

import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseSupportCustomer {
    private String urlDb = "https://pokecenter-ae954-default-rtdb.firebaseio.com/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
                            (String) optionKey,
                            (String) optionValue.get("optionImage"),
                            ((Double) optionValue.get("currentQuantity")).intValue(),
                            ((Double) optionValue.get("inputQuantity")).intValue(),
                            ((Double) optionValue.get("cost")).intValue(),
                            ((Double) optionValue.get("price")).intValue()
                    ));
                });

                List<Option> sortedOptions = options.stream().sorted(Comparator.comparing(Option::getPrice)).collect(Collectors.toList());

                ProductData.fetchedProducts.put(key, new Product(
                        key,
                        (String) value.get("name"),
                        (String) value.get("desc"),
                        (List<String>) value.get("images"),
                        sortedOptions,
                        (String) value.get("venderId")
                ));
            });

        } else {
            // Handle the error
        }
    }

    public void fetchingTrendingProductId() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "trendingProducts.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return;
            }

            Type type = new TypeToken<List<String>>(){}.getType();
            ProductData.trendingProductsId = new Gson().fromJson(responseString, type);
        }
    }

    public List<String> fetchingProductIdByPokemonName(String name) throws IOException {
        List<String> fetchedProductsId = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDb + "pokemons/" + name.toLowerCase() + ".json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<String>>(){}.getType();
            fetchedProductsId = new Gson().fromJson(responseString, type);
        }

        return fetchedProductsId;
    }

    public void addNewCartUsingApi(String productId, int quantity, int selectedOption) throws IOException {
        /* Lấy productId làm key cho cart, vì mỗi card tương ứng 1 sản phẩm mà id sản phẩm là unique */

        OkHttpClient client = new OkHttpClient();

        Map<String, Integer> pushData = new HashMap<>();
        pushData.put("quantity", quantity);
        pushData.put("selectedOption", selectedOption);

        String jsonData = new Gson().toJson(pushData);

        // create request body
        RequestBody body = RequestBody.create(jsonData, JSON);

        // create POST request
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/carts/" + productId + ".json")
                .put(body)
                .build();

        client.newCall(request).execute();
    }

    public void addNewCart(String productId, int quantity, int selectedOption) {
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("customers/" + emailWithCurrentUser.replace(".", ",") + "/carts");

        Map<String, Integer> pushData = new HashMap<>();
        pushData.put("quantity", quantity);
        pushData.put("selectedOption", selectedOption);

        usersRef.child(productId).setValue(pushData);
    }

    public List<Cart> fetchingAllCarts() throws IOException {
        List<Cart> fetchedCarts = new ArrayList<>();
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/carts.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Map<String, Object>> fetchedCartsId = new Gson().fromJson(responseString, type);

            fetchedCartsId.forEach((key, value) -> {
                fetchedCarts.add(new Cart(
                        ProductData.fetchedProducts.get(key),
                        ((Double) value.get("quantity")).intValue(),
                        ((Double) value.get("selectedOption")).intValue()
                ));
            });

        }

        return fetchedCarts;
    }

    public void deleteCart(String id) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/carts/" + id + ".json")
                .delete()
                .build();

        client.newCall(request).execute();
    }

    public void updateAllCarts(List<Cart> carts) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Map<String, Integer>> updateData = new HashMap<>();

        carts.forEach(cart -> {

            Map<String, Integer> value = new HashMap<>();
            value.put("quantity", cart.getQuantity());
            value.put("selectedOption", cart.getSelectedOption());

            updateData.put(cart.getProduct().getId(), value);
        });

        String jsonData = new Gson().toJson(updateData);
        RequestBody body = RequestBody.create(jsonData, JSON);
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/carts.json")
                .put(body)
                .build();

        client.newCall(request).execute();
    }

    public Vender fetchingVenderById(String venderId) throws IOException {
        Vender fetchedVender = new Vender();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + venderId + ".json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> fetchedAccountData = new Gson().fromJson(responseString, type);

            fetchedVender.setAddress((String) fetchedAccountData.get("address"));
            fetchedVender.setAvatar((String) fetchedAccountData.get("avatar"));
            fetchedVender.setPhoneNumber((String) fetchedAccountData.get("phoneNumber"));

        } else {
            return null;
        }

        /* Fetch Vender Data */
        OkHttpClient client1 = new OkHttpClient();
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
            fetchedVender.setRegistrationDate((String) fetchedVenderData.get("registrationDate"));
            fetchedVender.setTotalProduct(((Double) fetchedVenderData.get("totalProduct")).intValue());

        } else {
            return null;
        }

        return fetchedVender;
    }
}
