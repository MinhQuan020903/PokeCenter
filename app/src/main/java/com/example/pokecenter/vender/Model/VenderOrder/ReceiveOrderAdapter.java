package com.example.pokecenter.vender.Model.VenderOrder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.order.Order;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.vender.API.FirebaseSupportVender;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiveOrderAdapter extends RecyclerView.Adapter<ReceiveOrderAdapter.ReceiveOrderViewHolder> {

    private Context mContext;
    private List<Order> mOrders;

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

    public ReceiveOrderAdapter(Context context, List<Order> orders) {
        this.mContext = context;
        this.mOrders = orders;
    }

    public void setData(List<Order> orders) {
        this.mOrders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReceiveOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_receive_order_vender_item, parent, false);
        return new ReceiveOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveOrderViewHolder holder, int position) {
        Order order = mOrders.get(position);

        holder.totalAmount.setText(currencyFormatter.format(order.getTotalAmount()));

        holder.createDateTime.setText("Created: " + order.getCreateDateTimeString());

        holder.listOrders.removeAllViews();
        order.getOrdersDetail().forEach(detailOrder -> {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View detailItemView = inflater.inflate(R.layout.lam_detail_order_item, null);

            TextView productName = detailItemView.findViewById(R.id.product_name);
            TextView selectedOption = detailItemView.findViewById(R.id.selectedOption);
            TextView quantity = detailItemView.findViewById(R.id.quantity);
            TextView price = detailItemView.findViewById(R.id.price);

            Product product = ProductData.fetchedProducts.get(detailOrder.getProductId());
            productName.setText(product.getName());

            selectedOption.setText(product.getOptions().get(detailOrder.getSelectedOption()).getOptionName());

            quantity.setText(String.valueOf(detailOrder.getQuantity()));
            price.setText(currencyFormatter.format(product.getOptions().get(detailOrder.getSelectedOption()).getPrice()));

            detailItemView.setPadding(0, 0, 0, 20);

            holder.listOrders.addView(detailItemView);

        });

        if (order.getStatus().contains("Packaged")) {
            holder.packaged.setVisibility(View.GONE);
        }
        if (order.getStatus().contains("Delivery completed")) {
            holder.packaged.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (mOrders != null) {
            return mOrders.size();
        }
        return 0;
    }

    public class ReceiveOrderViewHolder extends RecyclerView.ViewHolder {

        private TextView totalAmount;
        private TextView createDateTime;

        private LinearLayout expandableLayout;
        private LinearLayout listOrders;

        private Button packaged;

        private ProgressBar progressBar;


        public ReceiveOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            totalAmount = itemView.findViewById(R.id.total_amount);
            createDateTime = itemView.findViewById(R.id.createDateTime);

            expandableLayout = itemView.findViewById(R.id.expandable_part);
            listOrders = itemView.findViewById(R.id.list_orders);

            packaged = itemView.findViewById(R.id.packedOrder);

            progressBar = itemView.findViewById(R.id.progress_bar);



            packaged.setOnClickListener(view -> {

                packaged.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                int pos = getAbsoluteAdapterPosition();
                Order order = mOrders.get(pos);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                String newStatus = "Packaged - " + dateFormat.format(new Date());

                executor.execute(() -> {

                    boolean isSuccess = true;

                    try {
                        new FirebaseSupportVender().ChangeOrderStatus(order.getId(), newStatus);
                    } catch (IOException e) {
                        isSuccess = false;
                    }

                    boolean finalIsSuccess = isSuccess;
                    handler.post(() -> {
                        if (finalIsSuccess) {

                            order.setStatus(newStatus);

                            mOrders.remove(pos);
                            notifyItemRemoved(pos);

                            Toast.makeText(mContext, "Order status changed", Toast.LENGTH_SHORT)
                                    .show();

                            progressBar.setVisibility(View.GONE);

                        } else {

                            packaged.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(mContext, "Failed to connect server!", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });
                });
            });

            itemView.setOnClickListener(view -> {

                int pos = getAbsoluteAdapterPosition();
                Order order = mOrders.get(pos);
                order.toggleExpand();
                if (order.isExpand()) {
                    expandableLayout.setVisibility(View.VISIBLE);
                } else {
                    expandableLayout.setVisibility(View.GONE);
                }

            });
        }
    }

}
