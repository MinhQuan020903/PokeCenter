package com.example.pokecenter.vender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityVenderBinding;
import com.example.pokecenter.vender.VenderTab.Home.VenderHomeFragment;
import com.example.pokecenter.vender.VenderTab.Chat.VenderChatFragment;
import com.example.pokecenter.vender.VenderTab.VenderNotificationsFragment;
import com.example.pokecenter.vender.VenderTab.VenderOrderFragment;
import com.example.pokecenter.vender.VenderTab.Home.Profile.VenderProfileFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VenderActivity extends AppCompatActivity implements VenderHomeFragment.OnFragmentChangeListener{

    private ActivityVenderBinding binding;

    private int selectedFragment = R.id.venderHomeNav;
    public static BottomNavigationView venderBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenderBinding.inflate(getLayoutInflater());

        venderBottomNavigationView = binding.bottomNavView;

//        BadgeDrawable badgeDrawable = venderBottomNavigationView.getOrCreateBadge(R.id.venderNotificationNav);
//        badgeDrawable.setVisible(true);
//        badgeDrawable.setNumber(4);
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

        venderBottomNavigationView.setSelectedItemId(getIntent().getIntExtra("targetedFragment", R.id.venderHomeNav));
        setContentView(binding.getRoot());
    }
    private void replaceFragment(Fragment selectedFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentVender, selectedFragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onFragmentChange(Fragment fragment) {
        // Replace the current fragment with the new fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentVender, fragment)
                .commit();
    }
}