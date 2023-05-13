package com.example.pokecenter.vender.Ninh.VenderTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.BinderThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.FragmentCustomerProfileBinding;
import com.example.pokecenter.databinding.FragmentVenderProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class VenderProfileFragment extends Fragment {
FragmentVenderProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVenderProfileBinding.inflate(inflater, container, false);
        binding.btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finishAffinity();
            FollowData.hasData = false;
            WishListData.hasData = false;
        });
        return inflater.inflate(R.layout.fragment_vender_profile, container, false);

    }
}