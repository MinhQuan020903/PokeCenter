package com.example.pokecenter.admin.AdminTab.Tabs.Home.VoucherManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucherAdapter;
import com.example.pokecenter.databinding.ActivityAdminVoucherManagementBinding;

import java.util.ArrayList;

public class AdminVoucherManagementActivity extends AppCompatActivity {

    private ArrayList<AdminBlockVoucher> blockVouchers;
    private AdminBlockVoucherAdapter adminBlockVoucherAdapter;
    private ActivityAdminVoucherManagementBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminVoucherManagementBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Block Vouchers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        blockVouchers = new ArrayList<>();

        FirebaseSupportVoucher firebaseSupportVoucher = new FirebaseSupportVoucher(this);
        firebaseSupportVoucher.getBlockVoucherList(new FirebaseCallback<ArrayList<AdminBlockVoucher>>() {
            @Override
            public void onCallback(ArrayList<AdminBlockVoucher> blockVouchers1) {
                blockVouchers = blockVouchers1;
                setUpRecyclerView();

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        binding.ivAddVoucherBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminVoucherManagementActivity.this, AddBlockVoucherActivity.class);
                startActivity(intent);
            }
        });

        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {

        adminBlockVoucherAdapter = new AdminBlockVoucherAdapter(blockVouchers,  R.layout.quan_block_voucher_item, AdminVoucherManagementActivity.this);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvVoucherBlock.addItemDecoration(itemSpacingDecoration);
        binding.rvVoucherBlock.setLayoutManager(new LinearLayoutManager(AdminVoucherManagementActivity.this));
        binding.rvVoucherBlock.setAdapter(adminBlockVoucherAdapter);
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