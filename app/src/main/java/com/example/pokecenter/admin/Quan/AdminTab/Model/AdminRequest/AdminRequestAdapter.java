package com.example.pokecenter.admin.Quan.AdminTab.Model.AdminRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminNotification.AdminNotificationAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.OnItemClickListener;

import java.util.ArrayList;

public class AdminRequestAdapter extends RecyclerView.Adapter<AdminRequestAdapter.ViewHolder> {

    private ArrayList<AdminRequest> requestList;

    private Context context;
    private int resource;
    private OnItemClickListener<AdminRequest> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdminRequestAdapter(ArrayList<AdminRequest> requestList, Context context, int resource) {
        this.requestList = requestList;
        this.context = context;
        this.resource = resource;
    }

    public void setRequestList(ArrayList<AdminRequest> requestList) {
        this.requestList = requestList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRequestId;
        private TextView tvCustomerId;
        private TextView tvCreateDate;
        private RadioButton rbRequestResolved;
        private RadioButton rbRequestNotResolved;
        private RadioButton rbRequestResolving;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestId = itemView.findViewById(R.id.tvRequestId);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
            rbRequestResolved = itemView.findViewById(R.id.rbRequestResolved);
            rbRequestNotResolved = itemView.findViewById(R.id.rbRequestNotResolved);
            rbRequestResolving = itemView.findViewById(R.id.rbRequestResolving);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(requestList.get(position), position);
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
        AdminRequest request = requestList.get(position);
        if (request != null) {
            holder.tvRequestId.setText(request.getId());
            if (request.getCustomerId().contains(",")) {
                holder.tvCustomerId.setText(request.getCustomerId().replace(",", "."));
            } else {
                holder.tvCustomerId.setText(request.getCustomerId());

            }

            holder.tvCreateDate.setText(request.getCreateDate());
            switch (request.getStatus()) {
                case "Not resolved" : {
                    holder.rbRequestNotResolved.setChecked(true);
                    break;
                }
                case "Resolved" : {
                    holder.rbRequestResolved.setChecked(true);
                    break;
                }
                case "Resolving" : {
                    holder.rbRequestResolving.setChecked(true);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}
