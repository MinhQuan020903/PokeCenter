package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseFetchProduct {
    private Context context;

    public FirebaseFetchProduct(Context context) {
        this.context = context;
    }

    public void getProductListFromFirebase(final FirebaseCallback firebaseCallback) {
        ArrayList<AdminProduct> adminProductList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("products");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    AdminProduct adminProduct = new AdminProduct();
                    //Fetch ArrayList<AdminOption> options
                    ArrayList<AdminOption> options = null;
                    DataSnapshot optionsSnapShot = dataSnapshot.child("options");
                    if (optionsSnapShot.exists()) {
                        try {
                            options = new ArrayList<>();
                            for (DataSnapshot optionSnapShot : optionsSnapShot.getChildren()) {
                                //Fetch attributes of an AdminOption object
                                String id = optionSnapShot.getKey();
                                Long cost = optionSnapShot.child("cost").getValue(Long.class);
                                int currentQuantity = optionSnapShot.child("currentQuantity").getValue(int.class);
                                int inputQuantity = optionSnapShot.child("inputQuantity").getValue(int.class);
                                String optionImage = optionSnapShot.child("optionImage").getValue(String.class);
                                Long price = optionSnapShot.child("price").getValue(Long.class);
                                // Set other address properties if available
                                AdminOption adminOption = new AdminOption(id, cost, currentQuantity, inputQuantity, optionImage, price);
                                options.add(adminOption);
                            }
                        } catch (Exception e) {
                                Log.d("FirebaseFetchProduct", e.toString());
                        }

                    }

                    //Fetch ArrayList<String> images
                    ArrayList<String> images = null;
                    DataSnapshot imagesSnapShot = dataSnapshot.child("images");
                    if (imagesSnapShot.exists()) {
                        try {
                            images = new ArrayList<>();
                            Integer index = 0;
                            for (DataSnapshot imageSnapShot : imagesSnapShot.getChildren()) {
                                //Fetch attributes of images
                                String res = imageSnapShot.getValue(String.class);
                                images.add(res);
                                index++;
                            }
                        } catch (Exception e) {
                            Log.d("FirebaseFetchProduct", e.toString());
                        }

                    }

                    try {
                        //Add attributes to adminProduct object
                        adminProduct.setId(dataSnapshot.getKey());
                        adminProduct.setDesc(dataSnapshot.child("desc").getValue(String.class));
                        adminProduct.setName(dataSnapshot.child("name").getValue(String.class));
                        adminProduct.setVenderId(dataSnapshot.child("venderId").getValue(String.class).replace(",", "."));
                        adminProduct.setImages(images);
                        adminProduct.setOptions(options);
                    } catch (Exception e) {
                        Log.d("FirebaseFetchProduct", e.toString());
                    }

                    //Add adminProduct to adminProductList
                    if (adminProduct != null) {
                        adminProductList.add(adminProduct);
                    }
                }
                firebaseCallback.onCallback(adminProductList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET PRODUCT FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getProductOrderHistoryFromFirebase(AdminProduct product, FirebaseCallback<HashMap<Integer, Integer>> firebaseCallback) {
        ArrayList<AdminProduct> adminProductList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("orders");

        HashMap<Integer, Integer> selectedOptionAndQuantity = new HashMap<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DataSnapshot orderDetailSnapshot = dataSnapshot.child("details");
                        try {
                            for (DataSnapshot option : orderDetailSnapshot.getChildren()) {
                                //If this order detail's product ID match with product,
                                //Get product option and quantity
                                if (option.child("productId").getValue(String.class)
                                        .equals(product.getId())) {
                                    Integer selectedOption = option.child("selectedOption").getValue(Integer.class);
                                    Integer quantity = option.child("quantity").getValue(Integer.class);
                                    selectedOptionAndQuantity.merge(selectedOption, quantity, Integer::sum);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("getProductOrderHistoryFromFirebase", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("getProductOrderHistoryFromFirebase", e.toString());
                }
                firebaseCallback.onCallback(selectedOptionAndQuantity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
