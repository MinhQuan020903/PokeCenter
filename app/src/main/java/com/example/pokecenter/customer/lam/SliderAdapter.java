package com.example.pokecenter.customer.lam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pokecenter.R;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    List<String> imgUrl;

    public SliderAdapter(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_product_item, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Picasso.get().load(imgUrl.get(position)).into(viewHolder.productImageView);
    }

    @Override
    public int getCount() {
        return imgUrl.size();
    }


    class Holder extends SliderViewAdapter.ViewHolder {
        private ImageView productImageView;

        public Holder(View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.product_image_view);
        }
    }
}
