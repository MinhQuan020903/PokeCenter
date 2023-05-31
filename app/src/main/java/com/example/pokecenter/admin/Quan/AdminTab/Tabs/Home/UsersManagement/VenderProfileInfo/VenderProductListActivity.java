package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchVender;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProductAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement.AdminProductsManagementActivity;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement.ProductInfoAndActivityActivity;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityVenderProductListBinding;

public class VenderProductListActivity extends AppCompatActivity {
    private ActivityVenderProductListBinding binding;
    private Vender vender;
    private AdminProductAdapter venderProductAdapter;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenderProductListBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Vender's Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Get Vender object
        Intent intent = getIntent();
        vender = (Vender) intent.getSerializableExtra("Vender");

        FirebaseFetchVender firebaseFetchVender = new FirebaseFetchVender(this);
        firebaseFetchVender.getProductListFromFirebase(vender, new FirebaseCallback<Vender>() {
            @Override
            public void onCallback(Vender user) {
                vender = user;
                if (vender.getProductList() != null) {
                    setUpRecyclerView();
                }

                venderProductAdapter.setOnItemClickListener(new OnItemClickListener<AdminProduct>() {
                    @Override
                    public void onItemClick(AdminProduct object, int position) {
                        Intent intent = new Intent(VenderProductListActivity.this, ProductInfoAndActivityActivity.class);
                        intent.putExtra("Product", object);
                        startActivity(intent);
                    }
                });
            }
        });

        setContentView(binding.getRoot());

    }

    public void setUpRecyclerView() {

        venderProductAdapter = new AdminProductAdapter(vender.getProductList(), VenderProductListActivity.this, R.layout.quan_product_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvVenderProductList.addItemDecoration(itemSpacingDecoration);
        binding.rvVenderProductList.setLayoutManager(new LinearLayoutManager(VenderProductListActivity.this));
        binding.rvVenderProductList.setAdapter(venderProductAdapter);
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