package com.example.pokecenter.customer.lam.CustomerTab;

import static androidx.core.content.ContextCompat.getColor;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
import com.example.pokecenter.databinding.FragmentCustomerBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerFragment extends Fragment {
    private FragmentCustomerBinding binding;

    private int selectedFragment = R.id.customerHomeFragment;
    public static BottomNavigationView customerBottomNavigationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // if statement checks if the device is running Android Marshmallow (API level 23) or higher,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();

            // change StatusBarColor
            window.setStatusBarColor(getColor(requireContext(), R.color.light_canvas));

            // change color of icons in status bar
            // C1:
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            /*
            C2:
            add: <item name="android:windowLightStatusBar">true</item>
            in the <style name="Theme.PokeCenter" in themes.xml
             */
        }

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

        /*
        Set HomeFragment is default
        Đồng thời lúc này customerBottomNavigationView.setOnItemSelectedListener() ở dòng 80
        cũng sẽ được trigger
        => Thực thi lệnh "replaceFragment(new CustomerHomePlaceholderFragment());"
        => Nội dung page cũng sẽ thay đổi theo
         */

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