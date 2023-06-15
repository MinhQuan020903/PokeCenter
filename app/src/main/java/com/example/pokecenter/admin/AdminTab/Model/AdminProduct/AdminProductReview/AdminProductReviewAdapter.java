package com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductReview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;

import java.util.ArrayList;

public class AdminProductReviewAdapter extends RecyclerView.Adapter<AdminProductReviewAdapter.ViewHolder> {

    private ArrayList<AdminProductReview> adminProductReviewList;
    private Context context;
    private int resource;

    public AdminProductReviewAdapter(ArrayList<AdminProductReview> adminProductReviewList, Context context, int resource) {
        this.adminProductReviewList = adminProductReviewList;
        this.context = context;
        this.resource = resource;
    }

    public void setAdminProductReviewList(ArrayList<AdminProductReview> adminProductReviewList) {
        this.adminProductReviewList = adminProductReviewList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCustomerName;
        private ImageView ivProductReviewStar0;
        private ImageView ivProductReviewStar1;
        private ImageView ivProductReviewStar2;
        private ImageView ivProductReviewStar3;
        private ImageView ivProductReviewStar4;

        private TextView tvReviewSummary;
        private TextView tvReviewDetail;
        private TextView tvReviewCreateDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvReviewSummary = itemView.findViewById(R.id.tvReviewSummary);
            tvReviewDetail = itemView.findViewById(R.id.tvReviewDetail);
            tvReviewCreateDate = itemView.findViewById(R.id.tvReviewCreateDate);
            ivProductReviewStar0 = itemView.findViewById(R.id.ivProductReviewStar0);
            ivProductReviewStar1 = itemView.findViewById(R.id.ivProductReviewStar1);
            ivProductReviewStar2 = itemView.findViewById(R.id.ivProductReviewStar2);
            ivProductReviewStar3 = itemView.findViewById(R.id.ivProductReviewStar3);
            ivProductReviewStar4 = itemView.findViewById(R.id.ivProductReviewStar4);
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
        AdminProductReview review = adminProductReviewList.get(position);
        if (review != null) {
            try {
                holder.tvCustomerName.setText(review.getCustomerId().replace(",", "."));
                holder.tvReviewSummary.setText(review.getTitle());
                holder.tvReviewDetail.setText(review.getContent());
                holder.tvReviewCreateDate.setText(review.getCreateDate());

                //Bind stars ImageView based on rate number
                int rate = review.getRate();
                String drawableRes = "ivProductReviewStar";
                for (int i = 0; i < rate; i++) {
                    int starImageViewId = holder.itemView.getResources().getIdentifier(drawableRes + i, "id", holder.itemView.getContext().getPackageName());
                    ImageView starImageView = holder.itemView.findViewById(starImageViewId);
                    starImageView.setImageResource(R.drawable.quan_icon_filled_star);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return adminProductReviewList.size();
    }
}
