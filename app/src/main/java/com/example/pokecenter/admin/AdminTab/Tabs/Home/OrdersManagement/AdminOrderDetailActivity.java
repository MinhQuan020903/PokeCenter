package com.example.pokecenter.admin.AdminTab.Tabs.Home.OrdersManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.Model.Order.AdminOrderDetailAdapter;
import com.example.pokecenter.admin.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.AdminTab.Model.Order.OrderDetail;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportOrder;
import com.example.pokecenter.databinding.ActivityAdminOrderDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private ActivityAdminOrderDetailBinding binding;
    private Order order;
    private ArrayList<OrderDetail> orderDetailList;
    private AdminOrderDetailAdapter adminOrderDetailAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Order Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAdminOrderDetailBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        order = (Order) intent.getSerializableExtra("Order");

        FirebaseSupportOrder firebaseSupportOrder = new FirebaseSupportOrder(this);
        firebaseSupportOrder.getOrderDetailFromFirebase(order, new FirebaseCallback<ArrayList<OrderDetail>>() {
            @Override
            public void onCallback(ArrayList<OrderDetail> orderList) {
                orderDetailList = orderList;
                setUpRecyclerView();

                binding.tvOrderId.setText(order.getId());
                binding.tvCustomerId.setText(order.getCustomerId());
                binding.tvVenderId.setText(order.getVenderId());
                binding.tvCreateDate.setText(order.getCreateDate());

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(order.getTotalAmount());
                binding.tvOrderTotalAmount.setText(currencyString);
            }
        });


        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {

        adminOrderDetailAdapter = new AdminOrderDetailAdapter(orderDetailList, AdminOrderDetailActivity.this, R.layout.quan_order_detail_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvOrderDetails.addItemDecoration(itemSpacingDecoration);
        binding.rvOrderDetails.setLayoutManager(new LinearLayoutManager(AdminOrderDetailActivity.this));
        binding.rvOrderDetails.setAdapter(adminOrderDetailAdapter);
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