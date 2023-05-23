package com.example.pokecenter.customer.lam.Model.checkout_item;

import android.app.appsearch.observer.SchemaChangeInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Cart.CheckoutActivity;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutProductAdapter extends RecyclerView.Adapter<CheckoutProductAdapter.CheckoutItemViewHolder> {

    private Context mContext;
    private List<CheckoutItem> mCheckoutItemList;

    public int totalHeight = 0;

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public CheckoutProductAdapter(Context context, List<CheckoutItem> checkoutItemList) {
        this.mContext = context;
        this.mCheckoutItemList = checkoutItemList;
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_checkout_list_item, parent, false);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position) {
        CheckoutItem checkoutItem = mCheckoutItemList.get(position);

        Picasso.get().load(checkoutItem.getImage()).into(holder.productImage);

        holder.productName.setText(checkoutItem.getName());

        if (checkoutItem.getOptionSize() == 1) {
            holder.selectedOption.setVisibility(View.GONE);
        } else {
            String selectedOptionName = ProductData.fetchedProducts.get(checkoutItem.getProductId()).getOptions().get(checkoutItem.getSelectedOption()).getOptionName();
            holder.selectedOption.setText(selectedOptionName);
        }

        holder.price.setText(currencyFormatter.format(checkoutItem.getPrice()));
        holder.quantity.setText("x" + checkoutItem.getQuantity());
    }

    @Override
    public int getItemCount() {
        if (mCheckoutItemList != null) {
            return mCheckoutItemList.size();
        }
        return 0;
    }

    public class CheckoutItemViewHolder  extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView selectedOption;
        private TextView price;
        private TextView quantity;

        public CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            selectedOption = itemView.findViewById(R.id.selectedOption);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.order_quantity);
        }
    }

}
