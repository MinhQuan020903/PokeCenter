package com.example.pokecenter.admin.AdminTab.Model.AdminNotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;

import java.util.ArrayList;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder> {

    private ArrayList<AdminNotification> adminNotificationsList;

    private Context context;
    private int resource;

    public AdminNotificationAdapter(ArrayList<AdminNotification> adminNotificationsList, Context context, int resource) {
        this.context = context;
        this.resource = resource;
        this.adminNotificationsList = adminNotificationsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAdminNotificationImage;
        private TextView tvAdminNotificationTitle;
        private ImageView ivAdminNotificationRedCircle;
        private TextView tvAdminNotificationContent;
        private TextView tvAdminSentDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAdminNotificationImage = itemView.findViewById(R.id.ivAdminNotificationImage);
            tvAdminNotificationTitle = itemView.findViewById(R.id.tvAdminNotificationTitle);
            ivAdminNotificationRedCircle = itemView.findViewById(R.id.ivAdminNotificationRedCircle);
            tvAdminNotificationContent = itemView.findViewById(R.id.tvAdminNotificationContent);
            tvAdminSentDate = itemView.findViewById(R.id.tvAdminSentDate);
        }
    }
    @NonNull
    @Override
    public AdminNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNotificationAdapter.ViewHolder holder, int position) {
        AdminNotification an = adminNotificationsList.get(position);
        if (an != null) {
            try {
                holder.ivAdminNotificationImage.setImageResource((R.drawable.lam_budew));
                holder.tvAdminNotificationTitle.setText(an.getTitle());
                holder.tvAdminNotificationContent.setText(an.getContent());
                holder.tvAdminSentDate.setText(an.getSentDate());
                if (an.isRead()) {
                    holder.ivAdminNotificationRedCircle.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.ivAdminNotificationRedCircle.setImageResource(R.drawable.lam_baseline_circle_24);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return adminNotificationsList.size();
    }
}

