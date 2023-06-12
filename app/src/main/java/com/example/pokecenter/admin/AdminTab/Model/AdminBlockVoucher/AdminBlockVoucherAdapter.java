package com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminBlockVoucherAdapter extends RecyclerView.Adapter<AdminBlockVoucherAdapter.ViewHolder> {

    private ArrayList<AdminBlockVoucher> blockVouchers;
    private int resource;
    private Context context;

    public AdminBlockVoucherAdapter(ArrayList<AdminBlockVoucher> blockVouchers, int resource, Context context) {
        this.blockVouchers = blockVouchers;
        this.resource = resource;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBlockVoucherId;
        private TextView tvBlockVoucherName;
        private TextView tvBlockVoucherStartDate;
        private TextView tvBlockVoucherEndDate;
        private TextView tvBlockVoucherCurrentQuantity;
        private TextView tvBlockVoucherValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBlockVoucherId = itemView.findViewById(R.id.tvBlockVoucherId);
            tvBlockVoucherName = itemView.findViewById(R.id.tvBlockVoucherName);
            tvBlockVoucherStartDate = itemView.findViewById(R.id.tvBlockVoucherStartDate);
            tvBlockVoucherEndDate = itemView.findViewById(R.id.tvBlockVoucherEndDate);
            tvBlockVoucherCurrentQuantity = itemView.findViewById(R.id.tvBlockVoucherCurrentQuantity);
            tvBlockVoucherValue = itemView.findViewById(R.id.tvBlockVoucherValue);
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
        AdminBlockVoucher blockVoucher = blockVouchers.get(position);
        if (blockVoucher != null) {
            holder.tvBlockVoucherId.setText(blockVoucher.getId());
            holder.tvBlockVoucherName.setText(blockVoucher.getName());
            holder.tvBlockVoucherStartDate.setText(blockVoucher.getStartDate());
            holder.tvBlockVoucherEndDate.setText(blockVoucher.getEndDate());
            holder.tvBlockVoucherCurrentQuantity.setText(String.valueOf(blockVoucher.getCurrentQuantity()));

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            // Convert the long value to a currency string
            String currencyString = currencyFormat.format(blockVoucher.getValue());
            holder.tvBlockVoucherValue.setText(currencyString);
        }
    }

    @Override
    public int getItemCount() {
        return blockVouchers.size();
    }
}
