package com.example.pokecenter.admin.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminVoucher;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class FirebaseFetchVoucher {
    private Context context;

    public FirebaseFetchVoucher(Context context) {
        this.context = context;
    }

    public void addNewBlockVoucher(AdminBlockVoucher blockVoucher, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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

    }

    public void addNewVoucherFromBlock(AdminBlockVoucher blockVoucher, FirebaseCallback<Boolean> firebaseCallback) {

        Random random = new Random();

        ArrayList<AdminVoucher> voucherList = new ArrayList<>(blockVoucher.getCurrentQuantity());
        HashSet<String> generatedCodes = new HashSet<>(); // Keep track of generated codes

        for (int i = 0; i < blockVoucher.getCurrentQuantity(); i++) {
            String code;
            do {
                code = blockVoucher.getName() + random.nextInt(100000000);
            } while (generatedCodes.contains(code)); // Generate a new code if it already exists

            generatedCodes.add(code); // Add the generated code to the set of generated codes

            AdminVoucher voucher = new AdminVoucher(blockVoucher.getId(), code, true);
            voucherList.add(voucher);
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference voucherRef = database.getReference("vouchers");

        CompletableFuture<Void> pushVouchersFuture = new CompletableFuture<>();

        for (AdminVoucher voucher : voucherList) {
            DatabaseReference ref = voucherRef.push();
            ref.setValue(voucher)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }
        );
        }

        pushVouchersFuture.join();
        firebaseCallback.onCallback(true);
    }

    public void getBlockVoucherList(FirebaseCallback<ArrayList<AdminBlockVoucher>> firebaseCallback) {
        ArrayList<AdminBlockVoucher> blockVouchers = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
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
    }
}
