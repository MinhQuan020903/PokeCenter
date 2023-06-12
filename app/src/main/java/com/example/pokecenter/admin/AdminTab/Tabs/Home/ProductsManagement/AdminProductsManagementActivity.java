package com.example.pokecenter.admin.AdminTab.Tabs.Home.ProductsManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseFetchProduct;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductAdapter;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminProductsManagementBinding;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class AdminProductsManagementActivity extends AppCompatActivity {

    private ArrayList<AdminProduct> adminProductList;
    private ArrayList<String> productSortByAlphabet;
    private ArrayList<String> productSortByPrice;
    private InputMethodManager inputMethodManager;
    private AdminProductAdapter adminProductAdapter;
    private ActivityAdminProductsManagementBinding binding;
    private Collator collator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityAdminProductsManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Create a comparator for Vietnamese
        collator = Collator.getInstance(new Locale("vi"));

        adminProductList = new ArrayList<>();
        FirebaseFetchProduct firebaseFetchProduct = new FirebaseFetchProduct(this);
        firebaseFetchProduct.getProductListFromFirebase(new FirebaseCallback<ArrayList<AdminProduct>>() {
            @Override
            public void onCallback(ArrayList<AdminProduct> user) {
                adminProductList = user;
                //Set up spinner
                setUpRoleSpinner();
                //Set up recyclerview for user
                setUpRecyclerView();

                //Sort products by alphabet by default
                Collections.sort(adminProductList, Comparator.comparing(AdminProduct::getName, collator));


                //Set onitemclick when user choose role filter
                binding.spProductSortByAlphabet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //A-Z
                                Collections.sort(adminProductList, Comparator.comparing(AdminProduct::getName, collator));
                                break;
                            }
                            case 1: {   //Z-A
                                Collections.sort(adminProductList, Comparator.comparing(AdminProduct::getName, collator).reversed());
                                break;
                            }
                        }
                        adminProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.spProductSortByPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //Ascending by price
                                adminProductList.sort(Comparator.comparing(AdminProduct::getLowestPriceFromOptions));
                                break;
                            }
                            case 1: {   //Descending by price
                                adminProductList.sort(Comparator.comparing(AdminProduct::getLowestPriceFromOptions).reversed());
                                break;
                            }
                        }
                        adminProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.etProductsManagementSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not used in this case
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchQuery = s.toString().toLowerCase();
                        //Get position of role spinner

                        ArrayList<AdminProduct> filteredList = new ArrayList<>();
                        for (AdminProduct adminProduct : adminProductList) {
                            String productName = adminProduct.getName().toLowerCase();
                            if (productName.contains(searchQuery)) {
                                filteredList.add(adminProduct);
                            }

                        }
                        adminProductAdapter.setProductList(filteredList);
                        adminProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

                adminProductAdapter.setOnItemClickListener(new OnItemClickListener<AdminProduct>() {
                    @Override
                    public void onItemClick(AdminProduct object, int position) {
                        Intent intent = new Intent(AdminProductsManagementActivity.this, ProductStatisticActivity.class);
                        intent.putExtra("AdminProduct", object);
                        startActivity(intent);
                    }
                });

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setUpRoleSpinner() {
        productSortByAlphabet = new ArrayList<>();
        productSortByAlphabet.add("A-Z");
        productSortByAlphabet.add("Z-A");

        productSortByPrice = new ArrayList<>();
        productSortByPrice.add("Lowest Price");
        productSortByPrice.add("Highest Price");
        //Init Spinners
        ArrayAdapter productSortByAlphabetSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, productSortByAlphabet);
        binding.spProductSortByAlphabet.setAdapter(productSortByAlphabetSpinner);
        ArrayAdapter productSortByPriceSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, productSortByPrice);
        binding.spProductSortByPrice.setAdapter(productSortByPriceSpinner);
    }
    public void setUpRecyclerView() {
        adminProductAdapter = new AdminProductAdapter(adminProductList, AdminProductsManagementActivity.this, R.layout.quan_product_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvProductsManagement.addItemDecoration(itemSpacingDecoration);
        binding.rvProductsManagement.setLayoutManager(new LinearLayoutManager(AdminProductsManagementActivity.this));
        binding.rvProductsManagement.setAdapter(adminProductAdapter);
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