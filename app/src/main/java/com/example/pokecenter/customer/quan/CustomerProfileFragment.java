package com.example.pokecenter.customer.quan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentCustomerProfileBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerProfileFragment extends Fragment {

    private FragmentCustomerProfileBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerProfileFragment newInstance(String param1, String param2) {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
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