package com.example.pokecenter.customer.lam.Model.order;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.OrderRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.google.android.material.divider.MaterialDivider;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrders;
    private OrderRecyclerViewInterface orderRecyclerViewInterface;

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

    public OrderAdapter(Context context, List<Order> orders, OrderRecyclerViewInterface orderRecyclerViewInterface) {
        this.mContext = context;
        this.mOrders = orders;
        this.orderRecyclerViewInterface = orderRecyclerViewInterface;
    }

    public void setData(List<Order> orders) {
        this.mOrders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_expandable_order_item, parent, false);
        return new OrderViewHolder(view, orderRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mOrders.get(position);

        holder.totalAmount.setText(currencyFormatter.format(order.getTotalAmount()));

        holder.createDateTime.setText("Created: " + order.getCreateDateTimeString());

        holder.orderStatus.setText(order.getStatus());

        holder.listOrders.removeAllViews();
        order.getOrdersDetail().forEach(detailOrder -> {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View detailItemView = inflater.inflate(R.layout.lam_detail_order_item, null);

            TextView productName = detailItemView.findViewById(R.id.product_name);
            TextView quantity = detailItemView.findViewById(R.id.quantity);
            TextView price = detailItemView.findViewById(R.id.price);

            Product product = ProductData.fetchedProducts.get(detailOrder.getProductId());
            productName.setText(product.getName());
            quantity.setText(String.valueOf(detailOrder.getQuantity()));
            price.setText(currencyFormatter.format(product.getOptions().get(detailOrder.getSelectedOption()).getPrice()));

            detailItemView.setPadding(0, 0, 0, 20);

            holder.listOrders.addView(detailItemView);

        });

        holder.arrowIcon.setImageDrawable(mContext.getDrawable(R.drawable.lam_round_keyboard_arrow_down_24));
        holder.expandableLayout.setVisibility(View.GONE);


        if (order.getStatus().equals("Delivered")) {
            holder.operations.setVisibility(View.VISIBLE);

            holder.orderStatus.setText(order.getStatus() + " - " + dateFormat.format(order.getDeliveryDate()));

            LocalDate localDate = order.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            holder.informText.setText("Please submit the refund request by " + localDate.plusDays(6).format(formatter));

        } else {
            holder.operations.setVisibility(View.GONE);
            holder.orderStatus.setText(order.getStatus());
        }



    }

    @Override
    public int getItemCount() {
        if (mOrders != null) {
            return mOrders.size();
        }
        return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView totalAmount;
        private TextView createDateTime;
        private TextView orderStatus;
        private ImageView arrowIcon;
        private LinearLayout expandableLayout;
        private LinearLayout listOrders;

        private LinearLayout operations;
        private TextView informText;

        private TextView contactVender;
        private TextView requestRefund;
        private TextView confirmReceived;

        private ProgressBar progressBar;


        public OrderViewHolder(@NonNull View itemView, OrderRecyclerViewInterface orderRecyclerViewInterface) {
            super(itemView);

            totalAmount = itemView.findViewById(R.id.total_amount);
            createDateTime = itemView.findViewById(R.id.createDateTime);
            orderStatus = itemView.findViewById(R.id.order_status);

            arrowIcon = itemView.findViewById(R.id.icon);

            expandableLayout = itemView.findViewById(R.id.expandable_part);
            listOrders = itemView.findViewById(R.id.list_orders);

            operations = itemView.findViewById(R.id.operations);
            informText = itemView.findViewById(R.id.inform_text);

            contactVender = itemView.findViewById(R.id.contact_vender);
            requestRefund = itemView.findViewById(R.id.request_refund);;
            confirmReceived = itemView.findViewById(R.id.confirm_received);

            progressBar = itemView.findViewById(R.id.progress_bar);

            contactVender.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                orderRecyclerViewInterface.onContactSellerClick(pos);
            });

            requestRefund.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                orderRecyclerViewInterface.onRequestRefundClick(pos);
            });

            confirmReceived.setOnClickListener(view -> {

                confirmReceived.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                int pos = getAbsoluteAdapterPosition();
                Order order = mOrders.get(pos);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {

                    boolean isSuccess = true;

                    try {
                        new FirebaseSupportCustomer().ConfirmReceived(order.getId());
                    } catch (IOException e) {
                        isSuccess = false;
                    }

                    boolean finalIsSuccess = isSuccess;
                    handler.post(() -> {

                        if (finalIsSuccess) {

                            operations.setVisibility(View.GONE);
                            order.setStatus("Delivery completed");

                            notifyItemChanged(pos);

                        } else {
                            confirmReceived.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.GONE);
                    });
                });
            });

            itemView.setOnClickListener(view -> {

                int pos = getAbsoluteAdapterPosition();
                Order order = mOrders.get(pos);
                order.toggleExpand();
                if (order.isExpand()) {
                    arrowIcon.setImageDrawable(mContext.getDrawable(R.drawable.lam_round_keyboard_arrow_up_24));
                    expandableLayout.setVisibility(View.VISIBLE);
                } else {
                    arrowIcon.setImageDrawable(mContext.getDrawable(R.drawable.lam_round_keyboard_arrow_down_24));
                    expandableLayout.setVisibility(View.GONE);
                }

            });
        }
    }
}
