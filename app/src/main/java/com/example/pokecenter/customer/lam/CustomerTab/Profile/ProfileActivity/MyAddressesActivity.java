package com.example.pokecenter.customer.lam.CustomerTab.Profile.ProfileActivity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.address.AddressAdapter;
import com.example.pokecenter.databinding.ActivityMyAddressesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAddressesActivity extends AppCompatActivity {

    private ActivityMyAddressesBinding binding;

    private List<Address> myAddresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set statusBar Color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.white)));

        /* Set color to title */
        getSupportActionBar().setTitle("My Addresses");
        // getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#027B96\">Profile</font>", Html.FROM_HTML_MODE_LEGACY));

        /* Set up back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.lam_round_arrow_back_secondary_24);

        binding = ActivityMyAddressesBinding.inflate(getLayoutInflater());

        /* Set Address ListView */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rcvAddresses.setLayoutManager(linearLayoutManager);
        binding.rcvAddresses.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        AddressAdapter addressAdapter = new AddressAdapter(this, myAddresses);
        binding.rcvAddresses.setAdapter(addressAdapter);

        binding.addNewAddress.setOnClickListener(view -> {
            openBottomSheetDialog();
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            final List<Address> fetchedAddressesData = new FirebaseSupportCustomer().fetchingAddressesData();

            handler.post(() -> {
                myAddresses = fetchedAddressesData;
                binding.progressBar.setVisibility(View.INVISIBLE);
                addressAdapter.setData(myAddresses);
            });
        });

        setContentView(binding.getRoot());
    }

    private void openBottomSheetDialog() {

        View viewDialog = getLayoutInflater().inflate(R.layout.lam_address_bottom_sheet, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}