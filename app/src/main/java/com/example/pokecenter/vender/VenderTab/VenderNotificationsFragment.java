package com.example.pokecenter.vender.VenderTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pokecenter.databinding.FragmentVenderNotificationsBinding;

public class VenderNotificationsFragment extends Fragment {

    private FragmentVenderNotificationsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentVenderNotificationsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}