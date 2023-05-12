package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.ActivityCustomerAccountInfoBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;

public class CustomerAccountInfoActivity extends AppCompatActivity {

    private ActivityCustomerAccountInfoBinding binding;

    private Account currentAccount;

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




//        binding.backButton.setOnClickListener(view -> {
//            finish();
//        });
//
//        binding.userProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("SIUUU", "Mucha gracias aficion, esra es da vosotros, SIUUUUUUUUUUUUU");
//                ImagePicker.with(CustomerAccountInfoActivity.this)
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(150)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(150, 150)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .galleryOnly()
//                        .createIntent( intent -> {
//                            openSomeActivityForResult(intent);
//                            return Unit.INSTANCE;
//                        });
//
//            }
//        });
//

    }

    private void fetchingAndSetupData() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {


            handler.post(() -> {


                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

    }
//
//    public void openSomeActivityForResult(Intent intent) {
//        someActivityResultLauncher.launch(intent);
//    }
//
//    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        assert data != null;
//                        Uri uri = data.getData();
//                        binding.UserProfileImage.setImageURI(uri);
//                        binding.UserProfileImage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//                    }
//                }
//            });

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