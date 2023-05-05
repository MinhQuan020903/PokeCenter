package com.example.pokecenter.customer.lam.Model.cart;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.customer.lam.Interface.AddressRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Interface.CartRecyclerViewInterface;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context mContext;
    private List<Cart> mCarts;
    private final CartRecyclerViewInterface cartRecyclerViewInterface;

    public CartAdapter(Context context, List<Cart> carts, CartRecyclerViewInterface cartRecyclerViewInterface) {
        this.mContext = context;
        this.mCarts = carts;
        this.cartRecyclerViewInterface = cartRecyclerViewInterface;
    }

    public void setData(List<Cart> carts) {
        this.mCarts = carts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (mCarts == null ) {
            return 0;
        }
        return mCarts.size();
    }

    public class CartViewHolder  extends RecyclerView.ViewHolder {

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
