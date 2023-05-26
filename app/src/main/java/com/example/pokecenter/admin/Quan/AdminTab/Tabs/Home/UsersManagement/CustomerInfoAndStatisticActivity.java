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

        binding.spChooseDateForStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Day")) {
                    entries.clear();
                    for (int i = 0; i < revenues.size(); i++) {
                        entries.add(new BarEntry(revenues.get(i).getDate().getDayOfMonth(), revenues.get(i).getRevenue()));
                    }
                    BarDataSet dataSet = new BarDataSet(entries, "Revenue");
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    BarData data = new BarData(dataSet);
                    binding.barChart.setData(data);
                    binding.barChart.invalidate();
                }
                else if (selectedOption.equals("Month")) {
                    entries.clear();
                    for (int i = 0; i < revenues.size(); i++) {
                        entries.add(new BarEntry(revenues.get(i).getDate().getMonthValue(), revenues.get(i).getRevenue()));
                    }
                    BarDataSet dataSet = new BarDataSet(entries, "Revenue");
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    BarData data = new BarData(dataSet);
                    binding.barChart.setData(data);
                    binding.barChart.invalidate();
                }
                else if (selectedOption.equals("Year")) {
                    entries.clear();
                    for (int i = 0; i < revenues.size(); i++) {
                        entries.add(new BarEntry(revenues.get(i).getDate().getYear(), revenues.get(i).getRevenue()));
                    }
                    BarDataSet dataSet = new BarDataSet(entries, "Revenue");
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    BarData data = new BarData(dataSet);
                    binding.barChart.setData(data);
                    binding.barChart.invalidate();
                }
                binding.barChart.invalidate(); // Refresh the chart
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
        revenues.add(new RevenueData(LocalDate.of(2023, 5, 20), 1000));
        revenues.add(new RevenueData(LocalDate.of(2023, 4, 19), 1200));
        revenues.add(new RevenueData(LocalDate.of(2023, 5, 21), 1500));

        productRevenues.add(new RevenueProduct("LyNhan",1000));
        productRevenues.add(new RevenueProduct("LyNhan",1200));
        productRevenues.add(new RevenueProduct("LyNhan",1300));
        productRevenues.add(new RevenueProduct("VoTac",1000));
        productRevenues.add(new RevenueProduct("Lan",1000));
        productRevenues.add(new RevenueProduct("Nhan",1200));
        for (int i = 0; i < revenues.size(); i++) {
            entries.add(new BarEntry(revenues.get(i).getDate().getDayOfMonth(), revenues.get(i).getRevenue()));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Revenue");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);
        binding.barChart.setData(data);
        binding.barChart.invalidate();


        for (int i = 0; i < productRevenues.size(); i++) {
            pieEntries.add(new PieEntry(productRevenues.get(i).getRevenue(), productRevenues.get(i).getName()));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Product");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
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