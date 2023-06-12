package com.example.pokecenter.admin.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductReview.AdminProductReview;
import com.example.pokecenter.admin.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.AdminTab.Model.Order.OrderDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseSupportProduct {
    private Context context;

    public FirebaseSupportProduct(Context context) {
        this.context = context;
    }

    public void getProductListFromFirebase(final FirebaseCallback firebaseCallback) {
        ArrayList<AdminProduct> adminProductList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("products");

        myRef.limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
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


    public void getProductOrderDetailFromFirebase(AdminProduct product, FirebaseCallback<ArrayList<Order>> firebaseCallback) {
        ArrayList<AdminProduct> adminProductList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("orders");

        ArrayList<Order> orderList = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = new Order();
                        DataSnapshot orderDetailSnapshot = dataSnapshot.child("details");
                        try {
                            ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                            try {
                                for (DataSnapshot option : orderDetailSnapshot.getChildren()) {
                                    //If this order detail's product ID match with product,
                                    //Get product option and quantity
                                    if (option.child("productId").getValue(String.class)
                                            .equals(product.getId())) {
                                        String productId = option.child("productId").getValue(String.class);
                                        int quantity = option.child("quantity").getValue(int.class);
                                        int selectedOption = option.child("selectedOption").getValue(int.class);

                                        //Create a temp object
                                        OrderDetail orderDetail = new OrderDetail(productId, quantity, selectedOption);
                                        orderDetails.add(orderDetail);

                                        //Set OrderDetail to Order
                                        order.setDetails(orderDetails);

                                        try {
                                            order.setId(dataSnapshot.getKey());
                                            order.setCreateDate(dataSnapshot.child("createDate").getValue(String.class));
                                            order.setCustomerId(dataSnapshot.child("customerId").getValue(String.class).replace(",","."));
                                            order.setTotalAmount(dataSnapshot.child("totalAmount").getValue(int.class));
                                            order.setVenderId(dataSnapshot.child("venderId").getValue(String.class).replace(",","."));
                                        } catch (Exception e) {
                                            Log.d("FirebaseFetchUser", e.toString());
                                        }
                                        //Add Order to ArrayList<Order>
                                        orderList.add(order);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("getProductOrderHistoryFromFirebase", e.toString());
                            }

                        } catch (Exception e) {
                            Log.e("getProductOrderHistoryFromFirebase", e.toString());
                        }

                    }
                } catch (Exception e) {
                    Log.e("getProductOrderHistoryFromFirebase", e.toString());
                }

                firebaseCallback.onCallback(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getProductReviewsFromFirebase(AdminProduct adminProduct, FirebaseCallback<ArrayList<AdminProductReview>> firebaseCallback) {
        ArrayList<AdminProductReview> adminProductReviewList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("reviewsProduct");

        Query query = myRef.orderByChild("productId").equalTo(adminProduct.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.getKey();
                        String content = dataSnapshot.child("content").getValue(String.class);
                        String createDate = dataSnapshot.child("createDate").getValue(String.class);
                        String customerId = dataSnapshot.child("customerId").getValue(String.class);
                        int rate = dataSnapshot.child("rate").getValue(int.class);
                        String title = dataSnapshot.child("title").getValue(String.class);

                        AdminProductReview review = new AdminProductReview(id, content, createDate, customerId, adminProduct.getId(), rate, title);
                        adminProductReviewList.add(review);
                    }
                } catch (Exception e) {
                    Log.e("getProductReviewsFromFirebase", e.toString());
                }
                firebaseCallback.onCallback(adminProductReviewList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }
}
