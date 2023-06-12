package com.example.pokecenter.admin.AdminTab.Tabs.Home.VoucherManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucher;
import com.example.pokecenter.databinding.ActivityAddBlockVoucherBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AddBlockVoucherActivity extends AppCompatActivity {

    private ActivityAddBlockVoucherBinding binding;
    private Dialog adminAuthDialog;
    private FirebaseSupportVoucher firebaseSupportVoucher;
    private InputMethodManager inputMethodManager;
    private LocalDate localDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Add New Block Voucher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding = ActivityAddBlockVoucherBinding.inflate(getLayoutInflater());

        firebaseSupportVoucher = new FirebaseSupportVoucher(this);

        //Get current date
        //Determine current day
        Date date = new Date();
        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String selectedDate = String.format("%02d-%02d-%04d", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
        binding.bBlockVoucherStartDate.setText(selectedDate);
        binding.bBlockVoucherEndDate.setText(selectedDate);

        binding.bBlockVoucherStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        v.getContext(), // Pass the context from the button's view
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Note: month is zero-based, so add 1 to get the correct month value
                                String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                                binding.bBlockVoucherStartDate.setText(selectedDate);
                            }
                        },
                        localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth() // Set the initial date today
                );

                datePickerDialog.show();
            }
        });


        binding.bBlockVoucherEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        v.getContext(), // Pass the context from the button's view
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Note: month is zero-based, so add 1 to get the correct month value
                                String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                                binding.bBlockVoucherEndDate.setText(selectedDate);
                            }
                        },
                        localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth() // Set the initial date today
                );

                datePickerDialog.show();
            }
        });

        binding.bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate startDate = LocalDate.parse(binding.bBlockVoucherStartDate.getText().toString(), dateTimeFormatter);
                LocalDate endDate = LocalDate.parse(binding.bBlockVoucherEndDate.getText().toString(), dateTimeFormatter);

                if (endDate.isBefore(startDate)) {
                    Toast.makeText(AddBlockVoucherActivity.this, "Invalid start/end date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (binding.etBlockVoucherName.getText().toString().equals("")
                        || binding.etVoucherQuantity.getText().toString().equals("")
                        || binding.etVoucherValue.getText().toString().equals("")) {
                    Toast.makeText(AddBlockVoucherActivity.this, "Please type in values", Toast.LENGTH_SHORT).show();
                    return;
                }

                AdminBlockVoucher blockVoucher = new AdminBlockVoucher(
                        binding.etBlockVoucherName.getText().toString(),
                        Integer.parseInt(binding.etVoucherQuantity.getText().toString()),
                        binding.bBlockVoucherStartDate.getText().toString(),
                        binding.bBlockVoucherEndDate.getText().toString(),
                        Integer.parseInt(binding.etVoucherValue.getText().toString())
                );


                adminAuthDialog = new Dialog(AddBlockVoucherActivity.this);
                adminAuthDialog.setContentView(R.layout.quan_dialog_admin_auth);
                adminAuthDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Window window = adminAuthDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.getDecorView().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Check if the touch event is an ACTION_DOWN event
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            // Hide the keyboard
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });

                TextView tvAdminAuthFailed = adminAuthDialog.findViewById(R.id.tvAdminAuthFailed);
                tvAdminAuthFailed.setVisibility(View.INVISIBLE);

                adminAuthDialog.show();

                EditText etAdminAuthPassword = adminAuthDialog.findViewById(R.id.etAdminAuthPassword);
                Button bCancel = adminAuthDialog.findViewById(R.id.bAuthCancel);
                Button bAccept = adminAuthDialog.findViewById(R.id.bAuthAccept);

                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

                                        firebaseSupportVoucher.addNewBlockVoucher(blockVoucher, new FirebaseCallback<Boolean>() {
                                            @Override
                                            public void onCallback(Boolean done) {
                                                Toast.makeText(AddBlockVoucherActivity.this, "Add new block voucher successfully.", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                        adminAuthDialog.dismiss();

                                    } else {
                                        tvAdminAuthFailed.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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