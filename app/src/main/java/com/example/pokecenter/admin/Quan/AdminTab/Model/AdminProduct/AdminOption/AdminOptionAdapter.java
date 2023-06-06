package com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminOption;

import android.content.Context;
import android.util.Log;
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

public class AdminOptionAdapter extends RecyclerView.Adapter<AdminOptionAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private ArrayList<AdminOption> adminOptionList;

    public AdminOptionAdapter(ArrayList<AdminOption> adminOptionList, Context context, int resource) {
        this.context = context;
        this.resource = resource;
        this.adminOptionList = adminOptionList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProductOptionImage;
        private TextView tvProductOptionName;
        private TextView tvProductOptionCurrentQuantity;
        private TextView tvProductOptionPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductOptionImage = itemView.findViewById(R.id.ivProductOptionImage);
            tvProductOptionName = itemView.findViewById(R.id.tvProductOptionName);
            tvProductOptionCurrentQuantity = itemView.findViewById(R.id.tvProductOptionCurrentQuantity);
            tvProductOptionPrice = itemView.findViewById(R.id.tvProductOptionPrice);
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
        AdminOption option = adminOptionList.get(position);
        Log.e("POS", String.valueOf(position) + " " + adminOptionList.size());
        Log.e("option", option.getId() + ", position=" + position);
        holder.tvProductOptionName.setText(option.getId());
        holder.tvProductOptionCurrentQuantity.setText(String.valueOf(option.getCurrentQuantity()));
        Picasso.get().load(option.getOptionImage()).into(holder.ivProductOptionImage);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Convert the long value to a currency string
        String currencyString = currencyFormat.format(option.getPrice());
        holder.tvProductOptionPrice.setText(currencyString);
    }

    @Override
    public int getItemCount() {

        return adminOptionList.size();
    }
}
