package com.example.pokecenter.customer.lam.Model.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Product> mProducts = new ArrayList<>();
    private final PokemonRecyclerViewInterface pokemonRecyclerViewInterface;

    public ProductAdapter(Context context, PokemonRecyclerViewInterface pokemonRecyclerViewInterface) {
        this.mContext = context;
        this.pokemonRecyclerViewInterface = pokemonRecyclerViewInterface;
    }

    public ProductAdapter(Context context, List<Product> products, PokemonRecyclerViewInterface pokemonRecyclerViewInterface) {
        this.mContext = context;
        this.mProducts = products;
        this.pokemonRecyclerViewInterface = pokemonRecyclerViewInterface;
    }

    public void setData(List<Product> list) {
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
        return new ProductAdapter.ProductViewHolder(view, pokemonRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mProducts.get(position);

        if (product == null) {
            return;
        }

        if (product.getName() != null) {
            holder.progress_bar.setVisibility(View.INVISIBLE);
            Picasso.get().load(product.getImages().get(0)).into(holder.productImage);
            holder.productName.setText(product.getName());

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            List<Option> options = product.getOptions();

            if (options.size() == 1) {
                holder.productPrice.setText(currencyFormatter.format(options.get(0).getPrice()));
            } else {
                holder.productPrice.setText(currencyFormatter.format(options.get(0).getPrice()) + " - " + currencyFormatter.format(options.get(options.size() - 1).getPrice()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private ProgressBar progress_bar;

        public ProductViewHolder(@NonNull View itemView, PokemonRecyclerViewInterface pokemonRecyclerViewInterface) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            progress_bar = itemView.findViewById(R.id.progress_bar);


            itemView.setOnClickListener(view -> {
                if (pokemonRecyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        pokemonRecyclerViewInterface.onProductCardClick(mProducts.get(pos));
                    }
                }
            });
        }
    }
}
