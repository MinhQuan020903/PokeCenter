package com.example.pokecenter.admin.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminVoucher;
import com.example.pokecenter.admin.AdminTab.Model.User.Admin.Admin;
import com.example.pokecenter.admin.AdminTab.Utils.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class FirebaseSupportVoucher {
    private Context context;

    public FirebaseSupportVoucher(Context context) {
        this.context = context;
    }

    public void addNewBlockVoucher(AdminBlockVoucher blockVoucher, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        try {
            DatabaseReference ref = database.getReference("blockVoucher");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DatabaseReference blockVoucherRef = ref.push();
                    blockVoucherRef.setValue(blockVoucher)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    blockVoucher.setId(blockVoucherRef.getKey());
                                    Toast.makeText(context, "Add new BlockVoucher successfully!", Toast.LENGTH_SHORT).show();
                                    addNewVoucherFromBlock(blockVoucher, firebaseCallback);
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void addNewVoucherFromBlock(AdminBlockVoucher blockVoucher, FirebaseCallback<Boolean> firebaseCallback) {
        Random random = new Random();

        ArrayList<AdminVoucher> voucherList = new ArrayList<>(blockVoucher.getCurrentQuantity());
        HashSet<String> generatedCodes = new HashSet<>(); // Keep track of generated codes

        for (int i = 0; i < blockVoucher.getCurrentQuantity(); i++) {
            String code;
            do {
                code = blockVoucher.getName() + random.nextInt(1000000);
            } while (generatedCodes.contains(code)); // Generate a new code if it already exists

            generatedCodes.add(code); // Add the generated code to the set of generated codes

            AdminVoucher voucher = new AdminVoucher(blockVoucher.getId(), code, true);
            voucherList.add(voucher);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            DatabaseReference voucherRef = database.getReference("vouchers");

            Map<String, Object> updates = new HashMap<>();

            for (AdminVoucher voucher : voucherList) {
                DatabaseReference ref = voucherRef.push();
                updates.put(ref.getKey(), voucher);
            }

            voucherRef.updateChildren(updates)
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
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getBlockVoucherList(FirebaseCallback<ArrayList<AdminBlockVoucher>> firebaseCallback) {
        ArrayList<AdminBlockVoucher> blockVouchers = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        try {
            DatabaseReference myRef = firebaseDatabase.getReference("blockVoucher");

            Query query = myRef.orderByChild("value").limitToLast(20);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            AdminBlockVoucher blockVoucher = new AdminBlockVoucher();
                            try {
                                blockVoucher.setId(dataSnapshot.getKey());
                                blockVoucher.setName(dataSnapshot.child("name").getValue(String.class));
                                blockVoucher.setStartDate(dataSnapshot.child("startDate").getValue(String.class));
                                blockVoucher.setEndDate(dataSnapshot.child("endDate").getValue(String.class));
                                blockVoucher.setCurrentQuantity(dataSnapshot.child("currentQuantity").getValue(int.class));
                                blockVoucher.setValue(dataSnapshot.child("value").getValue(int.class));
                            } catch (Exception e) {
                                Log.d("FirebaseFetchUser", e.toString());
                            }

                            // Add to orderList
                            blockVouchers.add(blockVoucher);
                        }
                    } catch (Exception e) {
                        Log.e("getOrderListFromFirebase", e.toString());
                    }
                    // On callback
                    firebaseCallback.onCallback(blockVouchers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendVoucherForAllCustomer(AdminBlockVoucher blockVoucher, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Random random = new Random();
        HashSet<String> generatedCodes = new HashSet<>(); // Keep track of generated codes

        try {
            DatabaseReference ref = database.getReference("customers");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String code = "";
                    for (DataSnapshot customerSnapshot : snapshot.getChildren()) {

                        //Create notication node if there isn't exist one
                        if (!customerSnapshot.hasChild("notifications")) {
                            DatabaseReference newNotificationRef = ref.child(customerSnapshot.getKey()).child("notifications");
                            newNotificationRef.setValue("notifications");
                        }

                        //Check if voucher code is duplicated
                        do {
                            code = blockVoucher.getName() + random.nextInt(1000000);
                        } while (generatedCodes.contains(code));

                        generatedCodes.add(code);

                        //Create a notification for publish voucher to customer
                        String title = "You've just got a new voucher!";
                        String content = "";
                        content = "Dear " + customerSnapshot.getKey().replace(",", ".") + ",\n"
                                + "You've just received a new discount code! \n"
                                + "Discount code is " + code + ". \n"
                                + "We hope you have an enjoyable shopping experience.\n"
                                + "PokéCenter.";

                        HashMap<String, Object> notificationNode = new HashMap<>();
                        notificationNode.put("content", content);
                        notificationNode.put("read", false);
                        notificationNode.put("sentDate", DateUtils.getCurrentDateString());
                        notificationNode.put("title", title);
                        notificationNode.put("type", "fromPokeCenter");

                        DatabaseReference notificationRef = ref.child(customerSnapshot.getKey()).child("notifications").push();

                        notificationRef.setValue(notificationNode)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        firebaseCallback.onCallback(false);
                                    }
                                });

                    }

                    //Update block voucher quantity
                    updateBlockVoucherQuantity(blockVoucher, generatedCodes, firebaseCallback);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBlockVoucherQuantity(AdminBlockVoucher blockVoucher, HashSet<String> generatedCodes, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        try {
            DatabaseReference ref = database.getReference("blockVoucher");
            Query query = ref.orderByKey().equalTo(blockVoucher.getId());
            DatabaseReference blockVoucherRef = query.getRef().child(blockVoucher.getId());
            blockVoucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    HashMap<String, Object> updates = new HashMap<>();
                    int prevQuantity = 0;
                    try {
                        prevQuantity = snapshot.child("currentQuantity").getValue(int.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        updates.put("currentQuantity", prevQuantity + generatedCodes.size());
                        blockVoucher.setCurrentQuantity(prevQuantity + generatedCodes.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    blockVoucherRef.updateChildren(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            addNewVoucherFromBlockPublish(blockVoucher, generatedCodes, firebaseCallback);
                        }
                    })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                firebaseCallback.onCallback(false);
                            }
                        });
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

    public void addNewVoucherFromBlockPublish(AdminBlockVoucher blockVoucher, HashSet<String> generatedCodes, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try {
            DatabaseReference voucherRef = database.getReference("vouchers");

            ArrayList<AdminVoucher> voucherList = new ArrayList<>();
            for (String code : generatedCodes) {
                AdminVoucher voucher = new AdminVoucher(blockVoucher.getId(), code, true);
                voucherList.add(voucher);
            }

            Map<String, Object> updates = new HashMap<>();

            for (AdminVoucher voucher : voucherList) {
                DatabaseReference ref = voucherRef.push();
                updates.put(ref.getKey(), voucher);
            }

            voucherRef.updateChildren(updates)
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
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
