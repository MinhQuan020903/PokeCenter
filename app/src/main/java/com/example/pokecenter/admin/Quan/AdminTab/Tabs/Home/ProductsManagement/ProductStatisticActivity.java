package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.databinding.ActivityProductStatisticBinding;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductStatisticActivity extends AppCompatActivity {

    private ActivityProductStatisticBinding binding;
    private AdminProduct adminProduct;
    private ArrayList<Order> orderList;

    private long totalRevenue;
    private int maxOrderCount;
    private int maxRevenueCount;
    private LocalDate localDate;

    private ArrayList<BarEntry> orderValues;
    private ArrayList<BarEntry> revenueValues;

    private BarDataSet barDataSetOrder;
    private BarDataSet barDataSetRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Product Statistic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityProductStatisticBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        adminProduct = (AdminProduct)intent.getSerializableExtra("AdminProduct");

        FirebaseFetchProduct firebaseFetchProduct = new FirebaseFetchProduct(this);
        firebaseFetchProduct.getProductOrderDetailFromFirebase(adminProduct, new FirebaseCallback<ArrayList<Order>>() {
            @Override
            public void onCallback(ArrayList<Order> orders) {
                orderList = orders;

                Picasso.get().load(adminProduct.getImages().get(0)).into(binding.ivProductImage);
                binding.tvProductName.setText(adminProduct.getName());
                binding.tvProductVenderId.setText(adminProduct.getVenderId());

                int quantity = 0;
                for (AdminOption adminOption : adminProduct.getOptions()) {
                    quantity += adminOption.getCurrentQuantity();
                }

                binding.tvProductQuantityStatistic.setText(Integer.toString(quantity));
                binding.tvProductOptionStatistic.setText(String.valueOf(adminProduct.getOptions().size()));
                binding.tvProductOrderStatistic.setText(String.valueOf(orderList.size()));

                for (Order order : orderList) {
                    Log.e("ORDER", order.getId());
                }

            }
        });
        setContentView(binding.getRoot());
    }

    public static LocalDate determineDateTime(String dateString) {

        LocalDate date = null;
        LocalTime time = null;
        String sDate = null;
        if (dateString.contains("at")) {
            sDate  = dateString.substring(0, dateString.indexOf("at") - 1);
        }
        else {
            sDate = dateString.substring(0, dateString.length() - 12);
        }
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("[dd/MM/yyyy]" + "[MMM dd, yyyy]" + "[MM/dd/yyyy]" + "[dd-MM-yyyy]" + "[yyyy-MM-dd]"));
        DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
        return LocalDate.parse(sDate, dateTimeFormatter);
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