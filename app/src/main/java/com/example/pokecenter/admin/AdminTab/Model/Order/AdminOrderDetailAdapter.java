package com.example.pokecenter.admin.AdminTab.Model.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminOrderDetailAdapter extends  RecyclerView.Adapter<AdminOrderDetailAdapter.ViewHolder> {

    private ArrayList<OrderDetail> orderDetailList;
    private Context context;
    private int resource;

    public AdminOrderDetailAdapter(ArrayList<OrderDetail> orderDetailList, Context context, int resource) {
        this.orderDetailList = orderDetailList;
        this.context = context;
        this.resource = resource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivOrderDetailImage;
        private TextView tvOrderDetailName;
        private TextView tvOrderDetailOption;
        private TextView tvOrderDetailQuantityLabel;
        private TextView tvOrderDetailQuantity;
        private TextView tvOrderDetailTotalAmount;
        private TextView tvOrderDetailTotalAmountLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrderDetailImage = itemView.findViewById(R.id.ivOrderDetailImage);
            tvOrderDetailName = itemView.findViewById(R.id.tvOrderDetailName);
            tvOrderDetailOption = itemView.findViewById(R.id.tvOrderDetailOption);
            tvOrderDetailQuantityLabel = itemView.findViewById(R.id.tvOrderDetailQuantityLabel);
            tvOrderDetailQuantity = itemView.findViewById(R.id.tvOrderDetailQuantity);
            tvOrderDetailTotalAmountLabel = itemView.findViewById(R.id.tvOrderDetailTotalAmountLabel);
            tvOrderDetailTotalAmount = itemView.findViewById(R.id.tvOrderDetailTotalAmount);
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
        OrderDetail orderDetail = orderDetailList.get(position);
        if (orderDetail != null) {
            Picasso.get().load(orderDetail.getSelectedOptionImage()).into(holder.ivOrderDetailImage);
            holder.tvOrderDetailName.setText(orderDetail.getProductName());
            holder.tvOrderDetailOption.setText(orderDetail.getSelectedOptionName());
            holder.tvOrderDetailQuantityLabel.setText("Amount: ");
            holder.tvOrderDetailQuantity.setText(String.valueOf(orderDetail.getQuantity()));
            holder.tvOrderDetailTotalAmountLabel.setText("Total: ");

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            // Convert the long value to a currency string
            String currencyString = currencyFormat.format(orderDetail.getSelectedOptionPrice() * orderDetail.getQuantity());
            holder.tvOrderDetailTotalAmount.setText(currencyString);

        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }
}
