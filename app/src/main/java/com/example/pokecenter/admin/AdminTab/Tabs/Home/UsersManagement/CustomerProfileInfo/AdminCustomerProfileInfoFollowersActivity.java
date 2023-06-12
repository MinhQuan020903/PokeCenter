package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.CustomerProfileInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseFetchUser;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.example.pokecenter.admin.AdminTab.Model.User.UserAdapter;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.AdminVenderInfoAndStatisticActivity;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminCustomerProfileInfoFollowersBinding;

import java.util.ArrayList;

public class AdminCustomerProfileInfoFollowersActivity extends AppCompatActivity {
    private ActivityAdminCustomerProfileInfoFollowersBinding binding;
    private Customer customer;
    private UserAdapter userAdapter;
    private ArrayList<User> followerList;
    private ArrayList<String> followerEmailList;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ///INIT NEW ARRAYLIST!!!
        binding = ActivityAdminCustomerProfileInfoFollowersBinding.inflate(getLayoutInflater());

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

                            userAdapter.setOnItemClickListener(new OnItemClickListener<User>() {
                                @Override
                                public void onItemClick(User user, int position) {
                                    Intent intent = new Intent(AdminCustomerProfileInfoFollowersActivity.this, AdminVenderInfoAndStatisticActivity.class);
                                    intent.putExtra("Customer_Vender", user);
                                    startActivity(intent);
                                }
                            });
                        }
                    });

                }

            }
        });
        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {

        userAdapter = new UserAdapter(followerList, AdminCustomerProfileInfoFollowersActivity.this, R.layout.quan_user_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvCustomerFollowers.addItemDecoration(itemSpacingDecoration);
        binding.rvCustomerFollowers.setLayoutManager(new LinearLayoutManager(AdminCustomerProfileInfoFollowersActivity.this));
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