package com.example.pokecenter.vender.Model.Notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.vender.VenderTab.VenderNotificationsFragment;

import java.util.ArrayList;

public class NotificationDataAdapter extends RecyclerView.Adapter<NotificationDataAdapter.ViewHolder> {

    private ArrayList<NotificationData> venderNotificationsList;
    private Context context;
    private int resource;
    private Fragment fragment;

    public NotificationDataAdapter(ArrayList<NotificationData> venderNotificationsList, Context context, int resource, Fragment fragment) {
        this.context = context;
        this.resource = resource;
        this.venderNotificationsList = venderNotificationsList;
        this.fragment = fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivVenderNotificationImage;
        private TextView tvVenderNotificationTitle;
        private ImageView ivVenderNotificationRedCircle;
        private TextView tvVenderNotificationContent;
        private TextView tvVenderSentDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVenderNotificationImage = itemView.findViewById(R.id.ivAdminNotificationImage);
            tvVenderNotificationTitle = itemView.findViewById(R.id.tvAdminNotificationTitle);
            ivVenderNotificationRedCircle = itemView.findViewById(R.id.ivAdminNotificationRedCircle);
            tvVenderNotificationContent = itemView.findViewById(R.id.tvAdminNotificationContent);
            tvVenderSentDate = itemView.findViewById(R.id.tvAdminSentDate);
        }
    }
    @NonNull
    @Override
    public NotificationDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new NotificationDataAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NotificationData notificationData = venderNotificationsList.get(position);

        if (notificationData != null) {
            try {
                holder.ivVenderNotificationImage.setImageResource((R.drawable.lam_budew));
                holder.tvVenderNotificationTitle.setText(notificationData.getTitle());
                holder.tvVenderNotificationContent.setText(notificationData.getContent());
                holder.tvVenderSentDate.setText(notificationData.getSentDateString());
                if (notificationData.isRead()) {
                    holder.ivVenderNotificationRedCircle.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.ivVenderNotificationRedCircle.setImageResource(R.drawable.lam_baseline_circle_24);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Gán sự kiện onClick cho ViewHolder
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        if (fragment != null && fragment instanceof VenderNotificationsFragment) {
                            ((VenderNotificationsFragment) fragment).onNotificationItemClick(position);
                        }
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return venderNotificationsList.size();
    }
}
