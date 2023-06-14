package com.example.pokecenter.admin.AdminTab.Model.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> usersList;
    private Context context;
    private int resource;
    private OnItemClickListener<User> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

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
        private TextView tvUserRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhoneNumber = itemView.findViewById(R.id.tvUserPhoneNumber);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(usersList.get(position), position);
                        }
                    }
                }
            });
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
            try {
                Picasso.get().load(user.getAvatar()).into(holder.ivUserAvatar);
                holder.tvUsername.setText(user.getUsername());
                holder.tvUserEmail.setText(user.getEmail());
                if (user.getPhoneNumber() == null || Objects.equals(user.getPhoneNumber(), "")) {
                    holder.tvUserPhoneNumber.setText("___");
                }
                else {
                    holder.tvUserPhoneNumber.setText(user.getPhoneNumber());
                }

                String role = (user.getRole() == 0) ? "Customer" : (user.getRole() == 1) ? "Vender" : "Admin";
                holder.tvUserRole.setText(role);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
