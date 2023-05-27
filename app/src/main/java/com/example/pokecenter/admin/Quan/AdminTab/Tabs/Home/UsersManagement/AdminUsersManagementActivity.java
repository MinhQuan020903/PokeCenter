package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchUser;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Admin;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.UserAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminUsersManagementBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AdminUsersManagementActivity extends AppCompatActivity {

    private ArrayList<User> usersList;
    private ArrayList<String> userRoles;
    private ArrayList<String> userSorts;
    private InputMethodManager inputMethodManager;
    private UserAdapter userAdapter;
    private ActivityAdminUsersManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivityAdminUsersManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("accounts");

        usersList = new ArrayList<>();

        FirebaseFetchUser firebaseFetchUser = new FirebaseFetchUser(this);

        firebaseFetchUser.getUsersListFromFirebase(new FirebaseCallback<ArrayList<User>>() {
            @Override
            public void onCallback(ArrayList<User> users) {
                usersList = users;
                //Set up spinner
                setUpRoleSpinner();
                //Set up recyclerview for user
                setUpRecyclerView();

                //Set onitemclick when user choose role filter
                binding.spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //View All
                                userAdapter.setUsersList(usersList);
                                break;
                            }
                            case 1: {   //View Customer
                                userAdapter.setUsersList(usersList.stream()
                                        .filter(v -> v instanceof Customer)
                                        .collect(Collectors.toCollection(ArrayList::new)));
                                break;
                            }
                            case 2: {   //View Vender
                                userAdapter.setUsersList(usersList.stream()
                                        .filter(v -> v instanceof Vender)
                                        .collect(Collectors.toCollection(ArrayList::new)));
                                break;
                            }
                            case 3: {   //View Vender
                                userAdapter.setUsersList(usersList.stream()
                                        .filter(v -> v instanceof Admin)
                                        .collect(Collectors.toCollection(ArrayList::new)));
                                break;
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                //Set onitemclick when user choose role filter
                binding.spUserSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //Ascending
                                usersList.sort(Comparator.comparing(User::getUsername));
                                break;
                            }
                            case 1: {   //Descending
                                usersList.sort(Comparator.comparing(User::getUsername).reversed());

                                break;
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.etUsersManagementSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not used in this case
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchQuery = s.toString().toLowerCase();
                        //Get position of role spinner
                        int role = binding.spUserRole.getSelectedItemPosition();

                        ArrayList<User> filteredList = new ArrayList<>();
                        for (User user : usersList) {
                            String userName = user.getUsername().toLowerCase();

                            //If selected role in spinner is "All"
                            if (role == 0) {
                                if (userName.contains(searchQuery)) {
                                    filteredList.add(user);
                                }
                            } else {    //If selected role in spinner is "Customer", "Vender" or "Admin"
                                if (userName.contains(searchQuery) && user.getRole() == role - 1) {
                                    filteredList.add(user);
                                }
                            }

                        }
                        userAdapter.setUsersList(filteredList);
                        userAdapter.notifyDataSetChanged();
                    }

                    // user types in email
                    @Override
                    public void afterTextChanged(Editable s) {
                        String searchQuery = s.toString();
                        if (searchQuery.contains("@")) {
                            //If selected role in spinner is "All"
                            //Get position of role spinner
                            int role = binding.spUserRole.getSelectedItemPosition();

                            ArrayList<User> filteredList = new ArrayList<>();
                            for (User user : usersList) {
                                String userEmail = user.getEmail().toLowerCase();

                                //If selected role in spinner is "All"
                                if (role == 0) {
                                    if (userEmail.equals(searchQuery)) {
                                        filteredList.add(user);
                                    }
                                } else {    //If selected role in spinner is "Customer", "Vender" or "Admin"
                                    if (userEmail.equals(searchQuery) && user.getRole() == role - 1) {
                                        filteredList.add(user);
                                    }
                                }

                            }

                            userAdapter.setUsersList(filteredList);
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });

                //Navigate to Info and Statistic Activity when an item is clicked
                userAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(User user, int position) {
                        Intent intent = null;
                        switch (user.getRole()) {
                            case 0: {
                                intent = new Intent(AdminUsersManagementActivity.this, CustomerInfoAndStatisticActivity.class);
                                break;
                            }
                            case 1: {
                                intent = new Intent(AdminUsersManagementActivity.this, VenderInfoAndStatisticActivity.class);
                                break;
                            }
                            case 2: {
                                intent = new Intent(AdminUsersManagementActivity.this, AdminInfoAndStatisticActivity.class);
                                break;
                            }
                        }
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }
                });

                binding.progressBar.setVisibility(View.INVISIBLE);
            }


        });

    }

    public void setUpRoleSpinner() {
        userRoles = new ArrayList<>();
        userRoles.add("All");
        userRoles.add("Customer");
        userRoles.add("Vender");
        userRoles.add("Admin");

        userSorts = new ArrayList<>();
        userSorts.add("Ascending");
        userSorts.add("Descending");

        //Init UserRoleSpinner
        ArrayAdapter userRoleSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, userRoles);
        binding.spUserRole.setAdapter(userRoleSpinner);

        //Init UserSortSpinner
        ArrayAdapter userSortSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, userSorts);
        binding.spUserSort.setAdapter(userSortSpinner);

    }
    public void setUpRecyclerView() {
        userAdapter = new UserAdapter(usersList, AdminUsersManagementActivity.this, R.layout.quan_user_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvUsersManagement.addItemDecoration(itemSpacingDecoration);
        binding.rvUsersManagement.setLayoutManager(new LinearLayoutManager(AdminUsersManagementActivity.this));
        binding.rvUsersManagement.setAdapter(userAdapter);
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