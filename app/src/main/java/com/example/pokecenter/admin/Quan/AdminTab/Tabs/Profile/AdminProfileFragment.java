package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.FragmentAdminProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;

public class AdminProfileFragment extends Fragment {

    private Context context;
    private Account currentAccount;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FragmentAdminProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false);
        //Get context for fragment
        context = getContext();
        //Initialize a Firebase Storage reference for updating avatar
        mStorageRef = FirebaseStorage.getInstance().getReference("avatar");

        //Fetch and Setup user's info to layout
        fetchingAndSetupData();


        binding.bAdminLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finishAffinity();
            FollowData.hasData = false;
            WishListData.hasData = false;
        });
        return binding.getRoot();
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
                    Picasso.get().load(currentAccount.getAvatar()).into(binding.ivAdminProfileAvatar);

                    binding.tvAdminUsername.setText(currentAccount.getUsername());
                    binding.tvAdminEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    binding.tvAdminPhone.setText(currentAccount.getPhoneNumber());

                    binding.ibAdminChangeAvatar.setOnClickListener(view -> {
                        ImagePicker.with((Activity) context)
                                .crop()	    			//Crop image(Optional), Check Customization for more option
                                .galleryOnly()
                                .createIntent( intent -> {
                                    openGetImageActivityForResult(intent);
                                    return Unit.INSTANCE;
                                });

                    });
//
//                    binding.saveButton.setOnClickListener(view -> {
//
//                        if (getCurrentFocus() != null) {
//                            getCurrentFocus().clearFocus();
//                        }
//                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//                        updateAccountInfoToServer();
//                    });

                    binding.pbAdminProfile.setVisibility(View.INVISIBLE);
                }
            });
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
        binding.pbAdminProfile.setVisibility(View.VISIBLE);
        binding.clAdminProfile.setVisibility(View.INVISIBLE);

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
                                    Toast.makeText(context, "Update Avatar Successful", Toast.LENGTH_SHORT).show();
                                    currentAccount.setAvatar(String.valueOf(uri));
                                    binding.pbAdminProfile.setVisibility(View.INVISIBLE);
                                    binding.clAdminProfile.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Update Avatar Failed", Toast.LENGTH_SHORT).show();
                            binding.pbAdminProfile.setVisibility(View.INVISIBLE);
                            binding.clAdminProfile.setVisibility(View.VISIBLE);
                        }
                    });

        }
    }

}