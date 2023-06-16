package com.example.pokecenter.vender.Model.Option;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;

import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.vender.Interface.OptionRecyclerViewInterface;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class VenderOptionAdapter extends RecyclerView.Adapter<VenderOptionAdapter.OptionViewHolder> {

    private Context mContext;

    private List<Option> mOptions;

    private final OptionRecyclerViewInterface optionRecyclerViewInterface;
    public VenderOptionAdapter(Context context, OptionRecyclerViewInterface optionRecyclerViewInterface) {
        mContext = context;
        this.optionRecyclerViewInterface = optionRecyclerViewInterface;
    }
    public VenderOptionAdapter(Context context, List<Option> Options, OptionRecyclerViewInterface optionRecyclerViewInterface) {
        mContext = context;
        mOptions = Options;
        this.optionRecyclerViewInterface = optionRecyclerViewInterface;
    }

    public void setData(List<Option> Options) {
        mOptions = Options;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ninh_option_list_item, parent, false);
        return new OptionViewHolder(view, optionRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        Option option = mOptions.get(position);
        holder.optionName.setText(option.getOptionName());
        holder.optionQuantity.setText(Integer.toString(option.getCurrentQuantity()));
        holder.optionPrice.setText(Integer.toString(option.getPrice()));
        if(!option.getOptionName().equals("null"))
        Picasso.get().load(option.getOptionImage()).into(holder.optionImage);
    }


    @Override
    public int getItemCount() {
        if (mOptions == null ) {
            return 0;
        }
        return mOptions.size();
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder {

        private TextView optionName;
        private TextView optionQuantity;
        private TextView optionPrice;
        private ShapeableImageView optionImage;

        public OptionViewHolder(@NonNull View itemView, OptionRecyclerViewInterface optionRecyclerViewInterface) {
            super(itemView);

            optionName = itemView.findViewById(R.id.option_name);
            optionQuantity = itemView.findViewById(R.id.option_quantity);
            optionPrice = itemView.findViewById(R.id.option_price);
            optionImage = itemView.findViewById(R.id.option_image);

            itemView.setOnClickListener(view -> {
                if (optionRecyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        optionRecyclerViewInterface.onOptionItemClick(pos);
                    }
                }
            });
        }

        
    }
}
