package com.example.pokecenter.vender.VenderTab.Home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityVenderNotificationBinding;
import com.example.pokecenter.databinding.ActivityVenderStatisticsBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Model.VenderOrder.VenderDetailOrder;
import com.example.pokecenter.vender.Model.VenderOrder.VenderOrder;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VenderStatisticsActivity extends AppCompatActivity {
    ActivityVenderStatisticsBinding binding;
    private ArrayList<String> dates;
    private int orderCount;
    private long totalSpending;
    private int maxOrderCount;
    private int maxSpendingCount;
    private LocalDate localDate;

    private ArrayList<BarEntry> orderValues;
    private ArrayList<BarEntry> spendingValues;

    private BarDataSet barDataSetOrder;
    private BarDataSet barDataSetSpending;

    private ArrayList<PieEntry> orderValuesPie;
    private ArrayList<PieEntry> spendingValuesPie;

    private PieDataSet pieDataSetOrder;
    private PieDataSet pieDataSetSpending;
    private InputMethodManager inputMethodManager;
    public List<VenderOrder> venderOrders;
    List<VenderDetailOrder> venderDetailOrders;
    Map<String, List<VenderDetailOrder>> groupedDetailOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenderStatisticsBinding.inflate(getLayoutInflater());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        fetchingAndSetupData();
        getSupportActionBar().setTitle("Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Convert the long value to a currency string
        String currencyString = currencyFormat.format(totalSpending);
        binding.totalRevenue.setText(currencyString);

        //Determine current day
        Date date = new Date();
        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //Setup Data for LineChart
        orderValues = new ArrayList<>();
        spendingValues = new ArrayList<>();

        ////Setup Data for LineChart
                orderValuesPie = new ArrayList<>();
                spendingValuesPie = new ArrayList<>();
        //Check if customer's account has any order already
        if (venderOrders != null) {
            calculateSpendingInRecentDays();
            showOrderStatistic();
        }


        //Set up for choose date spinner
        setUpDateSpinner();

        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0) {
                    // Format the positive value as an integer
                    return String.valueOf((int) Math.floor(value));
                }
                return null;
            }
        };

        //Hide RightAxis
        binding.barChart.getAxisRight().setEnabled(false);
        //Personalized chart
        binding.barChart.setDrawBorders(true);
        binding.barChart.setBorderWidth(1);

        // Apply the custom ValueFormatter to the Y-axis
        binding.barChart.getAxisLeft().setGranularityEnabled(true);
        binding.barChart.getAxisLeft().setValueFormatter(valueFormatter);
        binding.barChart.getXAxis().setValueFormatter(valueFormatter);

        //Set max value to display on screen
        binding.barChart.setVisibleXRangeMaximum(12);

        //Update the chart when user choose other spinner's option
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        if (venderOrders != null) {
                            calculateSpendingInRecentDays();
                            showOrderStatistic();
                        }
                        break;
                    }
                    case 1: {
                        if (venderOrders != null) {
                            calculateSpendingInCurrentMonth();
                            showOrderStatistic();
                        }
                        break;
                    }
                    case 2: {
                        if (venderOrders != null) {
                            calculateSpendingInCurrentYear();
                            showOrderStatistic();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        binding.cvTotalBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderStatistic();
            }
        });

        binding.cvRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpendingStatistic();
            }
        });
        setContentView(binding.getRoot());
    }

    private void setUpDateSpinner() {
        dates = new ArrayList<>();
        dates.add("Recent days");
        dates.add("All month");
        dates.add("All year");
        ArrayAdapter dateAdapter = new ArrayAdapter(this, R.layout.quan_sender_role_spinner_item, dates);
        binding.spinner.setAdapter(dateAdapter);
    }

    private void showOrderStatistic() {

        binding.barChart.getAxisLeft().setAxisMinimum(0);
        binding.barChart.getAxisLeft().setAxisMaximum(maxOrderCount * 3);

        BarData data = new BarData(barDataSetOrder);
        binding.barChart.setData(data);
        binding.barChart.invalidate();

        PieData pieData = new PieData(pieDataSetOrder);
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();

    }

    private void showSpendingStatistic() {

        binding.barChart.getAxisLeft().setAxisMinimum(0);
        binding.barChart.getAxisLeft().setAxisMaximum(maxSpendingCount * 2);

        BarData data = new BarData(barDataSetSpending);
        binding.barChart.setData(data);
        binding.barChart.invalidate();

        PieData pieData = new PieData(pieDataSetSpending);
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();
    }

    private void calculateSpendingInRecentDays() {

        orderValues.clear();
        spendingValues.clear();
        orderValuesPie.clear();
        spendingValuesPie.clear();
        int currentMonth = localDate.getMonthValue();
        LocalDate orderedDate;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalOrderPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalSpendingPerDayInCurrentMonth = new HashMap<>();
        //Init base value
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            totalOrderPerDayInCurrentMonth.put(i, 0);
            totalSpendingPerDayInCurrentMonth.put(i, 0);
        }
        //Pie Chart
        LocalDate startDate = localDate.minusDays(3);
        LocalDate endDate = localDate.plusDays(3);

        // HashMap to store product names and order counts/spending
        HashMap<String, Integer> productOrderCountMap = new HashMap<>();
        HashMap<String, Integer> productSpendingMap = new HashMap<>();


        //Get date of order and put it in HashMap
        for (VenderOrder order : venderOrders) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            if (orderedMonth == currentMonth) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                totalOrderPerDayInCurrentMonth.put(orderedDay, (int) (tempValue));

                //Add spending for this specific day
                tempValue = totalSpendingPerDayInCurrentMonth.get(orderedDay) + order.getTotalAmount();
                totalSpendingPerDayInCurrentMonth.put(orderedDay, (int) (tempValue));
            }
        }

        //PieChart
        for (Map.Entry<String, List<VenderDetailOrder>> entry : groupedDetailOrders.entrySet()) {
            List<VenderDetailOrder> detailOrders = entry.getValue();
            VenderDetailOrder firstDetailOrder = detailOrders.get(0);
            String productName = firstDetailOrder.getName();
            int orderCount = 0;
            int spending = 0;

            for (VenderDetailOrder detailOrder : detailOrders) {
                orderedDate = determineDateTime(detailOrder.getCreateDate());
                int orderedMonth = orderedDate.getMonthValue();
                if (orderedMonth == currentMonth && orderedDate.isAfter(startDate) && orderedDate.isBefore(endDate)) {
                    orderCount++;
                    spending += detailOrder.getPrice() * detailOrder.getQuantity();
                }
            }

            // Update the order count for this specific product
            productOrderCountMap.put(productName, orderCount);

            // Update the spending for this specific product
            productSpendingMap.put(productName, spending);
        }


// Create PieEntry objects for order count
        for (Map.Entry<String, Integer> entry : productOrderCountMap.entrySet()) {
            String productName = entry.getKey();
            int orderCount = entry.getValue();
            if(orderCount>0) {
                PieEntry pieEntry = new PieEntry(orderCount, productName);
                orderValuesPie.add(pieEntry);
            }
        }

// Create PieEntry objects for spending
        for (Map.Entry<String, Integer> entry : productSpendingMap.entrySet()) {
            String productName = entry.getKey();
            int spending = entry.getValue();
            if(spending>0) {
                PieEntry pieEntry = new PieEntry(spending, productName);
                spendingValuesPie.add(pieEntry);
            }
        }


        //Get max value for left and right columns
        maxOrderCount = 0;
        maxSpendingCount = 0;
        //Add entry
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            if (totalOrderPerDayInCurrentMonth.get(i) != 0) {
                if (totalOrderPerDayInCurrentMonth.get(i) > maxOrderCount) {
                    maxOrderCount = totalOrderPerDayInCurrentMonth.get(i);
                }
                if (totalSpendingPerDayInCurrentMonth.get(i) > maxSpendingCount) {
                    maxSpendingCount = totalSpendingPerDayInCurrentMonth.get(i);
                }
                orderValues.add(new BarEntry(
                        i,
                        totalOrderPerDayInCurrentMonth.get(i)));
                spendingValues.add(new BarEntry(
                        i,
                        totalSpendingPerDayInCurrentMonth.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> max date of the month)
        if (localDate.getDayOfMonth() < localDate.lengthOfMonth() - 3) {
            if (localDate.getDayOfMonth() > 4) {
                binding.barChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 3);
                binding.barChart.getXAxis().setAxisMaximum(localDate.getDayOfMonth() + 3);
            } else {
                binding.barChart.getXAxis().setAxisMinimum(1);
                binding.barChart.getXAxis().setAxisMaximum(7);
            }

        } else {
            binding.barChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());
            binding.barChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 7 - (localDate.lengthOfMonth() - localDate.getDayOfMonth()));
        }

        //PieChart

        Description description = new Description();
        description.setText("Revenue this month");
        binding.barChart.setDescription(description);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetSpending = new BarDataSet(spendingValues, "Revenue");
        barDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);

        //PieChart
        binding.pieChart.setDescription(description);

        pieDataSetOrder = new PieDataSet(orderValuesPie, "Order");
        pieDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        pieDataSetSpending = new PieDataSet(spendingValuesPie, "Revenue");
        pieDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);
        
        
    }

    private void calculateSpendingInCurrentMonth() {

        orderValues.clear();
        spendingValues.clear();
        orderValuesPie.clear();
        spendingValuesPie.clear();
        int currentMonth = localDate.getMonthValue();

        LocalDate orderedDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalOrderPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalSpendingPerDayInCurrentMonth = new HashMap<>();
        //Init base value
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            totalOrderPerDayInCurrentMonth.put(i, 0);
            totalSpendingPerDayInCurrentMonth.put(i, 0);
        }


        // HashMap to store product names and order counts/spending
        HashMap<String, Integer> productOrderCountMap = new HashMap<>();
        HashMap<String, Integer> productSpendingMap = new HashMap<>();
        //Get date of order and put it in HashMap
        for (VenderOrder order : venderOrders) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            if (orderedMonth == currentMonth) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                totalOrderPerDayInCurrentMonth.put(orderedDay, (int) (tempValue));

                //Add spending for this specific day
                tempValue = totalSpendingPerDayInCurrentMonth.get(orderedDay) + order.getTotalAmount();
                totalSpendingPerDayInCurrentMonth.put(orderedDay, (int) (tempValue));
            }
        }

        //Get max value for left and right columns
        maxOrderCount = 0;
        maxSpendingCount = 0;
        //Add entry
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            if (totalOrderPerDayInCurrentMonth.get(i) != 0) {
                if (totalOrderPerDayInCurrentMonth.get(i) > maxOrderCount) {
                    maxOrderCount = totalOrderPerDayInCurrentMonth.get(i);
                }
                if (totalSpendingPerDayInCurrentMonth.get(i) > maxSpendingCount) {
                    maxSpendingCount = totalSpendingPerDayInCurrentMonth.get(i);
                }
                orderValues.add(new BarEntry(
                        i,
                        totalOrderPerDayInCurrentMonth.get(i)));
                spendingValues.add(new BarEntry(
                        i,
                        totalSpendingPerDayInCurrentMonth.get(i)));
            }
        }

        //PieChart
        for (Map.Entry<String, List<VenderDetailOrder>> entry : groupedDetailOrders.entrySet()) {
            List<VenderDetailOrder> detailOrders = entry.getValue();
            VenderDetailOrder firstDetailOrder = detailOrders.get(0);
            String productName = firstDetailOrder.getName();
            int orderCount = 0;
            int spending = 0;

            for (VenderDetailOrder detailOrder : detailOrders) {
                orderedDate = determineDateTime(detailOrder.getCreateDate());
                int orderedMonth = orderedDate.getMonthValue();
                if (orderedMonth == currentMonth ) {
                    orderCount++;
                    spending += detailOrder.getPrice() * detailOrder.getQuantity();
                }
            }

            // Update the order count for this specific product
            productOrderCountMap.put(productName, orderCount);

            // Update the spending for this specific product
            productSpendingMap.put(productName, spending);
        }


// Create PieEntry objects for order count
        for (Map.Entry<String, Integer> entry : productOrderCountMap.entrySet()) {
            String productName = entry.getKey();
            int orderCount = entry.getValue();
            if(orderCount>0) {
                PieEntry pieEntry = new PieEntry(orderCount, productName);
                orderValuesPie.add(pieEntry);
            }
        }

// Create PieEntry objects for spending
        for (Map.Entry<String, Integer> entry : productSpendingMap.entrySet()) {
            String productName = entry.getKey();
            int spending = entry.getValue();
            if(spending>0) {
                PieEntry pieEntry = new PieEntry(spending, productName);
                spendingValuesPie.add(pieEntry);
            }
        }



        //Set min and max XAxis points (1 -> max date of the month)
        binding.barChart.getXAxis().setAxisMinimum(1);
        binding.barChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());

        Description description = new Description();
        description.setText("Revenue this month");
        binding.barChart.setDescription(description);
        
        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetSpending = new BarDataSet(spendingValues, "Revenue");
        barDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);

        //PieChart
        binding.pieChart.setDescription(description);

        pieDataSetOrder = new PieDataSet(orderValuesPie, "Order");
        pieDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        pieDataSetSpending = new PieDataSet(spendingValuesPie, "Revenue");
        pieDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);
        
        

    }


    private void calculateSpendingInCurrentYear() {

        orderValues.clear();
        spendingValues.clear();
        orderValuesPie.clear();
        spendingValuesPie.clear();
        int currentYear = localDate.getYear();

        LocalDate orderedDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalOrderPerMonthInCurrentYear = new HashMap<>();
        HashMap<Integer, Integer> totalSpendingPerMonthInCurrentYear = new HashMap<>();
        //Init base value
        for (int i = 1; i <= 12; i++) {
            totalSpendingPerMonthInCurrentYear.put(i, 0);
            totalOrderPerMonthInCurrentYear.put(i, 0);
        }

        // HashMap to store product names and order counts/spending
        HashMap<String, Integer> productOrderCountMap = new HashMap<>();
        HashMap<String, Integer> productSpendingMap = new HashMap<>();

        //Get date of order and put it in HashMap
        for (VenderOrder order : venderOrders) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedYear = orderedDate.getYear();

            if (orderedYear == currentYear) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerMonthInCurrentYear.get(orderedMonth) + 1L;
                totalOrderPerMonthInCurrentYear.put(orderedMonth, (int) (tempValue));

                //Add spending for this specific day
                tempValue = totalSpendingPerMonthInCurrentYear.get(orderedMonth) + order.getTotalAmount();
                totalSpendingPerMonthInCurrentYear.put(orderedMonth, (int) (tempValue));
            }
        }

        //Get max value for left and right columns
        maxOrderCount = 0;
        maxSpendingCount = 0;
        //Add entry
        for (int i = 1; i <= 12; i++) {
            if (totalOrderPerMonthInCurrentYear.get(i) != 0) {
                if (totalOrderPerMonthInCurrentYear.get(i) > maxOrderCount) {
                    maxOrderCount = totalOrderPerMonthInCurrentYear.get(i);
                }
                if (totalSpendingPerMonthInCurrentYear.get(i) > maxSpendingCount) {
                    maxSpendingCount = totalSpendingPerMonthInCurrentYear.get(i);
                }
                orderValues.add(new BarEntry(
                        i,
                        totalOrderPerMonthInCurrentYear.get(i)));
                spendingValues.add(new BarEntry(
                        i,
                        totalSpendingPerMonthInCurrentYear.get(i)));
            }
        }

        //PieChart
        for (Map.Entry<String, List<VenderDetailOrder>> entry : groupedDetailOrders.entrySet()) {
            List<VenderDetailOrder> detailOrders = entry.getValue();
            VenderDetailOrder firstDetailOrder = detailOrders.get(0);
            String productName = firstDetailOrder.getName();
            int orderCount = 0;
            int spending = 0;

            for (VenderDetailOrder detailOrder : detailOrders) {
                orderedDate = determineDateTime(detailOrder.getCreateDate());
                int orderedYear = orderedDate.getYear();
                if (orderedYear == currentYear) {
                    orderCount++;
                    spending += detailOrder.getPrice() * detailOrder.getQuantity();
                }
            }

            // Update the order count for this specific product
            productOrderCountMap.put(productName, orderCount);

            // Update the spending for this specific product
            productSpendingMap.put(productName, spending);
        }


// Create PieEntry objects for order count
        for (Map.Entry<String, Integer> entry : productOrderCountMap.entrySet()) {
            String productName = entry.getKey();
            int orderCount = entry.getValue();
            if(orderCount>0) {
                PieEntry pieEntry = new PieEntry(orderCount, productName);
                orderValuesPie.add(pieEntry);
            }
        }

// Create PieEntry objects for spending
        for (Map.Entry<String, Integer> entry : productSpendingMap.entrySet()) {
            String productName = entry.getKey();
            int spending = entry.getValue();
            if(spending>0) {
                PieEntry pieEntry = new PieEntry(spending, productName);
                spendingValuesPie.add(pieEntry);
            }
        }



        //Set min and max XAxis points (1 -> 12)
        binding.barChart.getXAxis().setAxisMinimum(1);
        binding.barChart.getXAxis().setAxisMaximum(12);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetSpending = new BarDataSet(spendingValues, "Revenue");
        barDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);

        //PieChart

        pieDataSetOrder = new PieDataSet(orderValuesPie, "Order");
        pieDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        pieDataSetSpending = new PieDataSet(spendingValuesPie, "Revenue");
        pieDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    public static LocalDate determineDateTime(String dateString) {
        String sDate;
        if (dateString.contains("at")) {
            sDate = dateString.substring(0, dateString.indexOf("at") - 1);
        } else {
            sDate = dateString.substring(0, dateString.length() - 12);
        }
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("[dd/MM/yyyy]" + "[MMM dd, yyyy]" + "[MM/dd/yyyy]" + "[dd-MM-yyyy]" + "[yyyy-MM-dd]"));
        DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
        return LocalDate.parse(sDate, dateTimeFormatter);

    }

    private void fetchingAndSetupData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<VenderOrder> fetchedOrders = new ArrayList<>();
            boolean isSuccess = true;

            try {
                fetchedOrders = new FirebaseSupportVender().fetchingOrdersData2();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            List<VenderOrder> finalFetchedOrders = fetchedOrders;
            handler.post(() -> {

                if (finalIsSuccess) {
                    if (finalFetchedOrders.size() > 0) {
                        venderOrders = finalFetchedOrders;
                        if (venderOrders == null) {
                            orderCount = 0;
                            totalSpending = 0;
                        } else {
                            orderCount = venderOrders.size();
                            totalSpending=0;
                            for (VenderOrder order : venderOrders) {
                                totalSpending += order.getTotalAmount();
                            }
                        }
                        binding.totalBills.setText(String.valueOf(orderCount));

                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                        // Convert the long value to a currency string
                        String currencyString = currencyFormat.format(totalSpending);
                        binding.totalRevenue.setText(currencyString);
                        venderDetailOrders = new ArrayList<>();
                        for (VenderOrder order : venderOrders) {
                            for (VenderDetailOrder detail: order.getDetails()) {
                                venderDetailOrders.add(detail);
                            }
                        }
                        groupedDetailOrders = new HashMap<>();

// Duyệt qua từng DetailOrder trong danh sách detailVender
                        for (VenderDetailOrder detailOrder : venderDetailOrders) {
                            String productId = detailOrder.getProductId();

                            // Kiểm tra nếu đã có nhóm cho productId này trong Map groupedDetailOrders
                            // Nếu chưa có, thêm một danh sách mới và gán productId làm khóa
                            if (!groupedDetailOrders.containsKey(productId)) {
                                groupedDetailOrders.put(productId, new ArrayList<>());
                            }

                            // Thêm DetailOrder vào danh sách nhóm tương ứng với productId
                            groupedDetailOrders.get(productId).add(detailOrder);
                        }
                        calculateSpendingInRecentDays();
                        showOrderStatistic();
                    }
                }
            });
        });

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