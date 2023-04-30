package com.example.pokecenter.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pokecenter.vender.Ninh.VenderTab.Home.VenderHomeFragment;
import com.example.pokecenter.vender.Ninh.VenderTab.Home.VenderStatisticsFragment;

import android.os.Bundle;

import com.example.pokecenter.R;

public class AdminActivity extends AppCompatActivity {

    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

//        // Thay vì setContentView, sử dụng FragmentManager để hiển thị fragment đầu tiên
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(android.R.id.content, new VenderStatisticsFragment());
//        fragmentTransaction.commit();
    }
}