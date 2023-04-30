package com.example.pokecenter.customer.lam;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityCustomerAccountInfoBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;

public class CustomerAccountInfoActivity extends AppCompatActivity {

    private ActivityCustomerAccountInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerAccountInfoBinding.inflate(getLayoutInflater());

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.userProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SIUUU", "Mucha gracias aficion, esra es da vosotros, SIUUUUUUUUUUUUU");
                ImagePicker.with(CustomerAccountInfoActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(150)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(150, 150)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .galleryOnly()
                        .createIntent( intent -> {
                            openSomeActivityForResult(intent);
                            return Unit.INSTANCE;
                        });

            }
        });

        setContentView(binding.getRoot());
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
                        Uri uri = data.getData();
                        binding.UserProfileImage.setImageURI(uri);
                        binding.UserProfileImage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}