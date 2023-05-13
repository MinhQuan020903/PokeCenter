package com.example.pokecenter.vender.Ninh.VenderTab.Home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentVenderStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class VenderStatisticsFragment extends Fragment {


//  USE
    FragmentVenderStatisticsBinding binding;
    private ArrayList<RevenueData> revenues = new ArrayList<>();
    private ArrayList<RevenueProduct> productRevenues = new ArrayList<>();
    List<BarEntry> entries = new ArrayList<>();
    List<PieEntry> pieEntries = new ArrayList<>();

    public VenderStatisticsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVenderStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> dateOptions = new ArrayList<>();
        dateOptions.add("Day");
        dateOptions.add("Month");
        dateOptions.add("Year");

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dateOptions);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dateAdapter);
        binding.spinner.setSelection(0);

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
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

}