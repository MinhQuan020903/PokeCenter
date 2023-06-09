package com.example.pokecenter.vender.Model.Vender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class VenderFollowerAdapter extends RecyclerView.Adapter<VenderFollowerAdapter.ViewHolder> {

    private ArrayList<User> usersList;
    private Context context;
    private int resource;

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public VenderFollowerAdapter(ArrayList<User> usersList, Context context, int resource) {
        this.usersList = usersList;
        this.context = context;
        this.resource = resource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserAvatar;
        private TextView tvUsername;
        private TextView tvUserEmail;
        private TextView tvUserPhoneNumber;
        private TextView tvUserRegister;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhoneNumber = itemView.findViewById(R.id.tvUserPhoneNumber);
            tvUserRegister = itemView.findViewById(R.id.tvUserRegister);

        }
    }

    @NonNull
    @Override
    public VenderFollowerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);

        return new VenderFollowerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VenderFollowerAdapter.ViewHolder holder, int position) {
        User user = usersList.get(position);
        if (user != null) {
            Picasso.get().load(user.getAvatar()).into(holder.ivUserAvatar);
            holder.tvUsername.setText(user.getUsername());
            holder.tvUserEmail.setText(user.getEmail());
            if (user.getPhoneNumber() == null || Objects.equals(user.getPhoneNumber(), "")) {
                holder.tvUserPhoneNumber.setText("___");
            }
            else {
                holder.tvUserPhoneNumber.setText(user.getPhoneNumber());
            }

            holder.tvUserRegister.setText(user.getRegistrationDate());
        }

    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

