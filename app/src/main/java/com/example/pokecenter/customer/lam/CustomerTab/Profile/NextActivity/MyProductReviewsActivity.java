package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Model.purchasedProduct.PurchasedProduct;
import com.example.pokecenter.customer.lam.Model.purchasedProduct.PurchasedProductAdapter;
import com.example.pokecenter.databinding.ActivityMyProductReviewsBinding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyProductReviewsActivity extends AppCompatActivity {

    private ActivityMyProductReviewsBinding binding;
    private List<PurchasedProduct> myPurchasedProducts;

    private RecyclerView rcvPurchasedProduct;
    private PurchasedProductAdapter purchasedProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyProductReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Product Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchingAndSetupRecyclerView();

    }

    private void fetchingAndSetupRecyclerView() {

        rcvPurchasedProduct = binding.rcvPurchasedProduct;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvPurchasedProduct.setLayoutManager(linearLayoutManager);

        purchasedProductAdapter = new PurchasedProductAdapter(this, myPurchasedProducts);
        rcvPurchasedProduct.setAdapter(purchasedProductAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            boolean isSuccess = true;
            List<PurchasedProduct> fetchedPurchasedProducts = null;

            try {
                fetchedPurchasedProducts = new FirebaseSupportCustomer().fetchingPurchasedProducts();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            List<PurchasedProduct> finalFetchedPurchasedProducts = fetchedPurchasedProducts;
            handler.post(() -> {

                if (finalIsSuccess) {

                    myPurchasedProducts = finalFetchedPurchasedProducts;
                    purchasedProductAdapter.setData(myPurchasedProducts);

                    if (finalFetchedPurchasedProducts.size() == 0) {
                        binding.informText.setText("You haven't purchased any products yet.");
                        binding.informText.setVisibility(View.VISIBLE);
                    }

                } else {

                    binding.informText.setText("Failed to connect server");
                    binding.informText.setVisibility(View.VISIBLE);

                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}