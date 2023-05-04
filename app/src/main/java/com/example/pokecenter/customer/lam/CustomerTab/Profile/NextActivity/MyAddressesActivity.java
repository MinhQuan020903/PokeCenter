package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.AddressRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.address.AddressAdapter;
import com.example.pokecenter.databinding.ActivityMyAddressesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAddressesActivity extends AppCompatActivity implements AddressRecyclerViewInterface {

    private ActivityMyAddressesBinding binding;

    public static List<Address> myAddresses = new ArrayList<>();

    private AddressAdapter addressAdapter;

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

        addressAdapter = new AddressAdapter(this, myAddresses, this);
        binding.rcvAddresses.setAdapter(addressAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Address> fetchedAddressesData;
            try {
                fetchedAddressesData = new FirebaseSupportCustomer().fetchingAddressesData();
            } catch (IOException e) {
                fetchedAddressesData = null;
            }

            List<Address> finalFetchedAddressesData = fetchedAddressesData;
            handler.post(() -> {
                if (finalFetchedAddressesData != null) {
                    myAddresses = finalFetchedAddressesData;
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    addressAdapter.setData(myAddresses);
                } else {
                    Toast.makeText(this, "Connect sever failed", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });

        setContentView(binding.getRoot());
    }

    private void openBottomSheetDialog(Address existingAddress) {

        View viewDialog = getLayoutInflater().inflate(R.layout.lam_bottom_sheet_address, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);
        dialog.show();

        viewDialog.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        EditText fullName = viewDialog.findViewById(R.id.fullName_edit_text);
        EditText phoneNumber = viewDialog.findViewById(R.id.phoneNumber_edit_text);

        EditText numberStreetAddress = viewDialog.findViewById(R.id.numberStreetAddress_edit_text);
        EditText address2 = viewDialog.findViewById(R.id.address_2_edit_text);

        RadioButton homeButton = viewDialog.findViewById(R.id.homeType);
        RadioButton officeButton = viewDialog.findViewById(R.id.officeType);

        Button finishButton = viewDialog.findViewById(R.id.finish_button);
        Switch setDeliverySwitch = viewDialog.findViewById(R.id.set_delivery_switch);


        if (existingAddress != null) {

            fullName.setText(existingAddress.getReceiverName());
            phoneNumber.setText(existingAddress.getReceiverPhoneNumber());
            numberStreetAddress.setText(existingAddress.getNumberStreetAddress());
            address2.setText(existingAddress.getAddress2());
            homeButton.setChecked(existingAddress.getType().equals("home") ? true : false);
            officeButton.setChecked(existingAddress.getType().equals("home") ? false : true);
            setDeliverySwitch.setChecked(existingAddress.getDeliveryAddress());

        }

        homeButton.setOnClickListener(view -> {
            officeButton.setChecked(false);
        });

        officeButton.setOnClickListener(view -> {
            homeButton.setChecked(false);
        });

        View parentView = (View) viewDialog.getParent();

        finishButton.setOnClickListener(view -> {
            finishButton.setVisibility(View.INVISIBLE);

            if (validateDataInput(fullName, phoneNumber, numberStreetAddress, address2)) {

                Address newAddress = new Address(
                        null,
                        fullName.getText().toString(),
                        phoneNumber.getText().toString(),
                        numberStreetAddress.getText().toString(),
                        address2.getText().toString(),
                        homeButton.isChecked() ? "home" : "office",
                        setDeliverySwitch.isChecked()
                );

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                if (existingAddress != null) {

                    /* update existing address */
                    existingAddress.setReceiverName(newAddress.getReceiverName());
                    existingAddress.setReceiverPhoneNumber(newAddress.getReceiverPhoneNumber());
                    existingAddress.setNumberStreetAddress(newAddress.getNumberStreetAddress());
                    existingAddress.setAddress2(newAddress.getAddress2());
                    existingAddress.setType(newAddress.getType());
                    existingAddress.setDeliveryAddress(newAddress.getDeliveryAddress());

                    executor.execute(() -> {

                        boolean isSuccessful = true;

                        try {
                            new FirebaseSupportCustomer().updateAddress(existingAddress);
                        } catch (IOException e) {
                            isSuccessful = false;
                        }

                        boolean finalIsSuccessful = isSuccessful;
                        handler.post(() -> {

                            if (finalIsSuccessful) {
                                addressAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                                Toast.makeText(this, "Update address successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                                finishButton.setText("try again");
                            }

                        });
                    });
                } else {

                    /* add newAddress to Firebase */
                    executor.execute(() -> {

                        boolean isSuccessful = true;

                        try {
                            new FirebaseSupportCustomer().addNewAddressUsingApi(newAddress);
                        } catch (IOException e) {
                            isSuccessful = false;
                        }

                        boolean finalIsSuccessful = isSuccessful;
                        handler.post(() -> {
                            if (finalIsSuccessful) {
                                myAddresses.add(newAddress);
                                addressAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                                Toast.makeText(this, "Added new address", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Something wrong because connection error", Toast.LENGTH_SHORT).show();
                                finishButton.setText("try again");
                            }
                        });
                    });
                }



            }
            finishButton.setVisibility(View.VISIBLE);
        });

    }
    private boolean validateDataInput(EditText fullName, EditText phoneNumber, EditText numberStreetAddress, EditText address2) {
        if (fullName.getText().toString().isEmpty()) {
            fullName.setError("You have not entered Full Name");
            return false;
        }

        if (phoneNumber.getText().toString().isEmpty()) {
            phoneNumber.setError("You have not entered Phone Number");
            return false;
        }

        if (numberStreetAddress.getText().toString().isEmpty()) {
            numberStreetAddress.setError("You have not entered Address 1");
            return false;
        }

        if (address2.getText().toString().isEmpty()) {
            address2.setError("You have not entered Address 2");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lam_menu_only_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addButton) {
            openBottomSheetDialog(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        myAddresses = null;
    }

    @Override
    public void onAddressDeleteButtonClick(int position) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBarBg.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                new FirebaseSupportCustomer().deleteAddress(myAddresses.get(position).getId());
            } catch (IOException e) {
                isSuccessful = false;
            }
            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {

                    Toast.makeText(this, "Address removed", Toast.LENGTH_SHORT)
                            .show();
                    myAddresses.remove(position);
                    addressAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Delete address failed, try againt later!", Toast.LENGTH_SHORT)
                            .show();
                }
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.progressBarBg.setVisibility(View.INVISIBLE);
            });
        });
    }

    @Override
    public void onAddressItemClick(int position) {
        openBottomSheetDialog(myAddresses.get(position));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}