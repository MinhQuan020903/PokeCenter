package com.example.pokecenter.vender.Ninh.VenderTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentVenderBinding;
import com.example.pokecenter.vender.Ninh.VenderTab.Home.VenderHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenderFragment extends Fragment {

    private FragmentVenderBinding binding;

    public static BottomNavigationView customerBottomNavigationView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VenderFragment newInstance(String param1, String param2) {
        VenderFragment fragment = new VenderFragment();
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
        binding = FragmentVenderBinding.inflate(inflater, container, false);

        customerBottomNavigationView = binding.bottomNavView;

        // Move between fragments
        customerBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.venderPaymentNav:
                    replaceFragment(new VenderPaymentFragment());
                    break;
                case R.id.venderNotificationNav:
                    replaceFragment(new VenderNotificationsFragment());
                    break;
                case R.id.venderHomeNav:
                    replaceFragment(new VenderHomeFragment());
                    break;
                case R.id.venderSupportNav:
                    replaceFragment(new VenderSupportFragment());
                    break;
                case R.id.venderProfileNav:
                    replaceFragment(new VenderProfileFragment());
                    break;
            }
            return true;
        });


        customerBottomNavigationView.setSelectedItemId(R.id.customerHomeFragment);

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
        fragmentTransaction.replace(R.id.fragmentVender, selectedFragment);
        fragmentTransaction.commit();
    }
}