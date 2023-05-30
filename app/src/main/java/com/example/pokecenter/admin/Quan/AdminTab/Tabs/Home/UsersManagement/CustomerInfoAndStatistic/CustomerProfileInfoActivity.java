package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.CustomerInfoAndStatistic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer;
import com.example.pokecenter.databinding.ActivityCustomerProfileInfoBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CustomerProfileInfoActivity extends AppCompatActivity {

    private ActivityCustomerProfileInfoBinding binding;
    private Customer customer;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Customer Profile Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityCustomerProfileInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get Customer object
        Intent intent = getIntent();
        customer = (Customer) intent.getSerializableExtra("Customer");
        //Bind to views
        Picasso.get().load(customer.getAvatar()).into(binding.ivCustomerProfileInfoAvatar);
        binding.tvCustomerProfileInfoName.setText(customer.getUsername());
        binding.tvCustomerProfileInfoEmail.setText(customer.getEmail());
        binding.tvCustomerProfileInfoPhoneNumber.setText(customer.getPhoneNumber());
        binding.tvCustomerProfileInfoRegistrationDate.setText(customer.getRegistrationDate());
        if (Objects.equals(customer.getGender(), "male")) {
            binding.rbCustomerProfileInfoMaleGender.setChecked(true);
        } else {
            binding.rbCustomerProfileInfoFemaleGender.setChecked(true);
        }


        binding.clCustomerProfileInfoAddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerProfileInfoActivity.this, CustomerProfileInfoAddressesActivity.class);
                intent.putExtra("Customer", customer);
                startActivity(intent);
            }
        });
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