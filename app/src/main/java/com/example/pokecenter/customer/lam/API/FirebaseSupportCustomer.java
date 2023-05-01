package com.example.pokecenter.customer.lam.API;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.customer.lam.CustomerTab.Profile.ProfileActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FirebaseSupportCustomer {
    private String urlDb = "https://pokecenter-ae954-default-rtdb.firebaseio.com/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String emailWithCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
        pushData.put("isDeliveryAddress", newAddress.getDeliveryAddress());

        /* convert pushData to Json string */
        String userJsonData = new Gson().toJson(pushData);

        // create request body
        RequestBody body = RequestBody.create(userJsonData, JSON);

        // create POST request
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
                removeCurrentDeliveryAddress();
            }

        }

    }

    private void removeCurrentDeliveryAddress() {

        String remoteId = "";

        /* set current Delivery Address to false locally*/
        for (Address address : MyAddressesActivity.myAddresses) {
            if (address.getDeliveryAddress() == true) {
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

        String url = urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses/" + remoteId + ".json";

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        executor.execute(() -> {
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Address> fetchingAddressesData() throws IOException {
        List<Address> fetchedAddresses = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlDb + "accounts/" + emailWithCurrentUser.replace(".", ",") + "/addresses.json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();

            Type type = new TypeToken<Map<String, Object>>(){}.getType();

            Map<String, Map<String, Object>> extractedData = new Gson().fromJson(responseString, type);



            extractedData.forEach((key, value) -> {

                fetchedAddresses.add(new Address(
                                key,
                                (String) value.get("receiverName"),
                                (String) value.get("receiverPhoneNumber"),
                                (String) value.get("numberStreetAddress"),
                                (String) value.get("address2"),
                                (String) value.get("Type"),
                                (Boolean) value.get("isDeliveryAddress")
                        )
                );
            });
        }

        return fetchedAddresses;

    }

}
