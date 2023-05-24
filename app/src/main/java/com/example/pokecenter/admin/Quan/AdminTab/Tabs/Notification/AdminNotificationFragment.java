package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Notification;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminNotification.AdminNotificationAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminNotification.AdminNotification;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.FragmentAdminNotificationBinding;

import java.util.ArrayList;

public class AdminNotificationFragment extends Fragment {

    private Context context;
    private FragmentAdminNotificationBinding binding;
    private ArrayList<AdminNotification> adminNotificationsList;
    private AdminNotificationAdapter adminNotificationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminNotificationBinding.inflate(inflater, container, false);

        context = container.getContext();

        AdminNotification an1 = new AdminNotification("001", "HELLO", "Welcome to app",
                "8:46 PM", "Yowassup", true);
        AdminNotification an2 = new AdminNotification("002", "HELLO", "Welcome to app again",
                "9:00 PM", "Yowassup", false);

        adminNotificationsList = new ArrayList<>();
        adminNotificationsList.add(an1);
        adminNotificationsList.add(an2);


        adminNotificationAdapter = new AdminNotificationAdapter(adminNotificationsList, context, R.layout.quan_admin_notification_item);
        //Add spacing to RecyclerView rvNotifications
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvNotifications.addItemDecoration(itemSpacingDecoration);

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(context));
        binding.rvNotifications.setAdapter(adminNotificationAdapter);

        return binding.getRoot();
    }
}