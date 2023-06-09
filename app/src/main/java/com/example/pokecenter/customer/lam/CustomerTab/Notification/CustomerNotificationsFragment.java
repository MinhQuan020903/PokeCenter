package com.example.pokecenter.customer.lam.CustomerTab.Notification;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.customer.lam.Interface.NotificationRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.notification.Notification;
import com.example.pokecenter.customer.lam.Model.notification.NotificationAdapter;
import com.example.pokecenter.databinding.FragmentCustomerNotificationsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerNotificationsFragment extends Fragment implements NotificationRecyclerViewInterface {

    private FragmentCustomerNotificationsBinding binding;

    private List<Notification> myNotifications = new ArrayList<>();

    public static List<Notification> myNotificationsPromotion = new ArrayList<>();

    public static List<Notification> myNotificationsFromPokeCenter = new ArrayList<>();

    public static List<Notification> myNotificationsOrders = new ArrayList<>();

    private ListView lvNotifications;
    private NotificationAdapter notificationAdapter;

    private Dialog dialog;
    private boolean isFirst = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CustomerActivity.setEnableBottomNavigation(false);

        myNotificationsPromotion.clear();
        myNotificationsFromPokeCenter.clear();
        myNotificationsOrders.clear();
        isFirst = true;

        binding = FragmentCustomerNotificationsBinding.inflate(inflater, container, false);

        binding.promotion.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), PromotionNotificationsActivity.class));
        });

        binding.fromPokecenter.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), FromPokeCenterNotificationsActivity.class));
        });

        binding.ordersProgress.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), OrdersProgressNotificationsActivity.class));
        });

        /* Set up all notifications */
        setUpAllNotifications();

        setUpPopupDialog();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            updateBadge();
            notificationAdapter.notifyDataSetChanged();
        }
        isFirst = false;
    }

    private void setUpPopupDialog() {

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.lam_dialog_notification);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        /*
         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); khá quan trọng. THỬ BỎ ĐI SẼ HIỂU =)))
         nếu bỏ dòng này đi thì các thuộc tính của LinearLayout mẹ trong todo_dialog.xml sẽ mất hết
         thay vào đó sẽ là thuộc tính mặc định của dialog, còn nội dung vẫn giữ nguyên
         */
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            dialog.dismiss();
        });
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
                    myNotifications.sort(Comparator.comparing(Notification::getSentDate).reversed());
                    notificationAdapter = new NotificationAdapter(getActivity(), myNotifications, this);
                    lvNotifications.setAdapter(notificationAdapter);

                    myNotifications.forEach(notification -> {

                        switch (notification.getType()) {
                            case "promotion":
                                myNotificationsPromotion.add(notification);
                                break;
                            case "fromPokeCenter":
                                myNotificationsFromPokeCenter.add(notification);
                                break;
                            case "orders":
                                myNotificationsOrders.add(notification);
                                break;
                        }
                    });

                    if (myNotificationsPromotion.size() > 0) {
                        binding.contentPromotion.setText(myNotificationsPromotion.get(0).getContent().replace("\\n", System.getProperty("line.separator")));
                    } else {
                        binding.promotion.setVisibility(View.GONE);
                    }

                    if (myNotificationsFromPokeCenter.size() > 0) {
                        binding.contentFromPokecenter.setText(myNotificationsFromPokeCenter.get(0).getContent().replace("\\n", System.getProperty("line.separator")));
                    } else {
                        binding.fromPokecenter.setVisibility(View.GONE);
                    }

                    if (myNotificationsOrders.size() > 0) {
                        binding.contentOrders.setText(myNotificationsOrders.get(0).getContent().replace("\\n", System.getProperty("line.separator")));
                    } else {
                        binding.ordersProgress.setVisibility(View.GONE);
                    }

                    updateBadge();

                } else {
                    Toast.makeText(getActivity(), "Failed to connect server", Toast.LENGTH_SHORT).show();
                }

                binding.progressBar.setVisibility(View.INVISIBLE);

                CustomerActivity.setEnableBottomNavigation(true);

            });
        });

    }

    private void updateBadge() {

        int countUnreadPromotion = 0;

        for (int i = 0 ; i < myNotificationsPromotion.size(); ++i) {
            if (!myNotificationsPromotion.get(i).isRead()) {
                countUnreadPromotion++;
            }
        }

        if (countUnreadPromotion > 0) {

            binding.unreadCountPromotion.setText(String.valueOf(countUnreadPromotion));

        } else {
            binding.unreadPromotion.setVisibility(View.GONE);
        }

        /* -------------------------------------------------------------------------------------- */

        int countUnreadFromPokeCenter = 0;

        for (int i = 0 ; i < myNotificationsFromPokeCenter.size(); ++i) {
            if (!myNotificationsFromPokeCenter.get(i).isRead()) {
                countUnreadFromPokeCenter++;
            }
        }

        if (countUnreadFromPokeCenter > 0) {

            binding.unreadCountFromPokecenter.setText(String.valueOf(countUnreadFromPokeCenter));

        } else {
            binding.unreadFromPokecenter.setVisibility(View.GONE);
        }

        int countUnreadOrders = 0;

        for (int i = 0 ; i < myNotificationsOrders.size(); ++i) {
            if (!myNotificationsOrders.get(i).isRead()) {
                countUnreadOrders++;
            }
        }

        if (countUnreadOrders > 0) {

            binding.unreadCountOrders.setText(String.valueOf(countUnreadOrders));

        } else {
            binding.unreadOrders.setVisibility(View.GONE);
        }

    }

    @Override
    public void onNotificationItemClick(int position) {

        Notification notification = myNotifications.get(position);

        TextView title = dialog.findViewById(R.id.notification_title);
        title.setText(notification.getTitle());

        TextView content = dialog.findViewById(R.id.notification_content);
        content.setText(notification.getContent().replace("\\n", System.getProperty("line.separator")));

        TextView sentDate = dialog.findViewById(R.id.sentDate);
        sentDate.setText(notification.getSentDateString());


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
            notificationAdapter.notifyDataSetChanged();

            updateBadge();
        }

    }
}