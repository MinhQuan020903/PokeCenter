package com.example.pokecenter.admin.AdminTab.Tabs.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.FragmentAdminProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AdminProfileFragment extends Fragment {

    private Context context;
    private Account currentAccount;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FragmentAdminProfileBinding binding;

    public AdminProfileFragment() {
    }
    public AdminProfileFragment(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
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

        if (currentAccount != null) {
            Picasso.get().load(currentAccount.getAvatar()).into(binding.ivAdminProfileAvatar);
            binding.tvAdminUsername.setText(currentAccount.getUsername());
            binding.tvAdminEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            binding.tvAdminPhone.setText(currentAccount.getPhoneNumber());
        }

        binding.bChangeInformation.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AdminAccountInfoActivity.class);
            intent.putExtra("currentAccount", currentAccount);
            startActivity(intent, null);
        });

        binding.pbAdminProfile.setVisibility(View.INVISIBLE);


        binding.bAdminLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finishAffinity();
            FollowData.hasData = false;
            WishListData.hasData = false;
        });
        return binding.getRoot();
    }

}