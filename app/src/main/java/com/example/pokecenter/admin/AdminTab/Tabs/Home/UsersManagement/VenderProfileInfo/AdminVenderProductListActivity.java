package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportVender;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProductAdapter;
import com.example.pokecenter.admin.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.ProductsManagement.ProductStatisticActivity;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.admin.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.databinding.ActivityAdminVenderProductListBinding;

public class AdminVenderProductListActivity extends AppCompatActivity {
    private ActivityAdminVenderProductListBinding binding;
    private Vender vender;
    private AdminProductAdapter venderProductAdapter;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminVenderProductListBinding.inflate(getLayoutInflater());

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

        FirebaseSupportVender firebaseSupportVender = new FirebaseSupportVender(this);
        firebaseSupportVender.getProductListFromFirebase(vender, new FirebaseCallback<Vender>() {
            @Override
            public void onCallback(Vender user) {
                vender = user;
                if (vender.getProductList() != null) {
                    setUpRecyclerView();
                }

                venderProductAdapter.setOnItemClickListener(new OnItemClickListener<AdminProduct>() {
                    @Override
                    public void onItemClick(AdminProduct object, int position) {
                        Intent intent = new Intent(AdminVenderProductListActivity.this, ProductStatisticActivity.class);
                        intent.putExtra("Product", object);
                        startActivity(intent);
                    }
                });
            }
        });

        setContentView(binding.getRoot());

    }

    public void setUpRecyclerView() {

        venderProductAdapter = new AdminProductAdapter(vender.getProductList(), AdminVenderProductListActivity.this, R.layout.quan_product_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvVenderProductList.addItemDecoration(itemSpacingDecoration);
        binding.rvVenderProductList.setLayoutManager(new LinearLayoutManager(AdminVenderProductListActivity.this));
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