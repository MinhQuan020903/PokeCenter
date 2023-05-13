package com.example.pokecenter.vender.Ninh.VenderTab.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentVenderHomeBinding;

public class VenderHomeFragment extends Fragment {
private FragmentVenderHomeBinding binding;


    public VenderHomeFragment() {
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
        binding=FragmentVenderHomeBinding.inflate(inflater, container, false);

        binding.StatisticsFunction.setOnClickListener(view -> {

            NavHostFragment.findNavController(VenderHomeFragment.this)
                    .navigate(R.id.action_venderHomeFragment_to_venderStatisticsFragment);
        });
        return binding.getRoot();
    }
}