package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.CustomerProfileInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.databinding.ActivityAdminCustomerProfileInfoBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AdminCustomerProfileInfoActivity extends AppCompatActivity {

    private ActivityAdminCustomerProfileInfoBinding binding;
    private Customer customer;
    private Dialog confirmationDialog;
    private Dialog adminAuthDialog;
    private InputMethodManager inputMethodManager;

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
        getSupportActionBar().setTitle("Customer Profile Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityAdminCustomerProfileInfoBinding.inflate(getLayoutInflater());
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
                Intent intent = new Intent(AdminCustomerProfileInfoActivity.this, AdminCustomerProfileInfoAddressesActivity.class);
                intent.putExtra("Customer", customer);
                startActivity(intent);
            }
        });

        binding.clCustomerProfileInfoFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCustomerProfileInfoActivity.this, AdminCustomerProfileInfoFollowersActivity.class);
                intent.putExtra("Customer", customer);
                startActivity(intent);
            }
        });

        binding.bCustomerProfileInfoDisableAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create Confirmation Dialog
                confirmationDialog = new Dialog(AdminCustomerProfileInfoActivity.this);
                confirmationDialog.setContentView(R.layout.quan_dialog_confirm_delete_account);
                confirmationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Window window = confirmationDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationDialog.show();

                Button bCancel = confirmationDialog.findViewById(R.id.bCancel);
                Button bAccept = confirmationDialog.findViewById(R.id.bAccept);

                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmationDialog.dismiss();
                    }
                });

                //Create Admin Authentication Dialog
                bAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adminAuthDialog = new Dialog(AdminCustomerProfileInfoActivity.this);
                        adminAuthDialog.setContentView(R.layout.quan_dialog_admin_auth);
                        adminAuthDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        Window window = adminAuthDialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView tvAdminAuthFailed = adminAuthDialog.findViewById(R.id.tvAdminAuthFailed);
                        tvAdminAuthFailed.setVisibility(View.INVISIBLE);

                        adminAuthDialog.show();

                        EditText etAdminAuthPassword = adminAuthDialog.findViewById(R.id.etAdminAuthPassword);
                        Button bCancel = adminAuthDialog.findViewById(R.id.bAuthCancel);
                        Button bAccept = adminAuthDialog.findViewById(R.id.bAuthAccept);

                        bCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationDialog.dismiss();
                                adminAuthDialog.dismiss();
                            }
                        });
                        bAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String password = etAdminAuthPassword.getText().toString();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                                FirebaseAuth.getInstance().signInWithCredential(credential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                confirmationDialog.dismiss();
                                                adminAuthDialog.dismiss();
                                                Toast.makeText(AdminCustomerProfileInfoActivity.this, "DISABLED SUCCESSFULLY.", Toast.LENGTH_SHORT).show();

                                            } else {
                                                tvAdminAuthFailed.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        });
                    }
                });

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