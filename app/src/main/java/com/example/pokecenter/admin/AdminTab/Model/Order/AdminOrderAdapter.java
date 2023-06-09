package com.example.pokecenter.admin.AdminTab.Model.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private ArrayList<Order> orderList;
    private Context context;
    private int resource;

    private OnItemClickListener<Order> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public AdminOrderAdapter(ArrayList<Order> orderList, Context context, int resource) {
        this.orderList = orderList;
        this.context = context;
        this.resource = resource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOrderId;
        private TextView tvOrderCustomerId;
        private TextView tvOrderVenderId;
        private TextView tvOrderCreateDate;
        private TextView tvOrderTotalAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderCustomerId = itemView.findViewById(R.id.tvOrderCustomerId);
            tvOrderVenderId = itemView.findViewById(R.id.tvOrderVenderId);
            tvOrderCreateDate = itemView.findViewById(R.id.tvOrderCreateDate);
            tvOrderTotalAmount = itemView.findViewById(R.id.tvOrderTotalAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(orderList.get(position), position);
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
        Order order = orderList.get(position);
        if (order != null) {
            try {
                holder.tvOrderId.setText(order.getId());
                holder.tvOrderCustomerId.setText(order.getCustomerId());
                holder.tvOrderVenderId.setText(order.getVenderId());
                holder.tvOrderCreateDate.setText(order.getCreateDate());

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(order.getTotalAmount());
                holder.tvOrderTotalAmount.setText(currencyString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
