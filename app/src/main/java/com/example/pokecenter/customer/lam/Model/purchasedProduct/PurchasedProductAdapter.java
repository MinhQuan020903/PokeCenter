package com.example.pokecenter.customer.lam.Model.purchasedProduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PurchasedProductAdapter extends RecyclerView.Adapter<PurchasedProductAdapter.PurchasedProductVH> {

    private Context mContext;
    private List<PurchasedProduct> mPurchasedProducts;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    public PurchasedProductAdapter(Context context, List<PurchasedProduct> purchasedProducts) {
        mContext = context;
        mPurchasedProducts = purchasedProducts;
    }

    public void setData(List<PurchasedProduct> purchasedProducts) {
        mPurchasedProducts = purchasedProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PurchasedProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_purchased_product_item, parent, false);
        return new PurchasedProductVH(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PurchasedProductVH holder, int position) {

        PurchasedProduct purchasedProduct = mPurchasedProducts.get(position);

        Product product = ProductData.fetchedProducts.get(purchasedProduct.getProductId());

        holder.productName.setText(product.getName());

        Option option = product.getOptions().get(purchasedProduct.getSelectedOption());

        if (option.getOptionImage().isEmpty()) {
            Picasso.get().load(product.getImages().get(0)).into(holder.productImage);
        } else {
            Picasso.get().load(option.getOptionImage()).into(holder.productImage);
        }

        if (option.getOptionName().equals("null")) {
            holder.selectedOption.setVisibility(View.GONE);
        } else {
            holder.selectedOption.setVisibility(View.VISIBLE);
            holder.selectedOption.setText(option.getOptionName());
        }

        holder.price.setText(currencyFormatter.format(option.getPrice()));
        holder.purchasedDate.setText(purchasedProduct.getPurchasedDate());

        if (purchasedProduct.isReviewed()) {
            holder.reviewButton.setVisibility(View.GONE);
            holder.reviewedImage.setVisibility(View.VISIBLE);
        } else {
            holder.reviewButton.setVisibility(View.VISIBLE);
            holder.reviewedImage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (mPurchasedProducts != null) {
            return mPurchasedProducts.size();
        }
        return 0;
    }

    public class PurchasedProductVH extends RecyclerView.ViewHolder {

        private TextView productName;
        private ImageView productImage;
        private TextView selectedOption;
        private TextView price;
        private TextView purchasedDate;
        private TextView reviewButton;
        private ImageView reviewedImage;

        public PurchasedProductVH(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
            selectedOption = itemView.findViewById(R.id.selectedOption);
            price = itemView.findViewById(R.id.price);
            purchasedDate = itemView.findViewById(R.id.purchasedDate);
            reviewButton = itemView.findViewById(R.id.reviewButton);
            reviewedImage = itemView.findViewById(R.id.reviewedImage);

        }
    }

}
