package com.example.pokecenter.vender.VenderTab.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.databinding.FragmentVenderHomeBinding;
import com.example.pokecenter.vender.VenderTab.Chat.VenderChatFragment;
import com.example.pokecenter.vender.VenderTab.Home.Parcel.ParcelMainActivity;
import com.example.pokecenter.vender.VenderTab.VenderNotificationsFragment;
import com.example.pokecenter.vender.VenderTab.Home.Profile.VenderProfileFragment;
import com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity;
import com.squareup.picasso.Picasso;

public class VenderHomeFragment extends Fragment {
    private FragmentVenderHomeBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private OnFragmentChangeListener fragmentChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Check if the parent activity implements the OnFragmentChangeListener interface
        if (context instanceof OnFragmentChangeListener) {
            fragmentChangeListener = (OnFragmentChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentChangeListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVenderHomeBinding.inflate(inflater, container, false);
        if (VenderProfileFragment.currentVender.getAvatar() != null)
            Picasso.get().load(VenderProfileFragment.currentVender.getAvatar()).into(binding.VenderProfileImage);
        else
            Picasso.get().load("https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png").into(binding.VenderProfileImage);
        binding.StatisticsFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), VenderStatisticsActivity.class);
            startActivity(intent);
        });
        binding.ProductFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), VenderProductActivity.class);
            startActivity(intent);
        });
        binding.NotificationFunction.setOnClickListener(view -> {
            fragmentChangeListener.onFragmentChange(new VenderNotificationsFragment());
        });
        binding.chatFunction.setOnClickListener(view -> {
            fragmentChangeListener.onFragmentChange(new VenderChatFragment());
        });
        binding.parcelFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ParcelMainActivity.class);
            startActivity(intent);
        });
        binding.voucherFunction.setOnClickListener(view -> {

        });

        return binding.getRoot();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        fragmentChangeListener = null;
    }

    public interface OnFragmentChangeListener {
        void onFragmentChange(Fragment fragment);
    }

}