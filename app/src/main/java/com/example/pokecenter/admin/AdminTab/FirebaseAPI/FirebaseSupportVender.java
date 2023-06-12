package com.example.pokecenter.admin.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseSupportVender {
    private Context context;
    private int processedCount;
    private ArrayList<User> followingList;


    public FirebaseSupportVender(Context context) {
        this.context = context;
    }

    public void getVenderDetailFromFirebase(Vender vender, final FirebaseCallback firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("venders");

        Query query = myRef.orderByKey().equalTo(vender.getEmail().replace(".", ","));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Integer followCount = dataSnapshot.child("followCount").getValue(Integer.class);
                        Integer totalCount = dataSnapshot.child("totalProduct").getValue(Integer.class);
                        String shopName = dataSnapshot.child("shopName").getValue(String.class);
                        vender.setFollowCount(followCount);
                        vender.setTotalProduct(totalCount);
                        vender.setShopName(shopName);
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

        Query query = myRef.orderByChild("venderId").equalTo(vender.getEmail().replace(".", ","));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    Long cost = 0L;
                                    if (optionSnapShot.child("cost").exists()) {
                                        cost = optionSnapShot.child("cost").getValue(Long.class);
                                    }
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

    public void getFollowingListFromFirebase(Vender vender, final FirebaseCallback<ArrayList<User>> firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference customersRef = firebaseDatabase.getReference("customers");
        followingList = new ArrayList<>();

        Query query = customersRef.orderByChild("following/" + vender.getEmail().replace(".", ",")).equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final int totalFollowing = (int) snapshot.getChildrenCount();
                    processedCount = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String customerEmail = dataSnapshot.getKey();
                        DatabaseReference accountsRef = firebaseDatabase.getReference("accounts/" + customerEmail.replace(".", ","));

                        accountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    try {
                                        User user = new User();
                                        // Check role of User
                                        int role = dataSnapshot.child("role").getValue(int.class);

                                        // Check role of User
                                        switch (role) {
                                            case 0: {
                                                user = new Customer();
                                                break;
                                            }
                                            case 1: {
                                                user = new Vender();
                                                break;
                                            }
                                        }

                                        // Generate an addressList
                                        ArrayList<Address> addressList = null;
                                        if (dataSnapshot.child("addresses").exists()) {
                                            try {
                                                DataSnapshot addressesSnapShot = dataSnapshot.child("addresses");
                                                if (addressesSnapShot.exists()) {
                                                    addressList = new ArrayList<>();
                                                    for (DataSnapshot addressSnapShot : addressesSnapShot.getChildren()) {
                                                        // Fetch attributes of an address object
                                                        String id = addressSnapShot.getKey();
                                                        String address2 = addressSnapShot.child("address2").getValue(String.class);
                                                        Boolean isDeliveryAddress = addressSnapShot.child("isDeliveryAddress").getValue(Boolean.class);
                                                        String numberStreetAddress = addressSnapShot.child("numberStreetAddress").getValue(String.class);
                                                        String receiverName = addressSnapShot.child("receiverName").getValue(String.class);
                                                        String receiverPhoneNumber = addressSnapShot.child("receiverPhoneNumber").getValue(String.class);
                                                        String type = addressSnapShot.child("type").getValue(String.class);
                                                        // Set other address properties if available
                                                        Address address = new Address(id, receiverName, receiverPhoneNumber, numberStreetAddress, address2, type, isDeliveryAddress);
                                                        addressList.add(address);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.d("FirebaseFetchUser", e.toString());
                                            }
                                        }

                                        try {
                                            user.setEmail(customerEmail.replace(",", "."));
                                            user.setAddress("");
                                            user.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
                                            user.setGender(dataSnapshot.child("gender").getValue(String.class));
                                            user.setRegistrationDate(dataSnapshot.child("registrationDate").getValue(String.class));
                                            user.setRole(role);
                                            user.setUsername(dataSnapshot.child("username").getValue(String.class));
                                            user.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue(String.class));
                                            user.setAddresses(addressList);
                                        } catch (Exception e) {
                                            Log.d("FirebaseFetchUser", e.toString());
                                        }
                                        followingList.add(user);

                                        processedCount++;
                                        if (processedCount == totalFollowing) {
                                            firebaseCallback.onCallback(followingList);
                                        }
                                    } catch (Exception e) {
                                        Log.e("", e.toString());
                                    }
                                } else {
                                    processedCount++;
                                    if (processedCount == totalFollowing) {
                                        firebaseCallback.onCallback(followingList);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                processedCount++;
                                if (processedCount == totalFollowing) {
                                    firebaseCallback.onCallback(followingList);
                                }
                                Toast.makeText(context, "Failed to fetch customer data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle case where no matching children exist
                    firebaseCallback.onCallback(followingList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET FOLLOWING LIST FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
