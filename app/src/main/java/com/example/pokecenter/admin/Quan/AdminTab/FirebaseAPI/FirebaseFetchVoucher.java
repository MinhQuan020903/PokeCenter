package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucher;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminBlockVoucher.AdminVoucher;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class FirebaseFetchVoucher {
    private Context context;
    private String blockVoucherKey;

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
                blockVoucherKey = ref.getKey();
                blockVoucherRef.setValue(blockVoucher)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                addNewVoucherFromBlock(blockVoucherKey, blockVoucher, firebaseCallback);
                            }
                        });
                addNewVoucherFromBlock(blockVoucherKey, blockVoucher, firebaseCallback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addNewVoucherFromBlock(String blockVoucherKey, AdminBlockVoucher blockVoucher, FirebaseCallback<Boolean> firebaseCallback) {


        Random random = new Random();

        ArrayList<AdminVoucher> voucherList = new ArrayList<>(blockVoucher.getCurrentQuantity());
        for (int i = 0; i < blockVoucher.getCurrentQuantity(); i++)  {
            AdminVoucher voucher = new AdminVoucher(
                    blockVoucherKey,
                    blockVoucher.getName() + random.nextInt(100000000),
                    true
            );
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
    }
}
