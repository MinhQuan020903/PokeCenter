package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = new User();
                    if (!dataSnapshot.child("addresses").exists()) {
                        user = dataSnapshot.getValue(User.class);
                    }
                    else {
                        //Fetch address data
                        DataSnapshot addressesSnapShot = dataSnapshot.child("addresses");
                        if (addressesSnapShot.exists()) {
                            ArrayList<Address> addressList = new ArrayList<>();
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
                            assert user != null;
                            user.setAddresses(addressList);
                        }
                    }

                    user.setAddress("");
                    user.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
                    user.setGender(dataSnapshot.child("gender").getValue(String.class));
                    user.setRegistrationDate(dataSnapshot.child("registrationDate").getValue(String.class));
                    user.setRole(dataSnapshot.child("role").getValue(int.class));
                    user.setUsername(dataSnapshot.child("username").getValue(String.class));
                    user.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue(String.class));
                    user.setEmail(dataSnapshot.getKey().replace(",","."));
                    usersList.add(user);
                }
                firebaseCallback.onCallback(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "GET USER FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
