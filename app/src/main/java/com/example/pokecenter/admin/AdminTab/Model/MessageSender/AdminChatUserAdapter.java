package com.example.pokecenter.admin.AdminTab.Model.MessageSender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Utils.DateUtils;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminChatUserAdapter extends RecyclerView.Adapter<AdminChatUserAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private ArrayList<AdminChatUser> chatUsers;
    private OnItemClickListener onItemClickListener;

    public AdminChatUserAdapter(Context context, int resource, ArrayList<AdminChatUser> chatUsers) {
        this.context = context;
        this.resource = resource;
        this.chatUsers = chatUsers;
    }

    public void setChatUsers(ArrayList<AdminChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivUserAvatar;
        private TextView tvUserName;
        private TextView tvLatestMessageSentTime;
        private TextView tvLatestMessage;
        private TextView tvUnseenMessageCount;
        private FrameLayout flUnseenMessageCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLatestMessageSentTime = itemView.findViewById(R.id.tvLatestMessageSentTime);
            tvLatestMessage = itemView.findViewById(R.id.tvLatestMessage);
            tvUnseenMessageCount = itemView.findViewById(R.id.tvUnseenMessageCount);
            flUnseenMessageCount = itemView.findViewById(R.id.flUnseenMessageCount);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminChatUser chatUser = chatUsers.get(position);
        if (chatUser != null) {
            Picasso.get().load(chatUser.getAvatar()).into(holder.ivUserAvatar);
            holder.tvUserName.setText(chatUser.getName());
            Message latestMessage = DateUtils.getLatestMessage(chatUser.getMessageList());
            holder.tvLatestMessage.setText(latestMessage.getContent());

            //Format sentTime for easier reading
            String sentTime = DateUtils.formatMessageDateTime(latestMessage.getSentTime());
            holder.tvLatestMessageSentTime.setText(sentTime);

            //Count amount of unseen messages
            int unseenMessageCount = 0;
            for (Message message : chatUser.getMessageList()) {
                if (!message.isHasSeen()) {
                    unseenMessageCount++;
                }
            }
            //If unseenCount = 0 (there're no unseen messages),
            //Hide the count
            if (unseenMessageCount == 0) {
                holder.flUnseenMessageCount.setVisibility(View.INVISIBLE);
                holder.tvUnseenMessageCount.setVisibility(View.INVISIBLE);
            } else {
                holder.tvUnseenMessageCount.setText(String.valueOf(unseenMessageCount));

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set unseen count invisible
                    holder.flUnseenMessageCount.setVisibility(View.INVISIBLE);
                    holder.tvUnseenMessageCount.setVisibility(View.INVISIBLE);
                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(chatUsers.get(pos), pos);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }
}
