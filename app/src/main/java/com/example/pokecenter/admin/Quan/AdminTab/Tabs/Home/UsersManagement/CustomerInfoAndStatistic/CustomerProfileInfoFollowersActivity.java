package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.CustomerInfoAndStatistic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchUser;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.CustomerAddressesAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.UserAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.ActivityCustomerProfileInfoAddressesBinding;
import com.example.pokecenter.databinding.ActivityCustomerProfileInfoFollowersBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomerProfileInfoFollowersActivity extends AppCompatActivity {
    private ActivityCustomerProfileInfoFollowersBinding binding;
    private Customer customer;
    private UserAdapter userAdapter;
    private ArrayList<User> followerList;
    private ArrayList<String> followerEmailList;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ///INIT NEW ARRAYLIST!!!
        binding = ActivityCustomerProfileInfoFollowersBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Customer's Followers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Get Customer object
        Intent intent = getIntent();
        customer = (Customer) intent.getSerializableExtra("Customer");

        //Set no follower by default
        binding.llCustomerNoFollowers.setVisibility(View.INVISIBLE);

        //Get ArrayList<String> followerEmailList from Firebase
        String customerFirebaseEmail = customer.getEmail().replace(".", ",");
        FirebaseFetchUser firebaseFetchUser = new FirebaseFetchUser(this);
        firebaseFetchUser.getCustomerFollowers(customerFirebaseEmail, new FirebaseCallback<ArrayList<String>>() {
            @Override
            public void onCallback(ArrayList<String> user) {
                followerEmailList = user;
                if (followerEmailList.size() == 0 || followerEmailList == null) {
                    binding.rvCustomerFollowers.setVisibility(View.INVISIBLE);
                    binding.llCustomerNoFollowers.setVisibility(View.VISIBLE);
                } else {
                    followerList = new ArrayList<>();
                    firebaseFetchUser.getUsersListFromFirebase(new FirebaseCallback<ArrayList<User>>() {
                        @Override
                        public void onCallback(ArrayList<User> user) {
                            int index = 0;
                            while (index < followerEmailList.size()) {
                                for (User u : user) {
                                    if (followerEmailList.get(index).equals(u.getEmail())) {
                                        followerList.add(u);
                                        break;
                                    }
                                }
                                index++;
                            }
                            setUpRecyclerView();
                        }
                    });

                }

            }
        });
        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {

        userAdapter = new UserAdapter(followerList, CustomerProfileInfoFollowersActivity.this, R.layout.quan_user_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvCustomerFollowers.addItemDecoration(itemSpacingDecoration);
        binding.rvCustomerFollowers.setLayoutManager(new LinearLayoutManager(CustomerProfileInfoFollowersActivity.this));
        binding.rvCustomerFollowers.setAdapter(userAdapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}