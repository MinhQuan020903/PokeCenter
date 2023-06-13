package com.example.pokecenter.vender.VenderTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pokecenter.databinding.FragmentVenderOrderBinding;
import com.example.pokecenter.vender.VenderTab.Home.Parcel.CompletedOrderActivity;
import com.example.pokecenter.vender.VenderTab.Home.Parcel.PackagedOrdersActivity;
import com.example.pokecenter.vender.VenderTab.Home.Parcel.ReceiveOrderActivity;

public class VenderOrderFragment extends Fragment {
    FragmentVenderOrderBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVenderOrderBinding.inflate(inflater, container, false);


        binding.receiveOrder.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), ReceiveOrderActivity.class));
        });

        binding.packagedOrder.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), PackagedOrdersActivity.class));
        });
        binding.CompleteOrder.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CompletedOrderActivity.class));
        });
        return binding.getRoot();
    }
}