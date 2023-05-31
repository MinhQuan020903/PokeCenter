package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.OrderDetail;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Admin;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseFetchUser {

    private String email;
    private Context context;
    public FirebaseFetchUser(Context context) {
        this.context = context;
    }

    public void getUsersListFromFirebase(final FirebaseCallback firebaseCallback) {
        ArrayList<User> usersList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("accounts");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Generate an addressList
                    ArrayList<Address> addressList = null;
                    if (!dataSnapshot.child("addresses").exists()) {
                        try {
                            user = dataSnapshot.getValue(User.class);
                        } catch (Exception e) {
                            Log.d("FirebaseFetchUser", "Exception when fetching user...");
                        }

                    }
                    else {
                        //Fetch address data
                        try {
                            DataSnapshot addressesSnapShot = dataSnapshot.child("addresses");
                            if (addressesSnapShot.exists()) {
                                addressList = new ArrayList<>();
                                for (DataSnapshot addressSnapShot : addressesSnapShot.getChildren()) {
                                    //Fetch attributes of an address object
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
                            Log.d("FirebaseFetchUser", "Exception when fetching user's addresses...");
                        }

                    }

                    //Check role of User
                    int role = dataSnapshot.child("role").getValue(int.class);
                    switch (role) {
                        case 0: {
                            user = new Customer();
                            break;
                        }
                        case 1: {
                            user = new Vender();
                            break;
                        }
                        case 2: {
                            user = new Admin();
                            break;
                        }
                    }

                    try {
                        user.setAddress("");
                        user.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
                        user.setGender(dataSnapshot.child("gender").getValue(String.class));
                        user.setRegistrationDate(dataSnapshot.child("registrationDate").getValue(String.class));
                        user.setRole(role);
                        user.setUsername(dataSnapshot.child("username").getValue(String.class));
                        user.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue(String.class));
                        user.setEmail(dataSnapshot.getKey().replace(",","."));
                        user.setAddresses(addressList);
                    } catch (Exception e) {
                        Log.d("FirebaseFetchUser", "Exception when fetching user...");
                    }

                    if (user != null) {
                        usersList.add(user);
                    }
                }
                getCustomerOrderHistory(usersList, firebaseCallback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET USER FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCustomerOrderHistory(ArrayList<User> usersList, FirebaseCallback firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("orders");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = new Order();
                        //Fetch Order data
                        try {
                            DataSnapshot orderDetailsSnapShot = dataSnapshot.child("details");
                            if (orderDetailsSnapShot.exists()) {
                                ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                                for (DataSnapshot orderDetailSnapShot : orderDetailsSnapShot.getChildren()) {
                                    //Fetch attributes of an OrderDetail object
                                    String productId = orderDetailSnapShot.child("productId").getValue(String.class);
                                    int quantity = orderDetailSnapShot.child("quantity").getValue(int.class);
                                    int selectedOption = orderDetailSnapShot.child("selectedOption").getValue(int.class);

                                    //Create a temp object
                                    OrderDetail orderDetail = new OrderDetail(productId, quantity, selectedOption);
                                    orderDetails.add(orderDetail);
                                }
                                assert order != null;
                                order.setDetails(orderDetails);
                            }
                        }  catch (Exception e) {
                            Log.d("FirebaseFetchUser", "Exception when fetching user's order details...");
                        }

                    try {
                        order.setId(dataSnapshot.getKey());
                        order.setCreateDate(dataSnapshot.child("createDate").getValue(String.class));
                        order.setCustomerId(dataSnapshot.child("customerId").getValue(String.class).replace(",","."));
                        order.setTotalAmount(dataSnapshot.child("totalAmount").getValue(int.class));
                        order.setVenderId(dataSnapshot.child("venderId").getValue(String.class).replace(",","."));
                    } catch (Exception e) {
                        Log.d("FirebaseFetchUser", "Exception when fetching user's orders...");
                    }

                    for (User user : usersList) {
                        if (user instanceof Customer && user.getEmail().equals(order.getCustomerId())) {
                            if (((Customer) user).getOrderHistory() == null) {
                                ((Customer) user).setOrderHistory(new ArrayList<>());
                            }
                            ((Customer) user).getOrderHistory().add(order);
                            break;
                        }
                    }
                }
                firebaseCallback.onCallback(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET USER FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCustomerFollowers(String customerEmail, FirebaseCallback firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase
                .getReference("customers")
                .child(customerEmail)
                .child("following");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> followerList = new ArrayList<>();
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        followerList.add(dataSnapshot.getKey().replace(",","."));
                    }
                } catch (Exception e ) {
                    Log.d("FirebaseFetchUser", "Exception when fetching user's followers...");
                }

                firebaseCallback.onCallback(followerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET USER'S FOLLOWER FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
