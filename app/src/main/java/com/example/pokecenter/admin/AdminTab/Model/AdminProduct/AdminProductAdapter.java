package com.example.pokecenter.admin.AdminTab.Model.AdminProduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.squareup.picasso.Picasso;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private ArrayList<AdminProduct> adminProductList;
    private Context context;
    private int resource;
    private OnItemClickListener<AdminProduct> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setProductList(ArrayList<AdminProduct> adminProductList) {
        this.adminProductList = adminProductList;
    }

    public AdminProductAdapter(ArrayList<AdminProduct> adminProductList, Context context, int resource) {
        this.adminProductList = adminProductList;
        this.context = context;
        this.resource = resource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductVenderId;
        private TextView tvProductPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductVenderId = itemView.findViewById(R.id.tvProductVenderId);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(adminProductList.get(position), position);
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
        AdminProduct adminProduct = adminProductList.get(position);
        if (adminProduct != null) {
            try {
                Picasso.get().load(adminProduct.getImages().get(0)).into(holder.ivProductImage);
                holder.tvProductName.setText(adminProduct.getName());
                holder.tvProductVenderId.setText(adminProduct.getVenderId());

                //Get product price range
                String priceRange = "___";
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                if (adminProduct.getOptions().size() > 0) {

                    if (adminProduct.getOptions().size() > 1) { //If there're many options, find max and min price
                        long maxPrice = Long.MIN_VALUE, minPrice = Long.MAX_VALUE;
                        for (AdminOption option : adminProduct.getOptions()) {
                            long currentPrice = option.getPrice();
                            if (currentPrice > maxPrice) {
                                maxPrice = currentPrice;
                            }
                            if (currentPrice < minPrice) {
                                minPrice = currentPrice;
                            }
                        }
                        if (minPrice < maxPrice) {
                            String minPriceString = currencyFormat.format(minPrice);
                            String maxPriceString = currencyFormat.format(maxPrice);
                            priceRange = minPriceString + " ... " + maxPriceString;
                        }
                        else {
                            // Convert the long value to a currency string
                            String currencyString = currencyFormat.format(adminProduct.getOptions().get(0).getPrice());
                            priceRange = currencyString;
                        }
                    }
                    else {

                        // Convert the long value to a currency string
                        String currencyString = currencyFormat.format(adminProduct.getOptions().get(0).getPrice());
                        priceRange = currencyString;
                    }
                }

                holder.tvProductPrice.setText(priceRange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return adminProductList.size();
    }
}
