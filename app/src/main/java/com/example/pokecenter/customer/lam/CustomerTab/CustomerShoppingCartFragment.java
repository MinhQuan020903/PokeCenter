package com.example.pokecenter.customer.lam.CustomerTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentCustomerShoppingCartBinding;

public class CustomerShoppingCartFragment extends Fragment {

    private FragmentCustomerShoppingCartBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCustomerShoppingCartBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}