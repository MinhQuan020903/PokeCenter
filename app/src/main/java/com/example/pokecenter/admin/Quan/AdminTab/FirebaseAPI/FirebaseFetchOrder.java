package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.OrderDetail;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseFetchOrder {
    public Context context;

    public FirebaseFetchOrder(Context context) {
        this.context = context;
    }

    public void getOrderListFromFirebase(FirebaseCallback<ArrayList<Order>> firebaseCallback) {
        ArrayList<Order> orderList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("orders");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
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

                        //Add to orderList
                        orderList.add(order);
                    }
                } catch (Exception e) {
                    Log.e("getOrderListFromFirebase", e.toString());
                }
                //On callback
                firebaseCallback.onCallback(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
