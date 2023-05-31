package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseFetchVender {
    private Context context;

    public FirebaseFetchVender(Context context) {
        this.context = context;
    }

    public void getVenderDetailFromFirebase(Vender vender, final FirebaseCallback firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("venders");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String email = dataSnapshot.getKey().replace(",", ".");

                        //Find the matched vender
                        if (vender.getEmail().equals(email)) {
                            Integer followCount = dataSnapshot.child("followCount").getValue(Integer.class);
                            Integer totalCount = dataSnapshot.child("totalProduct").getValue(Integer.class);
                            String shopName = dataSnapshot.child("shopName").getValue(String.class);
                            vender.setFollowCount(followCount);
                            vender.setTotalProduct(totalCount);
                            vender.setShopName(shopName);
                        }
                    }
                    firebaseCallback.onCallback(vender);

                } catch (Exception e) {
                    Log.d("FirebaseFetchVender", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET VENDER'S DETAILS FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getProductListFromFirebase(Vender vender, final FirebaseCallback firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("products");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Check if vender has this product
                    String email = dataSnapshot.child("venderId").getValue(String.class).replace(",", ".");
                    if (vender.getEmail().equals(email)) {
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
                            if (vender.getProductList() == null) {
                                vender.setProductList(new ArrayList<>());
                            }
                            vender.getProductList().add(adminProduct);
                        }
                    }
                }
                firebaseCallback.onCallback(vender);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET PRODUCT FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
