package com.example.pokecenter.customer.quan.CustomerProfileTab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
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


        binding.AccountInformationButton.setOnClickListener(view -> {
            NavHostFragment.findNavController(CustomerProfileFragment.this)
                    .navigate(R.id.action_customerFragment_to_customerProfileAccountInfoFragment);
        });

        /* Logout Logic: Hoàng Lâm created on 28/04/2023 */
        binding.LogoutButton.setOnClickListener(view -> {
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