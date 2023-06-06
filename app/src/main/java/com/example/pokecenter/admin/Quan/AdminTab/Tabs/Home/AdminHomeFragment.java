package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.OrdersManagement.AdminOrdersManagementActivity;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement.AdminProductsManagementActivity;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.StoreStatistic.AdminStoreStatisticActivity;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.AdminUsersManagementActivity;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.FragmentAdminHomeBinding;
import com.squareup.picasso.Picasso;

public class AdminHomeFragment extends Fragment {

    private Account currentAccount;
    private FragmentAdminHomeBinding binding;
    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public AdminHomeFragment(Account currentAccount) {
        this.currentAccount = currentAccount;
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



        if (currentAccount != null) {
            Picasso.get().load(currentAccount.getAvatar()).into(binding.ivAdminAvatar);
            binding.tvWelcomeAdmin.setText("Welcome, "+ currentAccount.getUsername() + " !");
        }

        binding.cvAdminStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminStoreStatisticActivity.class));
            }
        });
        binding.cvAdminOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminOrdersManagementActivity.class));
            }
        });
        binding.cvAdminProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminProductsManagementActivity.class));
            }
        });
        binding.cvAdminUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminUsersManagementActivity.class));
            }
        });
        return binding.getRoot();
    }
}