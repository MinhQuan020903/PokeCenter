package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentAdminHomeBinding;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding binding;
    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}