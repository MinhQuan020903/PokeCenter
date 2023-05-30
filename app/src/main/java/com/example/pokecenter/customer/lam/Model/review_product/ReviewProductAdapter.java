package com.example.pokecenter.customer.lam.Model.review_product;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewProductAdapter extends ArrayAdapter<ReviewProduct> {

    private Context mContext;
    private List<ReviewProduct> mReviewsProduct;

    public ReviewProductAdapter(Context context, List<ReviewProduct> reviewsProduct) {
        super(context, R.layout.lam_review_product_item, reviewsProduct);

        mContext = context;
        mReviewsProduct = reviewsProduct;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.lam_review_product_item, null);

        ReviewProduct review = mReviewsProduct.get(position);

        ImageView avatarImage = view.findViewById(R.id.avatarImage);
        Picasso.get().load(review.getCustomerImage()).into(avatarImage);

        TextView customerName = view.findViewById(R.id.customerName);
        customerName.setText(review.getCustomerName());

        TextView reviewTitle = view.findViewById(R.id.review_title);
        reviewTitle.setText(review.getTitle());

        LinearLayout rate = view.findViewById(R.id.product_rate);

        // Set layout params with end margin of 8dp
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics()));

        // Add the appropriate number of stars to the LinearLayout
        for (int i = 1; i <= review.getRate(); ++i) {

            // Create a new ImageView object for gold fill star
            ImageView starGoldFill = new ImageView(mContext);
            starGoldFill.setImageResource(R.drawable.lam_star_fill_gold_24);

            rate.addView(starGoldFill, layoutParams);

        }

        for (int i = 1; i <= 5 - review.getRate(); ++i) {

            // Create a new ImageView object for black outline star
            ImageView starBlackOutline = new ImageView(mContext);
            starBlackOutline.setImageResource(R.drawable.lam_star_outline_black_24);

            rate.addView(starBlackOutline, layoutParams);
        }

        TextView reviewContent = view.findViewById(R.id.review_content);
        reviewContent.setText(review.getContent());


        TextView createDate = view.findViewById(R.id.review_create_date);
        createDate.setText(review.getCreateDate());

        return view;
    }

}
