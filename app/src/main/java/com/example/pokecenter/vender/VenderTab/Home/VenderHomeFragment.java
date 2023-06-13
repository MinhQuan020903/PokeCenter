package com.example.pokecenter.vender.VenderTab.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.FragmentVenderHomeBinding;
import com.example.pokecenter.vender.VenderTab.Home.Parcel.ParcelMainActivity;
import com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity;
import com.example.pokecenter.vender.VenderTab.Home.Profile.VenderProfileFragment;
import com.example.pokecenter.vender.VenderTab.VenderNotificationsFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VenderHomeFragment extends Fragment {
    private FragmentVenderHomeBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private OnFragmentChangeListener fragmentChangeListener;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    List<Account> accounts = new ArrayList<>();
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
//            Intent intent = new Intent(getActivity(), VenderNotificationActivity.class);
//            startActivity(intent);
        });
        binding.parcelFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ParcelMainActivity.class);
            startActivity(intent);
        });
        binding.voucherFunction.setOnClickListener(view -> {

//            fragmentChangeListener.onFragmentChange(new VenderNotificationsFragment());
            Intent intent = new Intent(getActivity(), VenderNotificationActivity.class);
            startActivity(intent);
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