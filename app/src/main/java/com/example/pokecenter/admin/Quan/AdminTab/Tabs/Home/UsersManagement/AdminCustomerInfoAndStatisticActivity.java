package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.UsersManagement.CustomerProfileInfo.AdminCustomerProfileInfoActivity;
import com.example.pokecenter.databinding.ActivityAdminCustomerInfoAndStatisticBinding;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AdminCustomerInfoAndStatisticActivity extends AppCompatActivity {

    private ActivityAdminCustomerInfoAndStatisticBinding binding;
    private ArrayList<String> dates;
    private Customer customer;
    private int orderCount;
    private long totalSpending;
    private int maxOrderCount;
    private int maxSpendingCount;
    private LocalDate localDate;

    private ArrayList<BarEntry> orderValues;
    private ArrayList<BarEntry> spendingValues;

    private BarDataSet barDataSetOrder;
    private BarDataSet barDataSetSpending;

    private InputMethodManager inputMethodManager;

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

        binding = ActivityAdminCustomerInfoAndStatisticBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if ((Customer)intent.getSerializableExtra("Vender_Customer") != null) {
            customer = (Customer)intent.getSerializableExtra("Vender_Customer");
        } else {
            customer = (Customer)intent.getSerializableExtra("User");
        }

        //Bind attributes to view
        binding.tvCustomerUsername.setText(customer.getUsername());
        binding.tvCustomerEmail.setText(customer.getEmail());
        Picasso.get().load(customer.getAvatar()).into(binding.ivCustomerAvatar);

        //Calculate customer's total orders and total spending
        if (customer.getOrderHistory() == null) {
            orderCount = 0;
            totalSpending = 0;
        }
        else {
            orderCount = customer.getOrderHistory().size();
            for (Order order : customer.getOrderHistory()) {
                totalSpending += order.getTotalAmount();
            }
        }
        binding.tvCustomerOrderStatistic.setText(String.valueOf(orderCount));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Convert the long value to a currency string
        String currencyString = currencyFormat.format(totalSpending);
        binding.tvCustomerSpendingStatistic.setText(currencyString);

        //Determine current day
        Date date = new Date();
        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //Setup Data for LineChart
        orderValues = new ArrayList<>();
        spendingValues = new ArrayList<>();

        //Check if customer's account has any order already
        if (customer.getOrderHistory() != null) {
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
        binding.bcCustomerSpendingChart.getAxisRight().setEnabled(false);

        //Personalized chart
        binding.bcCustomerSpendingChart.setDrawBorders(true);
        binding.bcCustomerSpendingChart.setBorderWidth(1);

        // Apply the custom ValueFormatter to the Y-axis
        binding.bcCustomerSpendingChart.getAxisLeft().setGranularityEnabled(true);
        binding.bcCustomerSpendingChart.getAxisLeft().setValueFormatter(valueFormatter);
        binding.bcCustomerSpendingChart.getXAxis().setValueFormatter(valueFormatter);

        //Set max value to display on screen
        binding.bcCustomerSpendingChart.setVisibleXRangeMaximum(12);

        //Update the chart when user choose other spinner's option
        binding.spChooseDateForStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0: {
                        if (customer.getOrderHistory() != null) {
                            calculateSpendingInRecentDays();
                            showOrderStatistic();
                        }
                        break;
                    }
                    case 1: {
                        if (customer.getOrderHistory() != null) {
                            calculateSpendingInCurrentMonth();
                            showOrderStatistic();
                        }
                        break;
                    }
                    case 2: {
                        if (customer.getOrderHistory() != null) {
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

        binding.cvCustomerOrderStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customer.getOrderHistory() != null && customer.getOrderHistory().size() > 0) {
                    showOrderStatistic();
                }
            }
        });

        binding.cvCustomerSpendingStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customer.getOrderHistory() != null && customer.getOrderHistory().size() > 0) {
                    showSpendingStatistic();
                }
            }
        });

        binding.clCustomerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCustomerInfoAndStatisticActivity.this, AdminCustomerProfileInfoActivity.class);
                intent.putExtra("Customer", customer);
                startActivity(intent);
            }
        });
    }

    private void setUpDateSpinner() {
        dates = new ArrayList<>();
        dates.add("Recent days");
        dates.add("All month");
        dates.add("All year");
        ArrayAdapter dateAdapter = new ArrayAdapter(this, R.layout.quan_sender_role_spinner_item, dates);
        binding.spChooseDateForStatistic.setAdapter(dateAdapter);
    }

    private void showOrderStatistic() {

        binding.bcCustomerSpendingChart.getAxisLeft().setAxisMinimum(0);
        binding.bcCustomerSpendingChart.getAxisLeft().setAxisMaximum(maxOrderCount * 3);

        BarData data = new BarData(barDataSetOrder);
        binding.bcCustomerSpendingChart.setData(data);
        binding.bcCustomerSpendingChart.invalidate();

    }

    private void showSpendingStatistic() {

        binding.bcCustomerSpendingChart.getAxisLeft().setAxisMinimum(0);
        binding.bcCustomerSpendingChart.getAxisLeft().setAxisMaximum(maxSpendingCount * 2);

        BarData data = new BarData(barDataSetSpending);
        binding.bcCustomerSpendingChart.setData(data);
        binding.bcCustomerSpendingChart.invalidate();
    }

    private void calculateSpendingInRecentDays() {

        orderValues.clear();
        spendingValues.clear();

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


        //Get date of order and put it in HashMap
        for (Order order : customer.getOrderHistory()) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            if (orderedMonth == currentMonth) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                totalOrderPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));

                //Add spending for this specific day
                tempValue = totalSpendingPerDayInCurrentMonth.get(orderedDay) + order.getTotalAmount();
                totalSpendingPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
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
                binding.bcCustomerSpendingChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 3);
                binding.bcCustomerSpendingChart.getXAxis().setAxisMaximum(localDate.getDayOfMonth() + 3);
            } else {
                binding.bcCustomerSpendingChart.getXAxis().setAxisMinimum(1);
                binding.bcCustomerSpendingChart.getXAxis().setAxisMaximum(7);
            }

        } else {
            binding.bcCustomerSpendingChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());
            binding.bcCustomerSpendingChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 7 - (localDate.lengthOfMonth() - localDate.getDayOfMonth()));
        }

        Description description = new Description();
        description.setText("Spending this month");
        binding.bcCustomerSpendingChart.setDescription(description);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetSpending = new BarDataSet(spendingValues, "Spending");
        barDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);


    }

    private void calculateSpendingInCurrentMonth() {

        orderValues.clear();
        spendingValues.clear();

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

        //Get date of order and put it in HashMap
        for (Order order : customer.getOrderHistory()) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            if (orderedMonth == currentMonth) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                totalOrderPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));

                //Add spending for this specific day
                tempValue = totalSpendingPerDayInCurrentMonth.get(orderedDay) + order.getTotalAmount();
                totalSpendingPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
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
        binding.bcCustomerSpendingChart.getXAxis().setAxisMinimum(1);
        binding.bcCustomerSpendingChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());

        Description description = new Description();
        description.setText("Spending this month");
        binding.bcCustomerSpendingChart.setDescription(description);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetSpending = new BarDataSet(spendingValues, "Spending");
        barDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);

    }


    private void calculateSpendingInCurrentYear() {

        orderValues.clear();
        spendingValues.clear();

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

        //Get date of order and put it in HashMap
        for (Order order : customer.getOrderHistory()) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedYear = orderedDate.getYear();

            if (orderedYear == currentYear) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerMonthInCurrentYear.get(orderedMonth) + 1L;
                totalOrderPerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));

                //Add spending for this specific day
                tempValue = totalSpendingPerMonthInCurrentYear.get(orderedMonth) + order.getTotalAmount();
                totalSpendingPerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));
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

        //Set min and max XAxis points (1 -> 12)
        binding.bcCustomerSpendingChart.getXAxis().setAxisMinimum(1);
        binding.bcCustomerSpendingChart.getXAxis().setAxisMaximum(12);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetSpending = new BarDataSet(spendingValues, "Spending");
        barDataSetSpending.setColors(ColorTemplate.JOYFUL_COLORS);
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