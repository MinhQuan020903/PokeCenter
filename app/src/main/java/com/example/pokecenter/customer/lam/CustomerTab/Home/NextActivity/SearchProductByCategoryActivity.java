package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivityCustomerSupportInFindingProductBinding;
import com.example.pokecenter.databinding.ActivitySearchProductByCategoryBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SearchProductByCategoryActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {

    private ActivitySearchProductByCategoryBinding binding;

    private Map<String, List<String>> categories;

    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();

    private ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivitySearchProductByCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rcvProduct.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(SearchProductByCategoryActivity.this, products, SearchProductByCategoryActivity.this);
        binding.rcvProduct.setAdapter(productAdapter);

        /* Set up Droplist down options */
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccess = true;

            try {
                categories = new FirebaseSupportCustomer().fetchingAllCategory();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            handler.post(() -> {
                if (finalIsSuccess) {

                    if (categories != null) {

                        List<String> items = new ArrayList<>(categories.keySet());

                        adapterItems = new ArrayAdapter<>(this, R.layout.lam_text_option_list_item, items);
                        binding.catetoryAutoCompleteTextView.setAdapter(adapterItems);

                        binding.catetoryAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                binding.searchBar.setText("");

                                products = categories.get(items.get(position)).stream().map(items -> ProductData.fetchedProducts.get(items)).collect(Collectors.toList());
                                productAdapter.setData(products);

                                binding.submitForm.setVisibility(View.VISIBLE);
                            }
                        });
                    }


                } else {
                    Toast.makeText(this, "Failed to connect server", Toast.LENGTH_SHORT)
                            .show();
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    productAdapter.setData(products);
                }
            }
        });

        binding.searchByTextButton.setOnClickListener(view -> {
            String searchText = binding.searchBar.getText().toString();
            if (!searchText.isEmpty()) {
                List<Product> filterProducts = products.stream().filter(product -> product.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());
                productAdapter.setData(filterProducts);
            }
        });

        binding.submitARequest.setOnClickListener(view -> {
            startActivity(new Intent(this, ActivityCustomerSupportInFindingProductBinding.class));
        });

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