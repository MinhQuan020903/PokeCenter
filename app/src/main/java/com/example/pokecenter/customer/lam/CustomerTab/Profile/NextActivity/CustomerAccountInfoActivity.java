package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.ActivityCustomerAccountInfoBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.Unit;

public class CustomerAccountInfoActivity extends AppCompatActivity {

    private ActivityCustomerAccountInfoBinding binding;

    private Account currentAccount;

    private InputMethodManager inputMethodManager;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Account Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityCustomerAccountInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mStorageRef = FirebaseStorage.getInstance().getReference("avatar");

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        fetchingAndSetupData();

        TextView genderBoy = binding.genderBoy;
        TextView genderGirl = binding.genderGirl;

        genderBoy.setOnClickListener(view -> {
            genderBoy.setTextColor(getColor(R.color.white));
            genderBoy.setBackground(getDrawable(R.drawable.lam_background_raised_secondary_corner_8));
            genderBoy.setTypeface(null, Typeface.BOLD);

            genderGirl.setTextColor(getColor(R.color.light_secondary));
            genderGirl.setBackground(getDrawable(R.drawable.lam_background_outline_secondary_corner_8));
            genderGirl.setTypeface(null, Typeface.NORMAL);

            currentAccount.setGender("male");
        });

        genderGirl.setOnClickListener(view -> {
            genderGirl.setTextColor(getColor(R.color.white));
            genderGirl.setBackground(getDrawable(R.drawable.lam_background_raised_secondary_corner_8));
            genderGirl.setTypeface(null, Typeface.BOLD);

            genderBoy.setTextColor(getColor(R.color.light_secondary));
            genderBoy.setBackground(getDrawable(R.drawable.lam_background_outline_secondary_corner_8));
            genderBoy.setTypeface(null, Typeface.NORMAL);

            currentAccount.setGender("female");
        });

        binding.phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String inputPhoneNumber = binding.phoneNumber.getText().toString();
                    // define the regular expression for Vietnamese phone numbers
                    Pattern pattern = Pattern.compile("(\\d{4})(\\d{3})(\\d{3})");
                    Matcher matcher = pattern.matcher(inputPhoneNumber);
                    if (matcher.matches()) {
                        // format the phone number using dots or spaces
                        // String formattedPhoneNumber = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3);
                        String formattedPhoneNumber = matcher.group(1) + " " + matcher.group(2) + " " + matcher.group(3);

                        // print the formatted phone number
                        binding.phoneNumber.setText(formattedPhoneNumber);
                    }

                    binding.phoneNumber.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    return true;
                }
                return false;

            }
        });

    }

    private void fetchingAndSetupData() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            Account fetchedAccountInfo = null;
            boolean isSuccessful = true;

            try {
                fetchedAccountInfo = new FirebaseSupportAccount().fetchingCurrentAccount();
            } catch (IOException e) {
                isSuccessful = false;
            }

            boolean finalIsSuccessful = isSuccessful;
            Account finalFetchedAccountInfo = fetchedAccountInfo;
            handler.post(() -> {
                if (finalIsSuccessful) {

                    currentAccount = finalFetchedAccountInfo;
                    Picasso.get().load(currentAccount.getAvatar()).into(binding.customerAvatar);

                    binding.username.setText(currentAccount.getUsername());
                    binding.email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    if (currentAccount.getGender().equals("male")) {

                        binding.genderBoy.setTextColor(getColor(R.color.white));
                        binding.genderBoy.setBackground(getDrawable(R.drawable.lam_background_raised_secondary_corner_8));
                        binding.genderBoy.setTypeface(null, Typeface.BOLD);

                    } else {

                        binding.genderGirl.setTextColor(getColor(R.color.white));
                        binding.genderGirl.setBackground(getDrawable(R.drawable.lam_background_raised_secondary_corner_8));
                        binding.genderGirl.setTypeface(null, Typeface.BOLD);

                    }

                    binding.phoneNumber.setText(currentAccount.getPhoneNumber());

                    binding.registrationDate.setText(currentAccount.getRegistrationDate());

                    binding.changeAvatarButton.setOnClickListener(view -> {
                        ImagePicker.with(CustomerAccountInfoActivity.this)
                                .crop()	    			//Crop image(Optional), Check Customization for more option
                                .createIntent( intent -> {
                                    openSomeActivityForResult(intent);
                                    return Unit.INSTANCE;
                                });

                    });

                    binding.saveButton.setOnClickListener(view -> {
                        if (getCurrentFocus() != null) {
                            getCurrentFocus().clearFocus();
                        }
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        updateAccountInfoToServer();
                    });


                } else {
                    Toast.makeText(this, "Failed to load account information", Toast.LENGTH_LONG)
                            .show();
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

    }

    private void updateAccountInfoToServer() {

        binding.saveButton.setVisibility(View.GONE);
        binding.progressBarSaveInfor.setVisibility(View.VISIBLE);

        currentAccount.setUsername(binding.username.getText().toString());
        currentAccount.setPhoneNumber(binding.phoneNumber.getText().toString());

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

                    Toast.makeText(CustomerAccountInfoActivity.this, "Update Information Successful", Toast.LENGTH_SHORT)
                            .show();
                    finish();

                } else {
                    Toast.makeText(CustomerAccountInfoActivity.this, "Update information failed", Toast.LENGTH_SHORT)
                            .show();
                }

                binding.saveButton.setVisibility(View.VISIBLE);
                binding.progressBarSaveInfor.setVisibility(View.GONE);
            });

        });

    }

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        mImageUri = data.getData();
                        binding.customerAvatar.setImageURI(mImageUri);
                        updateAvatar();
                    }
                }
            });

    private void updateAvatar() {
        binding.saveButton.setVisibility(View.GONE);
        binding.progressBarSaveInfor.setVisibility(View.VISIBLE);

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
                                    Toast.makeText(CustomerAccountInfoActivity.this, "Update Avatar Successful", Toast.LENGTH_SHORT).show();
                                    currentAccount.setAvatar(String.valueOf(uri));

                                    binding.saveButton.setVisibility(View.VISIBLE);
                                    binding.progressBarSaveInfor.setVisibility(View.GONE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CustomerAccountInfoActivity.this, "Update Avatar Failed", Toast.LENGTH_SHORT).show();

                            binding.saveButton.setVisibility(View.VISIBLE);
                            binding.progressBarSaveInfor.setVisibility(View.GONE);
                        }
                    });
        }
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