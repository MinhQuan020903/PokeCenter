package com.example.pokecenter.customer.lam.Model.notification;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Interface.NotificationRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.review_product.ReviewProduct;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private Context mContext;
    private List<Notification> mNotifications;

    private NotificationRecyclerViewInterface notificationRecyclerViewInterface;

    public NotificationAdapter(Context context, List<Notification> notifications, NotificationRecyclerViewInterface notificationRecyclerViewInterface) {
        super(context, R.layout.lam_notification_list_item, notifications);

        mContext = context;
        mNotifications = notifications;
        this.notificationRecyclerViewInterface = notificationRecyclerViewInterface;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.lam_notification_list_item, null);

        Notification notification = mNotifications.get(position);

        ImageView image = view.findViewById(R.id.image);
        if (notification.getType().equals("promotion")) {

            image.setImageDrawable(mContext.getDrawable(R.drawable.lam_budew));

        } else {

            image.setImageDrawable(mContext.getDrawable(R.drawable.lam_cubone));

        }

        TextView title = view.findViewById(R.id.notification_title);
        title.setText(notification.getTitle());

        TextView content = view.findViewById(R.id.notification_content);
        content.setText(notification.getContent().replace("\\n", System.getProperty("line.separator")));

        TextView sentDate = view.findViewById(R.id.sentDate);
        sentDate.setText(notification.getSentDate());

        ImageView redCircle = view.findViewById(R.id.red_circle);
        if (notification.isRead()) {
            redCircle.setVisibility(View.INVISIBLE);
        }

        view.setOnClickListener(v -> {
            if (notificationRecyclerViewInterface != null) {
                notificationRecyclerViewInterface.onNotificationItemClick(position);
            }
        });

        return view;
    }

}
