package com.example.pokecenter.admin.Quan.AdminTab.Model.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.MessageSender;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.MessageSenderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> usersList;
    private Context context;
    private int resource;

    public UserAdapter(ArrayList<User> usersList, Context context, int resource) {
        this.usersList = usersList;
        this.context = context;
        this.resource = resource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserAvatar;
        private TextView tvUsername;
        private TextView tvUserEmail;
        private TextView tvUserPhoneNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhoneNumber = itemView.findViewById(R.id.tvUserPhoneNumber);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = usersList.get(position);
        if (user != null) {
            Picasso.get().load(user.getAvatar()).into(holder.ivUserAvatar);
            holder.tvUsername.setText(user.getUsername());
            holder.tvUserEmail.setText(user.getEmail());
            holder.tvUserPhoneNumber.setText(user.getPhoneNumber());
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


}
