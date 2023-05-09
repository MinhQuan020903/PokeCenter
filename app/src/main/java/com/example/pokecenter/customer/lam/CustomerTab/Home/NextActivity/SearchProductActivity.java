package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivitySearchProductBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchProductActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {

    private ActivitySearchProductBinding binding;

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

        binding = ActivitySearchProductBinding.inflate(getLayoutInflater());

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        /* search bar logic */
        String searchTextReceiveFromHomeFragment = getIntent().getStringExtra("searchText");
        binding.searchProductBar.setText(searchTextReceiveFromHomeFragment);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.searchProductBar.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = binding.searchProductBar.getText().toString();
                if (!searchText.isEmpty()) {
                    getCurrentFocus().clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(binding.searchProductBar.getWindowToken(), 0);
                    searchProduct(searchText);
                }
                return true;
            }
            return false;
        });

        binding.searchButton.setOnClickListener(view -> {
            String searchText = binding.searchProductBar.getText().toString();
            if (!searchText.isEmpty()) {
                getCurrentFocus().clearFocus();
                inputMethodManager.hideSoftInputFromWindow(binding.searchProductBar.getWindowToken(), 0);
                searchProduct(searchText);
            }
        });

        rcvProduct = binding.rcvFetchedProduct;
        productAdapter = new ProductAdapter(this, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        // Setup Loading Trending Product (UX)
        productAdapter.setData(ProductData.fetchedProducts.values().stream().filter(item -> item.getName().toLowerCase().contains(searchTextReceiveFromHomeFragment.toLowerCase())).collect(Collectors.toList()));
        rcvProduct.setAdapter(productAdapter);

        binding.progressBar.setVisibility(View.INVISIBLE);

        setContentView(binding.getRoot());
    }

    private void searchProduct(String searchText) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBarBg.setVisibility(View.VISIBLE);
        productAdapter.setData(ProductData.fetchedProducts.values().stream().filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList()));
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.progressBarBg.setVisibility(View.INVISIBLE);
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
        if (product.getName() != null) {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product object", product);
            startActivity(intent);
        }
    }
}