package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo.VenderProfileInfoActivity;
import com.example.pokecenter.databinding.ActivityAdminProductDetailBinding;
import com.example.pokecenter.databinding.ActivityProductStatisticBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class AdminProductDetailActivity extends AppCompatActivity {

    private ActivityAdminProductDetailBinding binding;
    private AdminProduct adminProduct;
    private Dialog descriptionDialog;
    private Dialog confirmationDialog;
    private Dialog adminAuthDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Product Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        adminProduct = (AdminProduct)intent.getSerializableExtra("AdminProduct");

        binding = ActivityAdminProductDetailBinding.inflate(getLayoutInflater());

        //Bind views
        Picasso.get().load(adminProduct.getImages().get(0)).into(binding.ivProducImage);
        binding.tvProductName.setText(adminProduct.getName());
        binding.tvProductVenderId.setText(adminProduct.getVenderId());
        binding.tvProductDescription.setText(adminProduct.getDesc());

        binding.clProductReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProductDetailActivity.this, AdminProductDetailReviewsActivity.class);
                intent.putExtra("AdminProduct", adminProduct);
                startActivity(intent);
            }
        });


        binding.clProductDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionDialog = new Dialog(AdminProductDetailActivity.this);
                descriptionDialog.setContentView(R.layout.quan_dialog_product_description);
                descriptionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Window window = descriptionDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                descriptionDialog.show();

                TextView title = descriptionDialog.findViewById(R.id.dtProductDescription);
                title.setText("PRODUCT DESCRIPTION");

                TextView content = descriptionDialog.findViewById(R.id.dcProductDescription);
                content.setText(adminProduct.getDesc());

                Button bAccept = descriptionDialog.findViewById(R.id.ok_button);

                bAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        descriptionDialog.dismiss();
                    }
                });
            }
        });

        binding.bProductProfileInfoDisableAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create Confirmation Dialog
                confirmationDialog = new Dialog(AdminProductDetailActivity.this);
                confirmationDialog.setContentView(R.layout.quan_dialog_confirm_delete_product);
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
                        adminAuthDialog = new Dialog(AdminProductDetailActivity.this);
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
                                                Toast.makeText(AdminProductDetailActivity.this, "DELETED SUCCESSFULLY.", Toast.LENGTH_SHORT).show();
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