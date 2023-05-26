package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityCustomerInfoAndStatisticBinding;
import com.example.pokecenter.vender.VenderTab.Home.RevenueData;
import com.example.pokecenter.vender.VenderTab.Home.RevenueProduct;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerInfoAndStatisticActivity extends AppCompatActivity {

    private ActivityCustomerInfoAndStatisticBinding binding;
    private ArrayList<String> dates;
    private InputMethodManager inputMethodManager;

    private ArrayList<RevenueData> revenues = new ArrayList<>();
    private ArrayList<RevenueProduct> productRevenues = new ArrayList<>();
    List<BarEntry> entries = new ArrayList<>();
    List<PieEntry> pieEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Customer Info and Statistic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityCustomerInfoAndStatisticBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpChooseDateForStatisticSpinner();

        //Mock Data
        setUpMockData();

        //Update the chart when user choose other spinner's option
        binding.spChooseDateForStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setUpChooseDateForStatisticSpinner() {
        dates = new ArrayList<>();
        dates.add("Day");
        dates.add("Month");
        dates.add("Year");

        ArrayAdapter dateAdapter = new ArrayAdapter(this, R.layout.quan_sender_role_spinner_item, dates);
        binding.spChooseDateForStatistic.setAdapter(dateAdapter);
    }

    private void setUpMockData() {
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