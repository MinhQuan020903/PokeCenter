package com.example.pokecenter.customer.lam.CustomerTab;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.ProductDetailActivity;
import com.example.pokecenter.customer.lam.Interface.CartRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.address.AddressAdapter;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.cart.CartAdapter;
import com.example.pokecenter.databinding.FragmentCustomerShoppingCartBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerShoppingCartFragment extends Fragment implements CartRecyclerViewInterface {

    private FragmentCustomerShoppingCartBinding binding;

    private List<Cart> myCarts = new ArrayList<>();

    private CartAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCustomerShoppingCartBinding.inflate(inflater, container, false);

        /* Set Address ListView */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.rcvCarts.setLayoutManager(linearLayoutManager);

        cartAdapter = new CartAdapter(getActivity(), myCarts, this);
        binding.rcvCarts.setAdapter(cartAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Cart> fetchedCartsData;
            try {
                fetchedCartsData = new FirebaseSupportCustomer().fetchingAllCarts();
            } catch (IOException e) {
                fetchedCartsData = null;
            }
            List<Cart> finalFetchedCartsData = fetchedCartsData;
            handler.post(() -> {
                if (finalFetchedCartsData != null) {
                    finalFetchedCartsData.forEach(cart -> {
                        myCarts.add(cart);
                        cartAdapter.setData(myCarts);
                    });
                } else {
                    binding.informFail.setVisibility(View.VISIBLE);
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCartItemClick(int position) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra("product object", myCarts.get(position).getProduct());
        startActivity(intent);
    }
}