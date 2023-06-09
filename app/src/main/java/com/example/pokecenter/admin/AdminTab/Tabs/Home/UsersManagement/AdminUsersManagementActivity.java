package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportUser;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.example.pokecenter.admin.AdminTab.Model.User.UserAdapter;
import com.example.pokecenter.admin.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminUsersManagementBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

public class AdminUsersManagementActivity extends AppCompatActivity {

    private ArrayList<User> usersList;
    private ArrayList<User> filteredList;
    private ArrayList<String> userRoles;
    private ArrayList<String> userSorts;
    private InputMethodManager inputMethodManager;
    private UserAdapter userAdapter;
    private ActivityAdminUsersManagementBinding binding;
    private Collator collator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.light_primary;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        binding = ActivityAdminUsersManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Create a comparator for Vietnamese
        collator = Collator.getInstance(new Locale("vi"));

        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("accounts");

        usersList = new ArrayList<>();

        FirebaseSupportUser firebaseSupportUser = new FirebaseSupportUser(this);

        firebaseSupportUser.getUsersListFromFirebase(new FirebaseCallback<ArrayList<User>>() {
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
                        binding.getRoot().clearFocus();
                        binding.spUserRole.clearFocus();

                        ArrayList<User> sortedList;
                        //If user has search in EditText at least 1 time,
                        //Use the filteredList for spinner
                        //Since the original List was modified
                        if (filteredList != null) {
                            sortedList = new ArrayList<>(filteredList);
                        } else {
                            sortedList = new ArrayList<>(usersList);
                        }

                        switch(position) {
                            case 0: {   //Ascending
                                Collections.sort(sortedList, Comparator.comparing(User::getUsername, collator));
                                break;
                            }
                            case 1: {   //Descending
                                Collections.sort(sortedList, Comparator.comparing(User::getUsername, collator).reversed());

                                break;
                            }
                        }

                        userAdapter.setUsersList(sortedList);
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

                        // Initialize filteredList
                        filteredList = new ArrayList<>();

                        //Get position of role spinner
                        int role = binding.spUserRole.getSelectedItemPosition();

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
                userAdapter.setOnItemClickListener(new OnItemClickListener<User>() {
                    @Override
                    public void onItemClick(User user, int position) {
                        Intent intent = null;
                        switch (user.getRole()) {
                            case 0: {
                                intent = new Intent(AdminUsersManagementActivity.this, AdminCustomerInfoAndStatisticActivity.class);
                                break;
                            }
                            case 1: {
                                intent = new Intent(AdminUsersManagementActivity.this, AdminVenderInfoAndStatisticActivity.class);
                                break;
                            }
                        }
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }
                });

                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        binding.etUsersManagementSearch.clearFocus();
                        binding.spUserRole.clearFocus();
                        binding.spUserSort.clearFocus();
                    }
                });

                binding.progressBar.setVisibility(View.INVISIBLE);
            }


        });

        binding.bUsersManagementSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etUsersManagementSearch.getText().toString().contains("@")) {
                    firebaseSupportUser.getUserByEmail(binding.etUsersManagementSearch.getText().toString(), new FirebaseCallback<User>() {
                        @Override
                        public void onCallback(User user) {
                            ArrayList<User> users = new ArrayList<>();
                            users.add(user);
                            setUpRecyclerView();
                            userAdapter.setUsersList(users);
                            userAdapter.notifyDataSetChanged();
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            userAdapter.setOnItemClickListener(new OnItemClickListener<User>() {
                                @Override
                                public void onItemClick(User user, int position) {
                                    Intent intent = null;
                                    switch (user.getRole()) {
                                        case 0: {
                                            intent = new Intent(AdminUsersManagementActivity.this, AdminCustomerInfoAndStatisticActivity.class);
                                            break;
                                        }
                                        case 1: {
                                            intent = new Intent(AdminUsersManagementActivity.this, AdminVenderInfoAndStatisticActivity.class);
                                            break;
                                        }
                                    }
                                    intent.putExtra("User", user);
                                    startActivity(intent);
                                }
                            });

                        }
                    });
                }
            }
        });

    }

    public void setUpRoleSpinner() {
        userRoles = new ArrayList<>();
        userRoles.add("All");
        userRoles.add("Customer");
        userRoles.add("Vender");

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