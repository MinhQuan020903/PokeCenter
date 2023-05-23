package com.example.pokecenter.customer.lam.CustomerTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.customer.lam.Model.order.OrderAdapter;
import com.example.pokecenter.databinding.FragmentCustomerOrdersBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerOrdersFragment extends Fragment {

    private FragmentCustomerOrdersBinding binding;
    private RecyclerView rcvOrders;
    private OrderAdapter orderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCustomerOrdersBinding.inflate(inflater, container, false);

        rcvOrders = binding.rcvOrders;
        fetchingAndSetupData();

        return binding.getRoot();
    }

    private void fetchingAndSetupData() {

        orderAdapter = new OrderAdapter(getActivity(), new ArrayList<>());
        rcvOrders.setAdapter(orderAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<Order> fetchedOrders = new ArrayList<>();
            boolean isSuccess = true;

            try {
                fetchedOrders = new FirebaseSupportCustomer().fetchingOrdersData();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            List<Order> finalFetchedOrders = fetchedOrders;
            handler.post(() -> {

                if (finalIsSuccess) {

                    if (finalFetchedOrders.size() > 0) {

                        binding.informText.setVisibility(View.INVISIBLE);
                        orderAdapter.setData(finalFetchedOrders);

                    } else {
                        binding.rcvOrders.setVisibility(View.INVISIBLE);
                    }

                } else {

                    binding.informText.setText("Failed to load Orders data");
                    binding.rcvOrders.setVisibility(View.INVISIBLE);
                }

                binding.progressBar.setVisibility(View.INVISIBLE);

            });
        });

    }
}