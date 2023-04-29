package com.example.pokecenter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pokecenter.vender.Ninh.VenderTab.Home.VenderHomeFragment;
import com.example.pokecenter.vender.Ninh.VenderTab.Home.VenderStatisticsFragment;

public class MainActivity extends AppCompatActivity {
    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thay vì setContentView, sử dụng FragmentManager để hiển thị fragment đầu tiên
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new VenderStatisticsFragment());
        fragmentTransaction.commit();
    }
}