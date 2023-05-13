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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VenderFragment extends Fragment {

    private FragmentVenderBinding binding;
    private int selectedFragment = R.id.venderHomeNav;
    public static BottomNavigationView venderBottomNavigationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVenderBinding.inflate(inflater, container, false);

        venderBottomNavigationView = binding.bottomNavView;

        BadgeDrawable badgeDrawable = venderBottomNavigationView.getOrCreateBadge(R.id.venderNotificationNav);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(4);
        // Move between fragments
        venderBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.venderHomeNav:
                    selectedFragment = R.id.venderHomeNav;
                    replaceFragment(new VenderHomeFragment());
                    break;
                case R.id.venderOrderNav:
                    selectedFragment = R.id.venderOrderNav;
                    replaceFragment(new VenderOrderFragment());
                    break;
                case R.id.venderNotificationNav:
                    selectedFragment = R.id.venderNotificationNav;
                    replaceFragment(new VenderNotificationsFragment());
                    break;
                case R.id.venderChatNav:
                    selectedFragment = R.id.venderChatNav;
                    replaceFragment(new VenderChatFragment());
                    break;
                case R.id.venderProfileNav:
                    selectedFragment = R.id.venderProfileNav;
                    replaceFragment(new VenderProfileFragment());
                    break;
            }
            return true;
        });
        venderBottomNavigationView.setSelectedItemId(selectedFragment);

        return binding.getRoot();
    }
    private void replaceFragment(Fragment selectedFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentVender, selectedFragment);
        fragmentTransaction.commit();
    }
}