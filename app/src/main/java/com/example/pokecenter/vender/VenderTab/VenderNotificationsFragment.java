package com.example.pokecenter.vender.VenderTab;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.FragmentVenderNotificationsBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Interface.NotificationUpdateListener;
import com.example.pokecenter.vender.Model.Notification.NotificationData;
import com.example.pokecenter.vender.Model.Notification.NotificationDataAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VenderNotificationsFragment extends Fragment {

    private Context context;
    private CompletableFuture<ArrayList<NotificationData>> venderNotificationsList;
    private NotificationDataAdapter venderNotificationAdapter;
    private NotificationUpdateListener notificationUpdateListener;
    private Dialog dialog;
    private FragmentVenderNotificationsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentVenderNotificationsBinding.inflate(inflater, container, false);
        context = container.getContext();

        venderNotificationsList = new CompletableFuture<ArrayList<NotificationData>>();

        venderNotificationsList = new FirebaseSupportVender().fetchingAllNotifications();

        venderNotificationsList.thenAccept(notifications -> {
            NotificationDataAdapter venderNotificationAdapter = new NotificationDataAdapter(notifications, context, R.layout.quan_admin_notification_item, this);
            // Sử dụng venderNotificationAdapter ở đây

            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
            ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
            binding.rvNotifications.addItemDecoration(itemSpacingDecoration);

            binding.rvNotifications.setLayoutManager(new LinearLayoutManager(context));
            binding.rvNotifications.setAdapter(venderNotificationAdapter);
        });


        //Add spacing to RecyclerView rvNotifications


        setUpPopupDialog();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onNotificationItemClick(int position) {

        venderNotificationsList.thenAccept(notifications -> {
            if (position >= 0 && position < notifications.size()) {
                NotificationData notification = notifications.get(position);

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
                            Tasks.await(new FirebaseSupportVender().changeStatusNotification(notification.getId())
                                    .addOnSuccessListener(unused -> {
                                        notification.setRead(true);

                                        if(venderNotificationAdapter != null) {
                                            venderNotificationAdapter.notifyDataSetChanged();
                                        }
                                        else {
                                            venderNotificationAdapter = new NotificationDataAdapter(notifications, context, R.layout.quan_admin_notification_item, this);
                                            binding.rvNotifications.setAdapter(venderNotificationAdapter);
                                        }

                                        // Gọi callback để thông báo rằng dữ liệu đã được cập nhật
                                        if (notificationUpdateListener != null) {
                                            notificationUpdateListener.onNotificationUpdated();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    }));
                        } catch (Exception e) {
                            // Xử lý lỗi nếu cần
                        }
                    });
                } else {
                    System.out.println("Invalid position");
                }
            }
        });
    }

    private void setUpPopupDialog() {

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.lam_dialog_notification);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    public void setNotificationUpdateListener(NotificationUpdateListener listener) {
        notificationUpdateListener = listener;
    }
}