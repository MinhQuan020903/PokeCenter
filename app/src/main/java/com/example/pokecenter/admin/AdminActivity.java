package com.example.pokecenter.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pokecenter.admin.Quan.AdminTab.Home.AdminHomeFragment;
import com.example.pokecenter.admin.Quan.AdminTab.Notification.AdminNotificationFragment;
import com.example.pokecenter.admin.Quan.AdminTab.Profile.AdminProfileFragment;
import com.example.pokecenter.admin.Quan.AdminTab.Support.AdminSupportFragment;
import com.example.pokecenter.databinding.ActivityAdminBinding;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.pokecenter.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    public static BottomNavigationView adminBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.light_canvas));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        adminBottomNavigationView = binding.adminBottomNavView;

        Fragment home = new AdminHomeFragment();
        Fragment support = new AdminSupportFragment();
        Fragment notification = new AdminNotificationFragment();
        Fragment profile = new AdminProfileFragment();

        // Move between fragments
        adminBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.adminHomeFragment:
                    replaceFragment(home);
                    break;
                case R.id.adminSupportFragment:
                    replaceFragment(support);
                    break;
                case R.id.adminNotificationFragment:
                    replaceFragment(notification);
                    break;
                case R.id.adminProfileFragment:
                    replaceFragment(profile);
                    break;
            }
            return true;
        });

        /*
        Lấy giá trị targetedFragment để check xem ở một nơi nào đó trong app
        mà move tới CustomerActivity với Fragment mong muốn nào

        Set HomeFragment is default

        Đồng thời lúc này customerBottomNavigationView.setOnItemSelectedListener() ở dòng 51
        cũng sẽ được trigger
        => Thực thi lệnh "replaceFragment(new CustomerHomePlaceholderFragment());"
        => Nội dung page cũng sẽ thay đổi theo
         */

        adminBottomNavigationView.setSelectedItemId(getIntent().getIntExtra("targetedFragment", R.id.adminHomeFragment));
        setContentView(binding.getRoot());
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentAdmin, selectedFragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}