package com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender;


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


public class MessageSenderAdapter extends RecyclerView.Adapter<MessageSenderAdapter.ViewHolder> {
    public void setMessageSendersList(ArrayList<MessageSender> messageSendersList) {
        this.messageSendersList = messageSendersList;
    }

    private ArrayList<MessageSender> messageSendersList;

    private Context context;
    private int resource;

    private int senderRole;
    public MessageSenderAdapter(ArrayList<MessageSender> messageSendersList, Context context, int resource, int senderRole) {
        this.context = context;
        this.resource = resource;
        this.senderRole = senderRole;
        this.messageSendersList = messageSendersList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSenderAvatar;
        private TextView tvSenderName;
        private TextView tvMessageSentTime;
        private TextView tvMessageLatestSentence;
        private TextView tvMessageUnseenSentenceCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSenderAvatar = itemView.findViewById(R.id.ivSenderAvatar);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessageLatestSentence = itemView.findViewById(R.id.tvMessageLatestSentence);
            tvMessageSentTime = itemView.findViewById(R.id.tvMessageSentTime);
            tvMessageUnseenSentenceCount = itemView.findViewById(R.id.tvMessageUnseenSentenceCount);
        }
    }
    @NonNull
    @Override
    public MessageSenderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageSenderAdapter.ViewHolder holder, int position) {
        MessageSender ms = messageSendersList.get(position);
        if (ms != null) {
            holder.ivSenderAvatar.setImageResource(ms.getSenderAvatar());
            holder.tvSenderName.setText(ms.getSenderName());
            holder.tvMessageLatestSentence.setText(ms.getMessageLatestSentence());
            holder.tvMessageSentTime.setText(ms.getMessageSentTime());
            holder.tvMessageUnseenSentenceCount.setText(String.valueOf(ms.getMessageUnseenSentenceCount()));
        }
    }

    @Override
    public int getItemCount() {
        return messageSendersList.size();
    }
}
