package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseFetchVender {
    private Context context;

    public FirebaseFetchVender(Context context) {
        this.context = context;
    }

    public void getVenderDetailFromFirebase(Vender vender, final FirebaseCallback firebaseCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("venders");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String email = dataSnapshot.getKey().replace(",", ".");

                        //Find the matched vender
                        if (vender.getEmail().equals(email)) {
                            Integer followCount = dataSnapshot.child("followCount").getValue(Integer.class);
                            Integer totalCount = dataSnapshot.child("totalProduct").getValue(Integer.class);
                            String shopName = dataSnapshot.child("shopName").getValue(String.class);
                            vender.setFollowCount(followCount);
                            vender.setTotalProduct(totalCount);
                            vender.setShopName(shopName);
                        }
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
}
