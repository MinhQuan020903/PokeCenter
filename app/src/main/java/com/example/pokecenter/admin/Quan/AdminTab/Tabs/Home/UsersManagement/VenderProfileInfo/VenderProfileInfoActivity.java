package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.CustomerProfileInfo.CustomerProfileInfoActivity;
import com.example.pokecenter.databinding.ActivityCustomerProfileInfoBinding;
import com.example.pokecenter.databinding.ActivityVenderProfileInfoBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class VenderProfileInfoActivity extends AppCompatActivity {

    private ActivityVenderProfileInfoBinding binding;
    private Vender vender;
    private Dialog confirmationDialog;
    private Dialog adminAuthDialog;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenderProfileInfoBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Vender Profile Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Get Customer object
        Intent intent = getIntent();
        vender = (Vender) intent.getSerializableExtra("Vender");
        //Bind to views
        Picasso.get().load(vender.getAvatar()).into(binding.ivVenderProfileInfoAvatar);
        binding.tvVenderProfileInfoName.setText(vender.getUsername());
        binding.tvVenderProfileInfoEmail.setText(vender.getEmail());
        binding.tvVenderProfileInfoPhoneNumber.setText(vender.getPhoneNumber());
        binding.tvVenderProfileInfoRegistrationDate.setText(vender.getRegistrationDate());
        if (Objects.equals(vender.getGender(), "male")) {
            binding.rbVenderProfileInfoMaleGender.setChecked(true);
        } else {
            binding.rbVenderProfileInfoFemaleGender.setChecked(true);
        }

        binding.bVenderProfileInfoDisableAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create Confirmation Dialog
                confirmationDialog = new Dialog(VenderProfileInfoActivity.this);
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
                        adminAuthDialog = new Dialog(VenderProfileInfoActivity.this);
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

        setContentView(binding.getRoot());
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