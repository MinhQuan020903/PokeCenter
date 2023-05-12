package com.example.pokecenter.customer.lam.CustomerTab.Notification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.NotificationRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.notification.Notification;
import com.example.pokecenter.databinding.ActivityPromotionNotificationsBinding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PromotionNotificationsActivity extends AppCompatActivity implements NotificationRecyclerViewInterface {

    private ActivityPromotionNotificationsBinding binding;

    private List<Notification> myNotificationsPromotion = CustomerNotificationsFragment.myNotificationsPromotion;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPromotionNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    public void onNotificationItemClick(int position) {

        Notification notification = myNotificationsPromotion.get(position);

        TextView title = dialog.findViewById(R.id.notification_title);
        title.setText(notification.getTitle());

        TextView content = dialog.findViewById(R.id.notification_content);
        content.setText(notification.getContent().replace("\\n", System.getProperty("line.separator")));

        TextView sentDate = dialog.findViewById(R.id.sentDate);
        sentDate.setText(notification.getSentDate());


        dialog.show();

        if (!notification.isRead()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    new FirebaseSupportCustomer().changeStatusNotification(notification.getId());
                } catch (IOException e) {

                }
            });

            notification.setRead(true);
            // notificationAdapter.notifyDataSetChanged();

        }

    }
}