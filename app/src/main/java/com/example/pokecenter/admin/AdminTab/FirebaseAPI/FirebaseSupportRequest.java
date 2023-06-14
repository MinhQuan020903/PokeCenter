package com.example.pokecenter.admin.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequest;
import com.example.pokecenter.admin.AdminTab.Utils.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseSupportRequest {
    private Context context;
    private ArrayList<AdminRequest> requestList;

    public FirebaseSupportRequest(Context context) {
        this.context = context;
    }

    public void getRequestList(FirebaseCallback<ArrayList<AdminRequest>> firebaseCallback) {
        requestList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        try {
            DatabaseReference myRef = firebaseDatabase.getReference("findingProductSupport");

            Query query = myRef.orderByChild("createDate").limitToLast(50); // Query to limit to 20 most recent users

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            AdminRequest request = requestSnapshot.getValue(AdminRequest.class);
                            request.setId(requestSnapshot.getKey());
                            requestList.add(request);
                        }
                    } catch (Exception e) {
                        Log.e("getRequestList", e.toString());
                    }

                    firebaseCallback.onCallback(requestList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pushResponse(AdminRequest request, Boolean isApproved, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            DatabaseReference ref = database.getReference("findingProductSupport");

            Query query = ref.orderByKey().equalTo(request.getId());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DatabaseReference nodeRef = snapshot.getRef();

                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put("status", isApproved ? "Resolved" : "Not resolved");

                        // Update the node with the new response and status
                        nodeRef.updateChildren(updates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Update successful
                                            pushNotificationForRequestResponse(request, isApproved, firebaseCallback);
                                        } else {
                                            // Update failed
                                            firebaseCallback.onCallback(false);
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                    firebaseCallback.onCallback(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pushNotificationForRequestResponse(AdminRequest request, Boolean isApproved, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        try {
            DatabaseReference ref = database.getReference("customers").child(request.getCustomerId().replace(".",",")).child("notifications");
            String title = "PokéCenter's Response for Customer Product Request";
            String content = "";
            if (isApproved) {
                content = "Dear " + request.getCustomerId().replace(",", ".") +  ", \n"
                        + "We are pleased to inform you that your product request has been approved!"
                        + " We appreciate your interest in our store and your choice of products."
                        + " Our team has carefully reviewed your request, and we are happy to fulfill it.\n"
                        + "Thank you for your patronage.\n"
                        +"PokéCenter.";
            } else {
                content = "Dear " + request.getCustomerId().replace(",", ".") + ",\n"
                        + "We appreciate your interest in our store and your choice of products. "
                        + "But unfortunately, we cannot approve your request.\n"
                        + "If you have any further inquiries or need assistance, please feel free to contact us.\n"
                        + "Thank you for your understanding.\n"
                        + "PokéCenter.";
            }

            HashMap<String, Object> notificationNode = new HashMap<>();
            notificationNode.put("content", content);
            notificationNode.put("read", false);
            notificationNode.put("sentDate", DateUtils.getCurrentDateString());
            notificationNode.put("title", title);
            notificationNode.put("type", "fromPokeCenter");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        DatabaseReference notificationRef = ref.push();
                        notificationRef.setValue(notificationNode)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //If resolved, sent notifications for venders
                                        if (isApproved) {
                                            pushRequestNotificationForVenders(request, firebaseCallback);
                                        } else {
                                            firebaseCallback.onCallback(true);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        firebaseCallback.onCallback(false);
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    firebaseCallback.onCallback(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pushRequestNotificationForVenders(AdminRequest request, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            DatabaseReference ref = database.getReference("venders");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                        try {
                            //Create notication node if there isn't exist one
                            if (!notificationSnapshot.hasChild("notifications")) {
                                DatabaseReference newNotificationRef = ref.child(notificationSnapshot.getKey()).child("notifications");
                                newNotificationRef.setValue("notifications");
                            }


                            //Set details of notification
                            String title = "PokéCenter's Response for Customer Product Request";
                            String content = " Dear " + notificationSnapshot.getKey().replace(",", ".") +  ", \n"
                                    + "Recently, we've received a new product request from customers!"
                                    + " you have the resource for this product, please contact admin for further details."
                                    + "Thank you for your support.\n"
                                    +"PokéCenter.";


                            HashMap<String, Object> notificationNode = new HashMap<>();
                            notificationNode.put("content", content);
                            notificationNode.put("read", false);
                            notificationNode.put("sentDate", DateUtils.getCurrentDateString());
                            notificationNode.put("title", title);
                            notificationNode.put("type", "fromPokeCenter");

                            DatabaseReference notificationRef = ref.child(notificationSnapshot.getKey()).child("notifications").push();

                            notificationRef.setValue(notificationNode)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            firebaseCallback.onCallback(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            firebaseCallback.onCallback(false);
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    firebaseCallback.onCallback(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
