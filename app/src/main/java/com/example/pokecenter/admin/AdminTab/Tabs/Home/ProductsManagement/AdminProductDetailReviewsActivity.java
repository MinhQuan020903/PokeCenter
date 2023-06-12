package com.example.pokecenter.admin.AdminTab.Tabs.Home.ProductsManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportProduct;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductReview.AdminProductReview;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductReview.AdminProductReviewAdapter;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.ActivityAdminProductDetailReviewsBinding;

import java.util.ArrayList;

public class AdminProductDetailReviewsActivity extends AppCompatActivity {

    private ActivityAdminProductDetailReviewsBinding binding;
    private AdminProduct adminProduct;
    private ArrayList<AdminProductReview> adminProductReviewList;
    private AdminProductReviewAdapter adminProductReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivityAdminProductDetailReviewsBinding.inflate(getLayoutInflater());
        getSupportActionBar().setTitle("Product's Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        adminProduct = (AdminProduct)intent.getSerializableExtra("AdminProduct");

        //Set no review LinearLayout invisible by default
        binding.llProductNoReviews.setVisibility(View.INVISIBLE);

        FirebaseSupportProduct firebaseSupportProduct = new FirebaseSupportProduct(this);
        firebaseSupportProduct.getProductReviewsFromFirebase(adminProduct, new FirebaseCallback<ArrayList<AdminProductReview>>() {
            @Override
            public void onCallback(ArrayList<AdminProductReview> reviewList) {
                adminProductReviewList = reviewList;

                if (adminProductReviewList.size() == 0) {
                    binding.rvProductReviews.setVisibility(View.INVISIBLE);
                    binding.llProductNoReviews.setVisibility(View.VISIBLE);
                } else {

                    setUpRecyclerView();
                }
            }
        });

        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {
        adminProductReviewAdapter = new AdminProductReviewAdapter(adminProductReviewList, AdminProductDetailReviewsActivity.this, R.layout.quan_product_review_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvProductReviews.addItemDecoration(itemSpacingDecoration);
        binding.rvProductReviews.setLayoutManager(new LinearLayoutManager(AdminProductDetailReviewsActivity.this));
        binding.rvProductReviews.setAdapter(adminProductReviewAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}