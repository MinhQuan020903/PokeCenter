package com.example.pokecenter.customer.lam.Model.product;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Interface.RecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    private ArrayList<Product> mProducts = new ArrayList<>();

    private final RecyclerViewInterface recyclerViewInterface;

    public ProductAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.mContext = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setData(ArrayList<Product> list) {
        this.mProducts = list;
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
        if (position <= mProducts.size() - 1) {
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_card_view_product, parent, false);
        return new ProductAdapter.ProductViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mProducts.get(position);
        if (product == null) {
            return;
        }

        if (product.getDefaultImageUrl() != null) {
            holder.progress_bar.setVisibility(View.INVISIBLE);
            Picasso.get().load(product.getDefaultImageUrl()).into(holder.productImage);
            holder.productName.setText(product.getName());

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            // holder.productPrice.setTypeface(Typeface.MONOSPACE);
            holder.productPrice.setText(currencyFormatter.format(product.getPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView favoriteButton;
        private TextView productName;
        private TextView productPrice;
        private ProgressBar progress_bar;

        public ProductViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            progress_bar = itemView.findViewById(R.id.progress_bar);

            favoriteButton.setOnClickListener(view -> {
                favoriteButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.lam_heart_red_fill));
            });

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onProductCardClick(mProducts.get(pos));
                    }
                }
            });
        }
    }
}
