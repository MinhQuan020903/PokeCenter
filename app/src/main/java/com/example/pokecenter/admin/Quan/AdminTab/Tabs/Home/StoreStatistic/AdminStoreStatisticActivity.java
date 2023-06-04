package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.StoreStatistic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchUser;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.databinding.ActivityAdminStoreStatisticBinding;
import com.example.pokecenter.databinding.ActivityAdminVenderInfoAndStatisticBinding;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class AdminStoreStatisticActivity extends AppCompatActivity {

    private ActivityAdminStoreStatisticBinding binding;

    private ArrayList<User> userList;

    private int customerCount;
    private int storeCount;
    private Long totalRevenue;

    private int maxCustomerCount;
    private int maxStoreCount;
    private Long maxTotalRevenue;

    private ArrayList<String> dates;
    private LocalDate localDate;

    private ArrayList<BarEntry> customerValues;
    private ArrayList<BarEntry> storeValues;
    private ArrayList<BarEntry> revenueValues;

    private BarDataSet barDataSetCustomer;
    private BarDataSet barDataSetStore;
    private BarDataSet barDataSetRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Store Statistic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAdminStoreStatisticBinding.inflate(getLayoutInflater());

        //Fetch user data
        FirebaseFetchUser firebaseFetchUser = new FirebaseFetchUser(this);
        firebaseFetchUser.getUsersListFromFirebase(new FirebaseCallback<ArrayList<User>>() {
            @Override
            public void onCallback(ArrayList<User> users) {
                userList = users;
                //Get customerCount and storeCount
                for (User user : userList) {
                    if (user instanceof Customer) {
                        customerCount++;
                        for (Order order : ((Customer) user).getOrderHistory()) {
                            totalRevenue += (long)(order.getTotalAmount() * 0.03);
                        }
                    } else if (user instanceof Vender) {
                        storeCount++;
                    }
                }

                //Bind stats
                binding.tvCustomerCountStatistic.setText(String.valueOf(customerCount));
                binding.tvVenderCountStatistic.setText(String.valueOf(storeCount));

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(totalRevenue);
                binding.tvStoreRevenueStatistic.setText(currencyString);
            }
        });

        setContentView(binding.getRoot());
    }
}