package com.example.pokecenter.vender.VenderTab.Home.Product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.ProductDetailActivity;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivityVenderProductBinding;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.Unit;

public class VenderProductActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {
    ActivityVenderProductBinding binding;
    private static final int ADD_PRODUCT_REQUEST_CODE = 1;

    String venderId = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    private RecyclerView rcvProduct;

    public static ProductAdapter productAdapter;

    public static List<Product> venderProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenderProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("My Product List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.outbox.setPadding(0, 0, 0, getNavigationBarHeight());


        setUpVenderProductRcv(venderId);

        /* Set up search bar */
        binding.searchProductBar.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = binding.searchProductBar.getText().toString();
                searchProduct(searchText);
                return true;
            }
            return false;

        });

        binding.searchButton.setOnClickListener(view -> {
            String searchText = binding.searchProductBar.getText().toString();
            searchProduct(searchText);
        });
    }


    private void searchProduct(String searchText) {

        if (!searchText.isEmpty()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            binding.searchProductBar.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(binding.searchProductBar.getWindowToken(), 0);

            productAdapter.setData(venderProduct.stream().filter(product -> product.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList()));
            binding.totalProduct.setVisibility(View.GONE);
            //binding.totalProduct.setText(String.valueOf(venderProduct.stream().filter(product -> product.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList()).stream().count()));
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void setUpVenderProductRcv(String venderId) {

        rcvProduct = binding.rcvGridProduct;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);
        venderProduct = ProductData.getListProducts().stream().filter(product -> product.getVenderId().equals(venderId)).collect(Collectors.toList());
        venderProduct.removeIf(product -> product.getOptions().stream()
                .allMatch(option -> option.getCurrentQuantity() == -1));
        productAdapter = new ProductAdapter(this, venderProduct, this);
        binding.totalProduct.setVisibility(View.GONE);
    //    binding.totalProduct.setText(String.valueOf(productAdapter.getItemCount()));
        rcvProduct.setAdapter(productAdapter);
    }
    public int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onPokemonCardClick(Pokemon pokemon) {

    }

    @Override
    public void onProductCardClick(Product product) {
        Intent intent = new Intent(this, VenderProductDetailActivity.class);
        intent.putExtra("product object", product);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ninh_menu_only_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addButton) {
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivityForResult(intent, ADD_PRODUCT_REQUEST_CODE);
//            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}