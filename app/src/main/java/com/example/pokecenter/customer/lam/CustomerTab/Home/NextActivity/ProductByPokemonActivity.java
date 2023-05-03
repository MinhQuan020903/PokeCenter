package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivityProductByPokemonBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductByPokemonActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {
    private ActivityProductByPokemonBinding binding;
    private RecyclerView rcvProduct;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.light_canvas));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivityProductByPokemonBinding.inflate(getLayoutInflater());

        String receivePokemonName = getIntent().getStringExtra("pokemonName");

        binding.pokeName.setText(receivePokemonName);

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        rcvProduct = binding.rcvGridTrendingProduct;
        productAdapter = new ProductAdapter(this, this);
        
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);
        
        rcvProduct.setAdapter(productAdapter);
        getProductByPokemon(receivePokemonName);

        setContentView(binding.getRoot());
    }

    private void getProductByPokemon(String name) {
        List<Product> products = new ArrayList<>();
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {

            List<String> fetchedProductsId;

            try {
                fetchedProductsId = new FirebaseSupportCustomer().fetchingProductIdByPokemonName(name);
            } catch (IOException e) {
                fetchedProductsId = null;
            }

            List<String> finalFetchedProductsId = fetchedProductsId;
            handler.post(() -> {
                if (finalFetchedProductsId != null) {
                    finalFetchedProductsId.forEach(id -> {
                        products.add(ProductData.fetchedProducts.get(id));
                    });
                } else {
                    Toast.makeText(this, "Server connection failed", Toast.LENGTH_SHORT)
                            .show();
                }
                productAdapter.setData(products);
            });
        });
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

    }
}