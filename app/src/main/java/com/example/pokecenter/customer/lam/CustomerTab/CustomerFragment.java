package com.example.pokecenter.customer.lam.CustomerTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.quan.CustomerProfileTab.CustomerProfileFragment;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerFragment newInstance(String param1, String param2) {
        CustomerFragment fragment = new CustomerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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