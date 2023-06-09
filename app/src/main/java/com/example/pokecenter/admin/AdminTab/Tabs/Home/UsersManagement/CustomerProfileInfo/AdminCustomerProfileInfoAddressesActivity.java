package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.CustomerProfileInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.CustomerAddressesAdapter;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.ActivityAdminCustomerProfileInfoAddressesBinding;

public class AdminCustomerProfileInfoAddressesActivity extends AppCompatActivity {
    private ActivityAdminCustomerProfileInfoAddressesBinding binding;
    private Customer customer;
    private CustomerAddressesAdapter customerAddressesAdapter;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminCustomerProfileInfoAddressesBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.light_primary;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        getSupportActionBar().setTitle("Customer's Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);



        //Get Customer object
        Intent intent = getIntent();
        customer = (Customer) intent.getSerializableExtra("Customer");

        if (customer.getAddresses() != null) {
            binding.llCustomerNoAddresses.setVisibility(View.INVISIBLE);
            setUpRecyclerView();
        } else {
            binding.rvCustomerAddresses.setVisibility(View.INVISIBLE);
        }
        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {

        customerAddressesAdapter = new CustomerAddressesAdapter(customer.getAddresses(), AdminCustomerProfileInfoAddressesActivity.this, R.layout.quan_address_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvCustomerAddresses.addItemDecoration(itemSpacingDecoration);
        binding.rvCustomerAddresses.setLayoutManager(new LinearLayoutManager(AdminCustomerProfileInfoAddressesActivity.this));
        binding.rvCustomerAddresses.setAdapter(customerAddressesAdapter);
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