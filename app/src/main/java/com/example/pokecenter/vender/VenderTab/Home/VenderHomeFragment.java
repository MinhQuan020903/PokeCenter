package com.example.pokecenter.vender.VenderTab.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.databinding.FragmentVenderHomeBinding;
import com.example.pokecenter.vender.VenderTab.VenderProfileFragment;
import com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity;
import com.squareup.picasso.Picasso;

public class VenderHomeFragment extends Fragment {
private FragmentVenderHomeBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentVenderHomeBinding.inflate(inflater, container, false);
        Picasso.get().load(VenderProfileFragment.currentVender.getAvatar()).into(binding.VenderProfileImage);
        binding.StatisticsFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), VenderStatisticsActivity.class);
            startActivity(intent);
        });
        binding.ProductFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), VenderProductActivity.class);
            startActivity(intent);
        });
        return binding.getRoot();
    }
}