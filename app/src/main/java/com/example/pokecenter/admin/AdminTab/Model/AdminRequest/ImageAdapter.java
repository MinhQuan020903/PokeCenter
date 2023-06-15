package com.example.pokecenter.admin.AdminTab.Model.AdminRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imageList;
    private Context context;
    private int resource;

    public ImageAdapter(ArrayList<String> imageList, Context context, int resource) {
        this.imageList = imageList;
        this.context = context;
        this.resource = resource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImageItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImageItem = itemView.findViewById(R.id.ivImageItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageList.get(position);
        if (imageUrl != null) {
            try {
                Picasso.get().load(imageUrl).into(holder.ivImageItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
