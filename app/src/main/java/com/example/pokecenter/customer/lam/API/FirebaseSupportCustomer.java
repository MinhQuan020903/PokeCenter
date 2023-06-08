package com.example.pokecenter.customer.lam.API;

import android.net.Uri;

import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.checkout_item.CheckoutItem;
import com.example.pokecenter.customer.lam.Model.notification.Notification;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.order.DetailOrder;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.purchasedProduct.PurchasedProduct;
import com.example.pokecenter.customer.lam.Model.review_product.ReviewProduct;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.customer.lam.Model.voucher.VoucherInfo;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
                            optionKey,
                            (String) optionValue.get("optionImage"),
                            ((Double) optionValue.get("currentQuantity")).intValue(),
                            ((Double) optionValue.get("inputQuantity")).intValue(),
                            ((Double) optionValue.get("price")).intValue()
                    ));
                });

                // List<Option> sortedOptions = options.stream().sorted(Comparator.comparing(Option::getPrice)).collect(Collectors.toList());

                ProductData.fetchedProducts.put(key, new Product(
                        key,
                        (String) value.get("name"),
                        (String) value.get("desc"),
                        (List<String>) value.get("images"),
                        options,
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

        fetchedVender.setVenderId(venderId);

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
            fetchedVender.setBackground((String) fetchedAccountData.get("background"));
            fetchedVender.setRegistrationDate((String) fetchedAccountData.get("registrationDate"));

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
            fetchedVender.setTotalProduct(((Double) fetchedVenderData.get("totalProduct")).intValue());

        } else {
            return null;
        }

        return fetchedVender;
    }

    public void fetchingFollowData() throws IOException {
        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/following.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return;
            }

            Type type = new TypeToken<Map<String, Boolean>>(){}.getType();
            FollowData.fetchedFollowing = new Gson().fromJson(responseString, type);
        }
    }

    public void addFollowing(String venderId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, Boolean> pushData = new HashMap<>();
        pushData.put(venderId, true);

        String jsonData = new Gson().toJson(pushData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/following.json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public void deleteFollowing(String venderId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/following/" + venderId + ".json")
                .delete()
                .build();

        client.newCall(request).execute();
    }

    public void updateFollowCount(String venderId, int followCount) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, Integer> pushData = new HashMap<>();
        pushData.put("followCount", followCount);

        String jsonData = new Gson().toJson(pushData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "venders/" + venderId + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public Map<String, Boolean> fetchingWishList() throws IOException {
        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/wishList.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new HashMap<>();
            }

            Type type = new TypeToken<Map<String, Boolean>>(){}.getType();
            return new Gson().fromJson(responseString, type);
        }

        return new HashMap<>();
    }

    public void addWishListItem(String productId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, Boolean> pushData = new HashMap<>();
        pushData.put(productId, true);

        String jsonData = new Gson().toJson(pushData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/wishList.json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public void removeWishListItem(String productId) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/wishList/" + productId + ".json")
                .delete()
                .build();

        client.newCall(request).execute();

    }

    public List<ReviewProduct> fetchingReviewsForProductId(String productId) throws IOException {

        List<ReviewProduct> fetchedReviewsProduct = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the Firebase Realtime Database endpoint
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pokecenter-ae954-default-rtdb.firebaseio.com/reviewsProduct.json").newBuilder();

        urlBuilder.addQueryParameter("orderBy", "\"productId\"")
                .addQueryParameter("equalTo", "\"" + productId + "\"");

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

            Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> fetchedData = new Gson().fromJson(responseString, type);

            OkHttpClient clientCustomer = new OkHttpClient();

            fetchedData.forEach((key, value) -> {

                ReviewProduct review = new ReviewProduct(
                        key,
                        (String) value.get("title"),
                        (String) value.get("content"),
                        ((Double) value.get("rate")).intValue(),
                        "customer",
                        "https://static.wikia.nocookie.net/pokemon-fano/images/6/6f/Poke_Ball.png/revision/latest?cb=20140520015336",
                        (String) value.get("createDate")
                );

                String customerId = (String) value.get("customerId");

                String urlCustomerName = urlDb + "/accounts/" + customerId + "/username.json";
                Request requestCustomerName = new Request.Builder()
                        .url(urlCustomerName)
                        .build();


                try {
                    Response responseCustomerName = clientCustomer.newCall(requestCustomerName).execute();


                    if (responseCustomerName.isSuccessful()) {
                        String customerName = responseCustomerName.body().string();
                        review.setCustomerName(new Gson().fromJson(customerName, new TypeToken<String>(){}.getType()));
                    }

                } catch (IOException e) {

                }

                String urlCustomerAvatar = urlDb + "/accounts/" + customerId + "/avatar.json";
                Request requestCustomerAvatar = new Request.Builder()
                        .url(urlCustomerAvatar)
                        .build();

                try {
                    Response responseCustomerAvatar = clientCustomer.newCall(requestCustomerAvatar).execute();

                    if (responseCustomerAvatar.isSuccessful()) {
                        String customerAvatar = responseCustomerAvatar.body().string();
                        review.setCustomerImage(new Gson().fromJson(customerAvatar, new TypeToken<String>(){}.getType()));
                    }

                } catch (IOException e) {

                }

                fetchedReviewsProduct.add(review);
            });

        }

        return  fetchedReviewsProduct;
    }

    public List<Notification> fetchingAllNotifications() throws IOException {

        List<Notification> fetchedNotifications = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/notifications.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> fetchedData = new Gson().fromJson(responseString, type);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            fetchedData.forEach((key, value) -> {
                try {
                    fetchedNotifications.add(new Notification(
                        key,
                        (String) value.get("title"),
                        (String) value.get("content"),
                        dateFormat.parse((String) value.get("sentDate")),
                        (String) value.get("type"),
                        (Boolean) value.get("read")
                    ));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

        }

        Collections.reverse(fetchedNotifications);

        return fetchedNotifications;
    }

    public void changeStatusNotification(String notificationId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, Boolean> updateData = new HashMap<>();
        updateData.put("read", true);

        String jsonData = new Gson().toJson(updateData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/notifications/" + notificationId + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public VoucherInfo fetchingVoucherInfo(String voucherCode) throws IOException {
        VoucherInfo voucherInfo = new VoucherInfo();

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pokecenter-ae954-default-rtdb.firebaseio.com/vouchers.json").newBuilder();

        urlBuilder.addQueryParameter("orderBy", "\"code\"")
                .addQueryParameter("equalTo", "\"" + voucherCode + "\"");

        String url = urlBuilder.build().toString();

        // Create an HTTP GET request
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseString = response.body().string();

            if (responseString.equals("{}")) {
                return null;
            }

            Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> fetchedData = new Gson().fromJson(responseString, type);

            AtomicReference<String> blockVoucherId = new AtomicReference<>("");
            fetchedData.forEach((key, value) -> {
                voucherInfo.setKey(key);
                voucherInfo.setStatus((Boolean) value.get("status"));
                blockVoucherId.set((String) value.get("blockVoucherId"));
            });

            OkHttpClient client1 = new OkHttpClient();

            // Create an HTTP GET request
            Request request1 = new Request.Builder()
                    .url(urlDb + "blockVoucher/" + blockVoucherId.get() + ".json")
                    .build();

            Response response1 = client1.newCall(request1).execute();

            if (response1.isSuccessful()) {
                String responseString1 = response1.body().string();

                Type type1 = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> fetchedData1 = new Gson().fromJson(responseString1, type1);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                Date startDate = null;
                try {
                    startDate = dateFormat.parse((String) fetchedData1.get("startDate"));
                } catch (ParseException e) {

                }
                voucherInfo.setStartDate(startDate);

                Date endDate = null;
                try {
                    endDate = dateFormat.parse((String) fetchedData1.get("endDate"));
                } catch (ParseException e) {

                }
                voucherInfo.setEndDate(endDate);
                voucherInfo.setValue(((Double) fetchedData1.get("value")).intValue());
            }
        }

        return voucherInfo;
    }

    public void disableVoucher(String key) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Map<String, Boolean> updateData = new HashMap<>();
        updateData.put("status", false);

        String jsonData = new Gson().toJson(updateData);

        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "vouchers/" + key + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
    public boolean saveOrders(List<CheckoutItem> checkoutItemList) {

        AtomicBoolean isSuccess = new AtomicBoolean(true);

        Map<String, Boolean> vendersId = new HashMap<>();
        Map<String, Map<String, Object>> purchasedProducts = new HashMap<>();
        checkoutItemList.forEach(item -> {

            vendersId.put(item.getVenderId(), true);

            Map<String, Object> value = new HashMap<>();
            value.put("reviewed", false);
            value.put("selectedOption", item.getSelectedOption());
            value.put("purchasedDate", outputFormat.format(new Date()));
            purchasedProducts.put(item.getProductId(), value);

        });

        vendersId.keySet().forEach(key -> {

            int totalAmount = 0;
            List<Map<String, Object>> filterList = new ArrayList<>();
            for (int i = 0; i < checkoutItemList.size(); ++i) {
                if (checkoutItemList.get(i).getVenderId().equals(key)) {

                    CheckoutItem item = checkoutItemList.get(i);

                    Map<String, Object> orderDetail = new HashMap<>();
                    orderDetail.put("productId", item.getProductId());
                    orderDetail.put("selectedOption", item.getSelectedOption());
                    orderDetail.put("quantity", item.getQuantity());

                    filterList.add(orderDetail);
                    totalAmount += checkoutItemList.get(i).getPrice() * checkoutItemList.get(i).getQuantity();
                }
            }

            OkHttpClient client = new OkHttpClient();

            Map<String, Object> postData = new HashMap<>();


            postData.put("createDate", outputFormat.format(new Date()));
            postData.put("totalAmount", totalAmount);

            String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            postData.put("customerId", emailWithCurrentUser.replace(".", ","));
            postData.put("venderId", key);
            postData.put("details", filterList);
            postData.put("status", "Order placed");
            postData.put("deliveryDate", "");

            String jsonData = new Gson().toJson(postData);

            RequestBody body = RequestBody.create(jsonData, JSON);

            Request request = new Request.Builder()
                    .url(urlDb + "orders.json")
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                isSuccess.set(false);
                return;
            }

            // Update Product property
            filterList.forEach(item -> {
                String productId = (String) item.get("productId");
                int selectedOption = (int) item.get("selectedOption");
                int quantity = (int) item.get("quantity");

                Option option = ProductData.fetchedProducts.get(productId).getOptions().get(selectedOption);
                option.decreaseCurrentQuantity(quantity);

                Map<String, Integer> patchData = new HashMap<>();
                patchData.put("currentQuantity", option.getCurrentQuantity());

                String jsonPatchData = new Gson().toJson(patchData);
                RequestBody patchBody = RequestBody.create(jsonPatchData, JSON);

                Request patchRequest = new Request.Builder()
                        .url(urlDb + "products/" + productId + "/options/" + option.getOptionName() + ".json")
                        .patch(patchBody)
                        .build();

                try {
                    client.newCall(patchRequest).execute();
                } catch (IOException e) {
                    isSuccess.set(false);
                    return;
                }
            });

        });

        try {
            updateListProductReviews(purchasedProducts);
        } catch (IOException e) {

        }

        return isSuccess.get();
    }

    public void updateListProductReviews(Map<String, Map<String, Object>> productsId) throws IOException {

        OkHttpClient client = new OkHttpClient();
        String jsonData = new Gson().toJson(productsId);
        RequestBody body = RequestBody.create(jsonData, JSON);

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/purchased.json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }


    public List<Order> fetchingOrdersData() throws IOException {

        List<Order> fetchedOrders = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the Firebase Realtime Database endpoint
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pokecenter-ae954-default-rtdb.firebaseio.com/orders.json").newBuilder();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        urlBuilder.addQueryParameter("orderBy", "\"customerId\"")
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

            Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> fetchedData = new Gson().fromJson(responseString, type);

            fetchedData.forEach((key, value) -> {

                List<Map<String, Object>> detailOrderData = (List<Map<String, Object>>) value.get("details");

                List<DetailOrder> details = new ArrayList<>();
                detailOrderData.forEach(detailOrder -> {
                    details.add(new DetailOrder(
                            (String) detailOrder.get("productId"),
                            ((Double) detailOrder.get("selectedOption")).intValue(),
                            ((Double) detailOrder.get("quantity")).intValue()
                    ));
                });

                Order order = null;
                try {
                    order = new Order(
                            ((Double) value.get("totalAmount")).intValue(),
                            outputFormat.parse((String) value.get("createDate")),
                            details,
                            (String) value.get("status")
                    );
                } catch (ParseException e) {

                }


                String stringDeliveryDate = (String) value.get("deliveryDate");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if (stringDeliveryDate.isEmpty()) {

                } else {

                    try {
                        order.setDeliveryDate(dateFormat.parse(stringDeliveryDate));
                    } catch (ParseException e) {

                    }

                }

                fetchedOrders.add(order);

            });
        }

        fetchedOrders.sort(Comparator.comparing(Order::getCreateDateTime).reversed());
        return fetchedOrders;

    }

    public List<PurchasedProduct> fetchingPurchasedProducts() throws IOException {

        List<PurchasedProduct> fetchedPurchasedProducts = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Request request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/purchased.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            if (responseString.equals("null")) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
            Map<String, Map<String, Object>> fetchedData = new Gson().fromJson(responseString, type);

            fetchedData.forEach((key, value) -> {

                fetchedPurchasedProducts.add(new PurchasedProduct(
                        key,
                        (Boolean) value.get("reviewed"),
                        (String) value.get("purchasedDate"),
                        ((Double) value.get("selectedOption")).intValue()
                ));

            });
        }

        return  fetchedPurchasedProducts;
    }

    public void addProductReview(String productId, String title, String content, int rate) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> postData = new HashMap<>();
        postData.put("productId", productId);
        postData.put("title", title);
        postData.put("content", content);
        postData.put("rate", rate);
        postData.put("customerId", emailWithCurrentUser.replace(".", ","));
        postData.put("createDate", outputFormat.format(new Date()));

        String jsonData = new Gson().toJson(postData);
        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "reviewsProduct.json")
                .post(body)
                .build();

        client.newCall(request).execute();

        // Update reviewed -> true
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("reviewed", true);

        jsonData = new Gson().toJson(updateData);
        body = RequestBody.create(jsonData, JSON);

        request = new Request.Builder()
                .url(urlDb + "customers/" + emailWithCurrentUser.replace(".", ",") + "/purchased/" + productId + ".json")
                .patch(body)
                .build();

        client.newCall(request).execute();
    }

    public String addNewSupportTicket(String problemName, String desc, String contactMethod) throws IOException {

        OkHttpClient client = new OkHttpClient();
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> postData = new HashMap<>();
        postData.put("customerId", emailWithCurrentUser.replace(".", ","));
        postData.put("problemName", problemName);
        postData.put("desc", desc);
        postData.put("contactMethod", contactMethod);
        postData.put("createDate", outputFormat.format(new Date()));
        postData.put("status", "Not resolved");

        String jsonData = new Gson().toJson(postData);
        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "supportTickets.json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseBody = response.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Map<String, Object>> responseData = new Gson().fromJson(responseBody, type);

            String id = String.valueOf(responseData.get("name"));
            return id;
        }

        return "";
    }

    public String addNewFindingProductSupport(String name, String desc, List<String> additionalImages) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Map<String, Object> postData = new HashMap<>();
        postData.put("customerId", emailWithCurrentUser.replace(".", ","));
        postData.put("name", name);
        postData.put("desc", desc);
        postData.put("additionalImages", additionalImages);
        postData.put("createDate", outputFormat.format(new Date()));
        postData.put("status", "Not resolved");

        String jsonData = new Gson().toJson(postData);
        RequestBody body = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(urlDb + "findingProductSupport.json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseBody = response.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Map<String, Object>> responseData = new Gson().fromJson(responseBody, type);

            String id = String.valueOf(responseData.get("name"));
            return id;
        }

        return "";
    }

    public Map<String, List<String>> fetchingAllCategory() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "category.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {

            String responseBody = response.body().string();

            if (responseBody.equals("null")) {
                return null;
            }

            Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
            Map<String, List<String>> responseData = new Gson().fromJson(responseBody, type);

            return responseData;
        }
        return null;
    }

}
