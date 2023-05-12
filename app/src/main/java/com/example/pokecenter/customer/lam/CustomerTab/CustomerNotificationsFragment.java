package com.example.pokecenter.customer.lam.CustomerTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.NotificationRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.notification.Notification;
import com.example.pokecenter.customer.lam.Model.notification.NotificationAdapter;
import com.example.pokecenter.databinding.FragmentCustomerNotificationsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerNotificationsFragment extends Fragment implements NotificationRecyclerViewInterface {

    private FragmentCustomerNotificationsBinding binding;

    private List<Notification> myNotifications = new ArrayList<>();

    private ListView lvNotifications;
    private NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerNotificationsBinding.inflate(inflater, container, false);

        /* Set up all notifications */
        setUpAllNotifications();

        return binding.getRoot();
    }

    private void setUpAllNotifications() {

        lvNotifications = binding.lvNotifications;

        ExecutorService executor = Executors.newCachedThreadPool();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            List<Notification> fetchedNotifications = null;
            try {
                fetchedNotifications = new FirebaseSupportCustomer().fetchingAllNotifications();
            } catch (IOException e) {
                isSuccessful = false;
            }

            boolean finalIsSuccessful = isSuccessful;
            List<Notification> finalFetchedNotifications = fetchedNotifications;
            handler.post(() -> {

                if (finalIsSuccessful) {

                    myNotifications = finalFetchedNotifications;
                    notificationAdapter = new NotificationAdapter(getActivity(), myNotifications, this);
                    lvNotifications.setAdapter(notificationAdapter);


                } else {
                    Toast.makeText(getActivity(), "Failed to connect server", Toast.LENGTH_SHORT).show();
                }

                binding.progressBar.setVisibility(View.INVISIBLE);

            });
        });

    }

    @Override
    public void onNotificationItemClick(int position) {

    }
}