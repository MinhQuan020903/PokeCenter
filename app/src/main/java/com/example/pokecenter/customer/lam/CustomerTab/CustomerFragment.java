package com.example.pokecenter.customer.lam.CustomerTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.quan.CustomerProfileFragment;
import com.example.pokecenter.databinding.FragmentCustomerBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerFragment extends Fragment {
    private FragmentCustomerBinding binding;

    private int selectedFragment = R.id.customerHomeFragment;
    public static BottomNavigationView customerBottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerBinding.inflate(inflater, container, false);

        customerBottomNavigationView = binding.bottomNavView;

        Fragment home = new CustomerHomeFragment();
        Fragment orders = new CustomerOrdersFragment();
        Fragment notifications = new CustomerNotificationsFragment();
        Fragment shoppingCart = new CustomerShoppingCartFragment();
        Fragment profile = new CustomerProfileFragment();

        // Move between fragments
        customerBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.customerHomeFragment:
                    selectedFragment = R.id.customerHomeFragment;
                    replaceFragment(home);
                    break;
                case R.id.customerOrdersFragment:
                    selectedFragment = R.id.customerOrdersFragment;
                    replaceFragment(orders);
                    break;
                case R.id.customerNotificationsFragment:
                    selectedFragment = R.id.customerNotificationsFragment;
                    replaceFragment(notifications);
                    break;
                case R.id.customerShoppingCardFragment:
                    selectedFragment = R.id.customerShoppingCardFragment;
                    replaceFragment(shoppingCart);
                    break;
                case R.id.customerProfileFragment:
                    selectedFragment = R.id.customerProfileFragment;
                    replaceFragment(profile);
                    break;
            }
            return true;
        });

        customerBottomNavigationView.setSelectedItemId(selectedFragment);

        return binding.getRoot();
    }

    private void replaceFragment(Fragment selectedFragment) {

        /*
        Notes:
            getFragmentManager() was deprecated in API level 28.

            we will use getChildFragmentManager() Return a private FragmentManager for placing and managing Fragments inside of this Fragment.

            we will use getParentFragmentManager() Return the FragmentManager for interacting with fragments associated with this fragment's activity.

            so, if you deal with fragments inside a fragment you will use the first one and if you deal with fragments inside an activity you will use the second one.

            you can find them here package androidx.fragment.app;
         */

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentCustomer, selectedFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}