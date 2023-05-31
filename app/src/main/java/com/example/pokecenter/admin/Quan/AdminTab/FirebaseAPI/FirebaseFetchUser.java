package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.OrderDetail;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Admin.Admin;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.purchasedProduct.PurchasedProduct;
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
                            Log.d("FirebaseFetchUser", e.toString());
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
                            Log.d("FirebaseFetchUser", e.toString());
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
                        Log.d("FirebaseFetchUser", e.toString());
                    }

                    if (user != null) {
                        usersList.add(user);
                    }
                }
                getUserOrderHistory(usersList, firebaseCallback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET USER FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserOrderHistory(ArrayList<User> usersList, FirebaseCallback firebaseCallback) {
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
                            Log.d("FirebaseFetchUser", e.toString());
                        }

                    try {
                        order.setId(dataSnapshot.getKey());
                        order.setCreateDate(dataSnapshot.child("createDate").getValue(String.class));
                        order.setCustomerId(dataSnapshot.child("customerId").getValue(String.class).replace(",","."));
                        order.setTotalAmount(dataSnapshot.child("totalAmount").getValue(int.class));
                        order.setVenderId(dataSnapshot.child("venderId").getValue(String.class).replace(",","."));
                    } catch (Exception e) {
                        Log.d("FirebaseFetchUser", e.toString());
                    }

                    //Add order history
                    for (User user : usersList) {
                        if (user instanceof Customer) {
                            if (user.getEmail().equals(order.getCustomerId())) {
                                if (((Customer) user).getOrderHistory() == null) {
                                    ((Customer) user).setOrderHistory(new ArrayList<>());
                                }
                                    ((Customer) user).getOrderHistory().add(order);
                            }
                        } else if (user instanceof Vender) {
                            if (user.getEmail().equals(order.getVenderId())) {
                                if (((Vender) user).getOrderHistory() == null) {
                                    ((Vender) user).setOrderHistory(new ArrayList<>());
                                }
                                ((Vender) user).getOrderHistory().add(order);
                            }
                        }
                    }
                }
                firebaseCallback.onCallback(usersList);

                //getCustomerActivityDetail(usersList, firebaseCallback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET USER FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCustomerActivityDetail(ArrayList<User> usersList, FirebaseCallback firebaseCallback) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("customers");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ArrayList<Cart> carts = new ArrayList<>();
                    ArrayList<String> following = new ArrayList<>();
                    ArrayList<PurchasedProduct> purchased = new ArrayList<>();
                    ArrayList<String> wishList = new ArrayList<>();

                    //Fetch carts data
                    try {
                        DataSnapshot cartsSnapshot = dataSnapshot.child("carts");
                        if (cartsSnapshot.exists()) {
                            for (DataSnapshot cartSnapshot : cartsSnapshot.getChildren()) {
                                //Fetch attributes of an Cart object
                                int quantity = cartSnapshot.child("quantity").getValue(int.class);
                                int selectedOption = cartsSnapshot.child("selectedOption").getValue(int.class);

                                //Create a temp object
                                Cart cart = new Cart(quantity, selectedOption);
                                carts.add(cart);
                            }
                        }
                    }  catch (Exception e) {
                        Log.d("FirebaseFetchUser", e.toString());
                    }

                    //Fetch following data
                    try {
                        DataSnapshot followingSnapshot = dataSnapshot.child("following");
                        if (followingSnapshot.exists()) {
                            for (DataSnapshot followSnapshot : followingSnapshot.getChildren()) {
                                //Fetch following emails
                                String email = followSnapshot.getKey().replace(",",".");
                                following.add(email);
                            }
                        }
                    }  catch (Exception e) {
                        Log.d("FirebaseFetchUser", e.toString());
                    }

                    //Fetch purchased data
                    try {
                        DataSnapshot purchasedSnapshot = dataSnapshot.child("purchased");
                        if (purchasedSnapshot.exists()) {
                            for (DataSnapshot pSnapshot : purchasedSnapshot.getChildren()) {
                                //Fetch attributes of an OrderDetail object
                                String purchasedDate = pSnapshot.child("purchasedDate").getValue(String.class);
                                Boolean reviewed = pSnapshot.child("reviewed").getValue(Boolean.class);
                                int selectedOption = pSnapshot.child("selectedOption").getValue(int.class);

                                //Create a temp object
                                PurchasedProduct purchasedProduct = new PurchasedProduct(purchasedDate, reviewed, selectedOption);
                                purchased.add(purchasedProduct);
                            }
                        }
                    }  catch (Exception e) {
                        Log.d("FirebaseFetchUser", e.toString());
                    }

                    //Fetch wishlist data
                    try {
                        DataSnapshot wishlistsSnapshot = dataSnapshot.child("wishlist");
                        if (wishlistsSnapshot.exists()) {
                            for (DataSnapshot wishlistSnapshot : wishlistsSnapshot.getChildren()) {
                                //Fetch wishlist
                                String wishlist = wishlistSnapshot.getKey();
                                wishList.add(wishlist);
                            }
                        }
                    }  catch (Exception e) {
                        Log.d("FirebaseFetchUser", e.toString());
                    }

                    //Add order history
                    for (User user : usersList) {
                        if (user instanceof Customer) {
                            if (user.getEmail().equals(dataSnapshot.getKey().replace(",","."))) {
                                ((Customer) user).setCarts(carts);
                                ((Customer) user).setFollowing(following);
                                ((Customer) user).setPurchased(purchased);
                                ((Customer) user).setWishList(wishList);
                            }
                        }
                    }
                }
                firebaseCallback.onCallback(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    Log.d("FirebaseFetchUser", e.toString());
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