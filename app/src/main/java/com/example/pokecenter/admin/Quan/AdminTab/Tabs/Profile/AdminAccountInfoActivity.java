package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.ActivityAdminAccountInfoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminAccountInfoActivity extends AppCompatActivity {

    private Account currentAccount;
    private ActivityAdminAccountInfoBinding binding;
    private InputMethodManager inputMethodManager;
    private Uri mImageUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        binding = ActivityAdminAccountInfoBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        currentAccount = (Account)intent.getSerializableExtra("currentAccount");

        getSupportActionBar().setTitle("Change Account Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Picasso.get().load(currentAccount.getAvatar()).into(binding.ivAdminProfileAvatar);

        binding.etAdminUsername.setText(currentAccount.getUsername());
        binding.etAdminEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        binding.etAdminPhone.setText(currentAccount.getPhoneNumber());

        setContentView(binding.getRoot());

        binding.bAdminSaveChange.setOnClickListener(view -> {

            if (getCurrentFocus() != null) {
                getCurrentFocus().clearFocus();
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            updateAccountInfoToServer();
        });

        binding.bAdminCancelChange.setOnClickListener(view -> {
            onSupportNavigateUp();
        });
    }



    private void updateAccountInfoToServer() {

        currentAccount.setUsername(binding.etAdminUsername.getText().toString());
        currentAccount.setPhoneNumber(binding.etAdminPhone.getText().toString());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                new FirebaseSupportAccount().updateAccountInfo(currentAccount);
            } catch (IOException e) {
                isSuccessful = false;
            }

            boolean finalIsSuccessful = isSuccessful;

            handler.post(() -> {
                if (finalIsSuccessful) {

                    Toast.makeText(AdminAccountInfoActivity.this, "Update Information Successful", Toast.LENGTH_SHORT)
                            .show();
                    finish();

                } else {
                    Toast.makeText(AdminAccountInfoActivity.this, "Update information failed", Toast.LENGTH_SHORT)
                            .show();
                }

            });

            startActivity(new Intent(AdminAccountInfoActivity.this, SignInActivity.class));
        });

    }

    public void openGetImageActivityForResult(Intent intent) {
        getImageActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> getImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        mImageUri = data.getData();
                        binding.ivAdminProfileAvatar.setImageURI(mImageUri);
                        updateAvatar();
                    }
                }
            });
    private void updateAvatar() {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (mImageUri != null) {
            StorageReference fileRef = mStorageRef.child(currentEmail.replace(".", ","));
            fileRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                            downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(AdminAccountInfoActivity.this, "Update Avatar Successful", Toast.LENGTH_SHORT).show();
                                    currentAccount.setAvatar(String.valueOf(uri));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminAccountInfoActivity.this, "Update Avatar Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
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