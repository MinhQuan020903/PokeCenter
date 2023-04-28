package com.example.pokecenter.customer.quan.CustomerProfileTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentCustomerProfileBinding;

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
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();

            /*
            Cách 1: đã áp dụng cho bên LoginFragment

            Trong navigation/nav_app.xml:
            Add 1 action nối từ CustomerFragment tới LoginFrament (vì ProfileFragment nằm trong CustomerFragment)
            => Tìm đến action với id này: action_customerFragment_to_loginFragment:
            thêm 2 thuộc tính:
            app:popUpTo="@id/nav_app"
            app:popUpToInclusive="true"

            Sau đó add code trong logic của Logout Button
            NavHostFragment.findNavController(CustomerProfileFragment.this)
                    .navigate(R.id.action_customerFragment_to_loginFragment);
             */

            /* Cách 2: Học từ coursera */
            NavHostFragment.findNavController(this)
                    .navigate(R.id.loginFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_app, true).build());
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