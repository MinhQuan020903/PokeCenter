package com.example.pokecenter.vender.VenderTab.Home.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.databinding.ActivityPackagedOrdersBinding;
import com.example.pokecenter.databinding.ActivityVenderFollowersBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Model.Vender.VenderFollowerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VenderFollowersActivity extends AppCompatActivity {
    private ActivityVenderFollowersBinding binding;

    private ArrayList<User> myFollowers = new ArrayList<>();
    private RecyclerView rcvFollowers;
    private VenderFollowerAdapter followerDapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenderFollowersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Customer Followers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetupData();
    }

    private void SetupData() {
        followerDapter = new VenderFollowerAdapter(myFollowers, this, R.layout.ninh_followers_item);
        rcvFollowers = binding.rcvFollowers;
        rcvFollowers.setAdapter(followerDapter);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customers");

        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                        String customerId = customerSnapshot.getKey();
                        DatabaseReference followingRef = customerSnapshot.child("following").getRef();

                        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot followingSnapshot) {
                                if (followingSnapshot.exists() && followingSnapshot.hasChild(currentUserId)) {
                                    DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("accounts").child(customerId);

                                    accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot accountSnapshot) {
                                            if (accountSnapshot.exists()) {
                                                String username = accountSnapshot.child("username").getValue(String.class);
                                                String phoneNumber = accountSnapshot.child("phoneNumber").getValue(String.class);
                                                String keyId = accountSnapshot.getKey().replace(",", ".");
                                                String registrationDate = accountSnapshot.child("registrationDate").getValue(String.class);
                                                String avatar = accountSnapshot.child("avatar").getValue(String.class);

                                                User user = new User();
                                                user.setAvatar(avatar);
                                                user.setEmail(keyId);
                                                user.setPhoneNumber(phoneNumber);
                                                user.setRegistrationDate(registrationDate);
                                                user.setUsername(username);

                                                myFollowers.add(user);
                                                binding.progressBar.setVisibility(View.INVISIBLE);

                                                // Update the adapter with the new data
                                                followerDapter.setUsersList(myFollowers);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Error occurred while retrieving the account data
                                            String errorMessage = databaseError.getMessage();
                                            // Handle the error accordingly
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Error occurred while retrieving the following data
                                String errorMessage = databaseError.getMessage();
                                // Handle the error accordingly
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error occurred while retrieving the customers data
                String errorMessage = databaseError.getMessage();
                // Handle the error accordingly
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}