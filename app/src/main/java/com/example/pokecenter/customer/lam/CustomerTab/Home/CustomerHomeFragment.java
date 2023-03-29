package com.example.pokecenter.customer.lam.CustomerTab.Home;

import static androidx.core.content.ContextCompat.getColor;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.CustomerFragment;
import com.example.pokecenter.customer.quan.ProfileCustomerFragment;
import com.example.pokecenter.databinding.FragmentCustomerHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHomeFragment extends Fragment {

    private FragmentCustomerHomeBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerHomeFragment newInstance(String param1, String param2) {
        CustomerHomeFragment fragment = new CustomerHomeFragment();
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
        binding = FragmentCustomerHomeBinding.inflate(inflater, container, false);

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

        // Move to Profile Fragment when User click on avatarImage
        binding.avatarImage.setOnClickListener(view -> {
            // Set selectedItem in Bottom Nav Bar
            CustomerFragment.customerBottomNavigationView.setSelectedItemId(R.id.profileCustomerFragment);
        });

        binding.viewAllPokedex.setOnClickListener(view -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_customerFragment_to_customerPokedexFragment);
        });

        return binding.getRoot();
    }
}