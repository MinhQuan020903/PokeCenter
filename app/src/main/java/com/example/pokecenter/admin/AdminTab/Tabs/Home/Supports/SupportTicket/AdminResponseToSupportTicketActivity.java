package com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.SupportTicket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
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
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportRequest;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportUser;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequest;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.ImageAdapter;
import com.example.pokecenter.admin.AdminTab.Model.AdminSupportTicket.AdminSupportTicket;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.ProductRequest.AdminResponseToRequestActivity;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.JavaMailUtils;
import com.example.pokecenter.databinding.ActivityAdminResponseToRequestBinding;
import com.example.pokecenter.databinding.ActivityAdminResponseToSupportTicketBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminResponseToSupportTicketActivity extends AppCompatActivity {

    private ActivityAdminResponseToSupportTicketBinding binding;
    private AdminSupportTicket supportTicket;
    private Dialog descriptionDialog;
    private Dialog confirmationDialog;
    private Dialog adminAuthDialog;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.quan_light_green));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.quan_light_green;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        getSupportActionBar().setTitle("Support Ticket Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding = ActivityAdminResponseToSupportTicketBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        supportTicket = (AdminSupportTicket) intent.getSerializableExtra("AdminSupportTicket");
        if (supportTicket != null) {
            binding.tvCustomerId.setText(supportTicket.getCustomerId().replace(",","."));
            binding.tvCreateDate.setText(supportTicket.getCreateDate());
            binding.tvDesc.setText(supportTicket.getDesc());
            binding.tvContactMethod.setText(supportTicket.getContactMethod());
            binding.tvTitle.setText(supportTicket.getProblemName());
        }

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        binding.clDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionDialog = new Dialog(AdminResponseToSupportTicketActivity.this);
                descriptionDialog.setContentView(R.layout.quan_dialog_product_description);
                descriptionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Window window = descriptionDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                descriptionDialog.show();

                TextView title = descriptionDialog.findViewById(R.id.dtProductDescription);
                title.setText("REQUEST DESCRIPTION");

                TextView content = descriptionDialog.findViewById(R.id.dcProductDescription);
                content.setText(supportTicket.getDesc());

                Button bAccept = descriptionDialog.findViewById(R.id.ok_button);

                bAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        descriptionDialog.dismiss();
                    }
                });
            }
        });

        binding.bApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUpAdminAuthDialog();

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
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        //Send response of support ticket to customer's notifications on Firebase
                                        FirebaseSupportUser firebaseSupportUser = new FirebaseSupportUser(AdminResponseToSupportTicketActivity.this);
                                        firebaseSupportUser.pushResponseToUserSupportTicket(
                                                supportTicket,
                                                true,
                                                binding.etResponseDesc.getText().toString(),
                                                new FirebaseCallback<Boolean>() {
                                            @Override
                                            public void onCallback(Boolean user) {
                                                Toast.makeText(AdminResponseToSupportTicketActivity.this, "Send Response to Customer Succesfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        adminAuthDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Authentication failed
                                        Log.e("FirebaseAuth", "Authentication failed: " + e.getMessage());
                                    }
                                });
                    }
                });
            }
        });

        binding.bDisapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUpAdminAuthDialog();

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
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        //Send response of support ticket to customer's notifications on Firebase
                                        FirebaseSupportUser firebaseSupportUser = new FirebaseSupportUser(AdminResponseToSupportTicketActivity.this);
                                        firebaseSupportUser.pushResponseToUserSupportTicket(
                                                supportTicket,
                                                false,
                                                binding.etResponseDesc.getText().toString(),
                                                new FirebaseCallback<Boolean>() {
                                                    @Override
                                                    public void onCallback(Boolean user) {
                                                        Toast.makeText(AdminResponseToSupportTicketActivity.this, "Send Response to Customer Succesfully!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        adminAuthDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Authentication failed
                                        Log.e("FirebaseAuth", "Authentication failed: " + e.getMessage());
                                    }
                                });
                    }
                });
            }
        });

        setContentView(binding.getRoot());

    }

    public void setUpAdminAuthDialog() {
        adminAuthDialog = new Dialog(AdminResponseToSupportTicketActivity.this);
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
    }
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}