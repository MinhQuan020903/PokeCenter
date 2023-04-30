package com.example.pokecenter.customer.lam.CustomerTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.customer.lam.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
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


        /* Logout Logic: Hoàng Lâm created on 28/04/2023 */
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