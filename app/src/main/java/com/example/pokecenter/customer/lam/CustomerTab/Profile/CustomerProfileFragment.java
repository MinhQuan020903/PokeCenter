package com.example.pokecenter.customer.lam.CustomerTab.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.customer.lam.CustomerTab.Profile.ProfileActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.ProfileActivity.MyAddressesActivity;
import com.example.pokecenter.databinding.FragmentCustomerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerProfileFragment extends Fragment {

    private FragmentCustomerProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerProfileBinding.inflate(inflater, container, false);


        binding.accountInformationItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CustomerAccountInfoActivity.class));
        });

        binding.myAddressesItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), MyAddressesActivity.class));
        });


        binding.logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(getActivity(), SignInActivity.class));
        });

        return binding.getRoot();
    }

    private void replaceFragment(Fragment selectedFragment) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}