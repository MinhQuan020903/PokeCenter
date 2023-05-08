package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.ProductDetailActivity;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.ActivityWishListBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishListActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {

    private ActivityWishListBinding binding;

    private RecyclerView rcvProduct;

    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set statusBar Color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /* Set color to title */
        getSupportActionBar().setTitle("Wish List");
        /* Set up back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityWishListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpVenderProductRcv();
    }

    private void setUpVenderProductRcv() {

        rcvProduct = binding.rcvGridProduct;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);


        List<Product> wishListProduct = new ArrayList<>();

        if (WishListData.hasData) {

            binding.progressBar.setVisibility(View.INVISIBLE);

            WishListData.getWishListId().forEach(productId -> {
                wishListProduct.add(ProductData.fetchedProducts.get(productId));
            });

            productAdapter = new ProductAdapter(this, wishListProduct,this);
            rcvProduct.setAdapter(productAdapter);

        } else {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                boolean isSuccessful = true;
                try {
                    WishListData.fetchedWishList = new FirebaseSupportCustomer().fetchingWishList();
                } catch (IOException e) {
                    isSuccessful = false;
                }
                boolean finalIsSuccessful = isSuccessful;
                handler.post(() -> {
                    WishListData.hasData = finalIsSuccessful;

                    if (finalIsSuccessful) {
                        WishListData.getWishListId().forEach(productId -> {
                            wishListProduct.add(ProductData.fetchedProducts.get(productId));
                        });
                        productAdapter = new ProductAdapter(this, wishListProduct,this);
                        rcvProduct.setAdapter(productAdapter);
                    } else {
                        binding.informText.setVisibility(View.VISIBLE);
                    }

                    binding.progressBar.setVisibility(View.INVISIBLE);
                });
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void onRestart() {
        super.onRestart();

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onPokemonCardClick(Pokemon pokemon) {

    }

    @Override
    public void onProductCardClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product object", product);
        startActivity(intent);
    }
}