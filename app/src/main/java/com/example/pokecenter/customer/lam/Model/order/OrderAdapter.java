package com.example.pokecenter.customer.lam.Model.order;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.checkout_item.CheckoutProductAdapter;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Provider.ProductData;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrders;

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public OrderAdapter(Context context, List<Order> orders) {
        this.mContext = context;
        this.mOrders = orders;
    }

    public void setData(List<Order> orders) {
        this.mOrders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_expandable_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mOrders.get(position);

        holder.totalAmount.setText(currencyFormatter.format(order.getTotalAmount()));

        holder.createDateTime.setText(order.getCreateDateTime());

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

        if (order.isExpand()) {
            holder.arrowIcon.setImageDrawable(mContext.getDrawable(R.drawable.lam_round_keyboard_arrow_up_24));
            holder.expandableLayout.setVisibility(View.VISIBLE);
        } else {
            holder.arrowIcon.setImageDrawable(mContext.getDrawable(R.drawable.lam_round_keyboard_arrow_down_24));
            holder.expandableLayout.setVisibility(View.GONE);
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
        private ImageView arrowIcon;
        private LinearLayout expandableLayout;
        private LinearLayout listOrders;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            totalAmount = itemView.findViewById(R.id.total_amount);
            createDateTime = itemView.findViewById(R.id.createDateTime);
            arrowIcon = itemView.findViewById(R.id.icon);

            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            listOrders = itemView.findViewById(R.id.list_orders);

            itemView.setOnClickListener(view -> {

                int pos = getAbsoluteAdapterPosition();
                Order order = mOrders.get(pos);
                order.toggleExpand();
                notifyItemChanged(pos);

            });
        }
    }
}
