package com.example.pokecenter.customer.lam.CustomerTab.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.WishListActivity;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.FragmentCustomerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class CustomerProfileFragment extends Fragment {

    private FragmentCustomerProfileBinding binding;

    public static Account currentAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerProfileBinding.inflate(inflater, container, false);

        binding.username.setText(currentAccount.getUsername());
        Picasso.get().load(currentAccount.getAvatar()).into(binding.customerAvatar);

        binding.accountInformationItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CustomerAccountInfoActivity.class));
        });

        binding.myAddressesItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), MyAddressesActivity.class));
        });

        binding.wishListItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), WishListActivity.class));
        });


        binding.logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finishAffinity();

            FollowData.hasData = false;
            WishListData.hasData = false;
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}