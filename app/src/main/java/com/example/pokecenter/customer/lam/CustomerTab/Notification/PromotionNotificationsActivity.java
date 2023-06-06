package com.example.pokecenter.customer.lam.CustomerTab.Notification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.NotificationRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.notification.Notification;
import com.example.pokecenter.customer.lam.Model.notification.NotificationAdapter;
import com.example.pokecenter.databinding.ActivityPromotionNotificationsBinding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PromotionNotificationsActivity extends AppCompatActivity implements NotificationRecyclerViewInterface {

    private ActivityPromotionNotificationsBinding binding;

    private List<Notification> myNotificationsPromotion = CustomerNotificationsFragment.myNotificationsPromotion;

    private NotificationAdapter notificationAdapter;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Promotion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityPromotionNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notificationAdapter = new NotificationAdapter(this, myNotificationsPromotion, this);
        binding.lvNotifications.setAdapter(notificationAdapter);

        setUpPopupDialog();

    }

    private void setUpPopupDialog() {

        dialog = new Dialog(this);
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

    @Override
    public void onNotificationItemClick(int position) {

        Notification notification = myNotificationsPromotion.get(position);

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

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}