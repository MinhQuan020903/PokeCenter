package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.OrderDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseFetchOrder {
    public Context context;

    public FirebaseFetchOrder(Context context) {
        this.context = context;
    }

    public void getOrderListFromFirebase(FirebaseCallback<ArrayList<Order>> firebaseCallback) {
        ArrayList<Order> orderList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("orders");

        Query query = myRef.orderByChild("totalAmount").limitToLast(50);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = new Order();
                        try {
                            order.setId(dataSnapshot.getKey());
                            order.setCreateDate(dataSnapshot.child("createDate").getValue(String.class));
                            order.setCustomerId(dataSnapshot.child("customerId").getValue(String.class).replace(",", "."));
                            order.setTotalAmount(dataSnapshot.child("totalAmount").getValue(int.class));
                            order.setVenderId(dataSnapshot.child("venderId").getValue(String.class).replace(",", "."));
                        } catch (Exception e) {
                            Log.d("FirebaseFetchUser", e.toString());
                        }
                        // Fetch Order data
                        try {
                            DataSnapshot orderDetailsSnapShot = dataSnapshot.child("details");
                            if (orderDetailsSnapShot.exists()) {
                                ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                                for (DataSnapshot orderDetailSnapShot : orderDetailsSnapShot.getChildren()) {
                                    // Fetch attributes of an OrderDetail object
                                    String productId = orderDetailSnapShot.child("productId").getValue(String.class);
                                    int quantity = orderDetailSnapShot.child("quantity").getValue(int.class);
                                    int selectedOption = orderDetailSnapShot.child("selectedOption").getValue(int.class);

                                    OrderDetail orderDetail = new OrderDetail(productId, quantity, selectedOption);
                                    orderDetails.add(orderDetail);
                                }
                                order.setDetails(orderDetails);
                            }
                        } catch (Exception e) {
                            Log.d("FirebaseFetchUser", e.toString());
                        }

                        // Add to orderList
                        orderList.add(order);
                    }
                } catch (Exception e) {
                    Log.e("getOrderListFromFirebase", e.toString());
                }
                // On callback
                firebaseCallback.onCallback(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


    public void getOrderDetailFromFirebase(Order order, FirebaseCallback<ArrayList<OrderDetail>> firebaseCallback) {
        ArrayList<OrderDetail> orderDetailList = order.getDetails();
        ArrayList<OrderDetail> updatedOrderDetailList = new ArrayList<>();
        int totalOrderDetails = orderDetailList.size();
        AtomicInteger fetchedOrderDetails = new AtomicInteger(0);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        for (OrderDetail orderDetail : orderDetailList) {
            DatabaseReference myRef = firebaseDatabase.getReference("products");
            Query query = myRef.orderByKey().equalTo(orderDetail.getProductId());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String productId = dataSnapshot.getKey();
                            String productName = dataSnapshot.child("name").getValue(String.class);
                            DataSnapshot optionsSnapshot = dataSnapshot.child("options");
                            int selectedOption = orderDetail.getSelectedOption();
                            int index = 0;
                            for (DataSnapshot detailSnapshot : optionsSnapshot.getChildren()) {
                                if (index == selectedOption) {
                                    String selectedOptionName = detailSnapshot.getKey();
                                    String selectedOptionImage = detailSnapshot.child("optionImage").getValue(String.class);
                                    if (selectedOptionImage.equals("")) {
                                        Log.e("IMAGENULL", selectedOptionImage);
                                        // Get the first image of the product
                                        selectedOptionImage = dataSnapshot.child("images").child("0").getValue(String.class);
                                        Log.e("IMAGENULL", selectedOptionImage);
                                    }
                                    Long selectedOptionPrice = detailSnapshot.child("price").getValue(Long.class);
                                    OrderDetail updatedOrderDetail = new OrderDetail(selectedOptionImage, productName, productId, orderDetail.getQuantity(), selectedOption, selectedOptionName, selectedOptionPrice);
                                    updatedOrderDetailList.add(updatedOrderDetail);
                                    break;
                                }
                                index++;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("getOrderDetailFromFirebase", e.toString());
                    }

                    int fetchedCount = fetchedOrderDetails.incrementAndGet();

                    if (fetchedCount == totalOrderDetails) {
                        firebaseCallback.onCallback(updatedOrderDetailList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("getOrderDetailFromFirebase", "DatabaseError: " + error.getMessage());

                    int fetchedCount = fetchedOrderDetails.incrementAndGet();

                    if (fetchedCount == totalOrderDetails) {
                        firebaseCallback.onCallback(updatedOrderDetailList);
                    }
                }
            });
        }
    }



}
