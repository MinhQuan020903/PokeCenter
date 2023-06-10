package com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminRequest.AdminRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseFetchRequest {
    private Context context;
    private ArrayList<AdminRequest> requestList;

    public FirebaseFetchRequest(Context context) {
        this.context = context;
    }

    public void getRequestList(FirebaseCallback<ArrayList<AdminRequest>> firebaseCallback) {
        requestList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("findingProductSupport");

        Query query = myRef.orderByChild("createDate").limitToLast(20); // Query to limit to 20 most recent users

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
    }

    public void pushResponse(String id, String response, Boolean isApproved, FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("findingProductSupport");

        Query query = ref.orderByChild(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference nodeRef = snapshot.getRef();

                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("response", response);
                    updates.put("status", isApproved ? "Resolved" : "Not resolved");

                    // Update the node with the new response and status
                    nodeRef.updateChildren(updates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Update successful
                                        firebaseCallback.onCallback(true);
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
    }

}
