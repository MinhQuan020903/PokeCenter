package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement;

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
import com.example.pokecenter.admin.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseFetchVender;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo.AdminVenderFollowerListActivity;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo.AdminVenderProductListActivity;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo.AdminVenderProfileInfoActivity;
import com.example.pokecenter.databinding.ActivityAdminVenderInfoAndStatisticBinding;
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

public class AdminVenderInfoAndStatisticActivity extends AppCompatActivity {

    private ActivityAdminVenderInfoAndStatisticBinding binding;
    private ArrayList<String> dates;
    private Vender vender;
    private int orderCount;
    private long totalRevenue;
    private int maxOrderCount;
    private int maxRevenueCount;
    private LocalDate localDate;

    private ArrayList<BarEntry> orderValues;
    private ArrayList<BarEntry> revenueValues;

    private BarDataSet barDataSetOrder;
    private BarDataSet barDataSetRevenue;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Vender Info and Statistic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityAdminVenderInfoAndStatisticBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        if ((Vender)intent.getSerializableExtra("Customer_Vender") != null) {
            vender = (Vender)intent.getSerializableExtra("Customer_Vender");
        } else {
            vender = (Vender)intent.getSerializableExtra("User");
        }

        FirebaseFetchVender firebaseFetchVender = new FirebaseFetchVender(this);
        firebaseFetchVender.getVenderDetailFromFirebase(vender, new FirebaseCallback<Vender>() {
            @Override
            public void onCallback(Vender user) {

                vender = user;

                //Bind attributes to view
                binding.tvVenderUsername.setText(vender.getUsername());
                binding.tvVenderEmail.setText(vender.getEmail());
                binding.tvVenderStoreName.setText(vender.getShopName());
                Picasso.get().load(vender.getAvatar()).into(binding.ivVenderAvatar);

                binding.tvVenderProductStatistic.setText(String.valueOf(vender.getTotalProduct()));
                binding.tvVenderFollowerStatistic.setText(String.valueOf(vender.getFollowCount()));

                //Calculate vender's total orders and total spending
                if (vender.getOrderHistory() == null) {
                    orderCount = 0;
                    totalRevenue = 0;
                }
                else {
                    orderCount = vender.getOrderHistory().size();
                    for (Order order : vender.getOrderHistory()) {
                        totalRevenue += (order.getTotalAmount() * 0.97);
                    }
                }

                binding.tvVenderOrderStatistic.setText(String.valueOf(orderCount));
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(totalRevenue);
                binding.tvVenderRevenueStatistic.setText(currencyString);

                //Determine current day
                Date date = new Date();
                localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                //Setup Data for BarChart
                orderValues = new ArrayList<>();
                revenueValues = new ArrayList<>();

                //Check if customer's account has any order already
                if (vender.getOrderHistory() != null) {
                    calculateRevenueInRecentDays();
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
                binding.bcVenderChart.getAxisRight().setEnabled(false);

                //Personalized chart
                binding.bcVenderChart.setDrawBorders(true);
                binding.bcVenderChart.setBorderWidth(1);

                // Apply the custom ValueFormatter to the Y-axis
                binding.bcVenderChart.getAxisLeft().setGranularityEnabled(true);
                binding.bcVenderChart.getAxisLeft().setValueFormatter(valueFormatter);
                binding.bcVenderChart.getXAxis().setValueFormatter(valueFormatter);

                //Set max value to display on screen
                binding.bcVenderChart.setVisibleXRangeMaximum(12);

                //Update the chart when user choose other spinner's option
                binding.spChooseDateForStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {
                                if (vender.getOrderHistory() != null) {
                                    calculateRevenueInRecentDays();
                                    showOrderStatistic();
                                }
                                break;
                            }
                            case 1: {
                                if (vender.getOrderHistory() != null) {
                                    calculateRevenueInCurrentMonth();
                                    showOrderStatistic();
                                }
                                break;
                            }
                            case 2: {
                                if (vender.getOrderHistory() != null) {
                                    calculateRevenueInCurrentYear();
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

                binding.cvVenderOrderStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vender.getOrderHistory() != null && vender.getOrderHistory().size() > 0) {
                            showOrderStatistic();
                        }
                    }
                });

                binding.cvVenderRevenueStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vender.getOrderHistory() != null && vender.getOrderHistory().size() > 0) {
                            showRevenueStatistic();
                        }
                    }
                });

                binding.cvVenderProductStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminVenderInfoAndStatisticActivity.this, AdminVenderProductListActivity.class);
                        intent.putExtra("Vender", vender);
                        startActivity(intent);
                    }
                });

                binding.cvVenderFollowerStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminVenderInfoAndStatisticActivity.this, AdminVenderFollowerListActivity.class);
                        intent.putExtra("Vender", vender);
                        startActivity(intent);
                    }
                });
                binding.clVenderProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminVenderInfoAndStatisticActivity.this, AdminVenderProfileInfoActivity.class);
                        intent.putExtra("Vender", vender);
                        startActivity(intent);
                    }
                });
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
        binding.spChooseDateForStatistic.setAdapter(dateAdapter);
    }

    private void showOrderStatistic() {

        binding.bcVenderChart.getAxisLeft().setAxisMinimum(0);
        binding.bcVenderChart.getAxisLeft().setAxisMaximum(maxOrderCount * 3);

        BarData data = new BarData(barDataSetOrder);
        binding.bcVenderChart.setData(data);
        binding.bcVenderChart.invalidate();

    }

    private void showRevenueStatistic() {

        binding.bcVenderChart.getAxisLeft().setAxisMinimum(0);
        binding.bcVenderChart.getAxisLeft().setAxisMaximum(maxRevenueCount * 2);

        BarData data = new BarData(barDataSetRevenue);
        binding.bcVenderChart.setData(data);
        binding.bcVenderChart.invalidate();
    }

    private void calculateRevenueInRecentDays() {

        orderValues.clear();
        revenueValues.clear();

        int currentMonth = localDate.getMonthValue();

        LocalDate orderedDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalOrderPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalRevenuePerDayInCurrentMonth = new HashMap<>();
        //Init base value
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            totalOrderPerDayInCurrentMonth.put(i, 0);
            totalRevenuePerDayInCurrentMonth.put(i, 0);
        }


        //Get date of order and put it in HashMap
        for (Order order : vender.getOrderHistory()) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            if (orderedMonth == currentMonth) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                totalOrderPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));

                //Add revenue for this specific day
                tempValue = (long) (totalRevenuePerDayInCurrentMonth.get(orderedDay) + (order.getTotalAmount() * 0.97));
                totalRevenuePerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
            }
        }

        //Get max value for left and right columns
        maxOrderCount = 0;
        maxRevenueCount = 0;
        //Add entry
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            if (totalOrderPerDayInCurrentMonth.get(i) != 0) {
                if (totalOrderPerDayInCurrentMonth.get(i) > maxOrderCount) {
                    maxOrderCount = totalOrderPerDayInCurrentMonth.get(i);
                }
                if (totalRevenuePerDayInCurrentMonth.get(i) > maxRevenueCount) {
                    maxRevenueCount = totalRevenuePerDayInCurrentMonth.get(i);
                }
                orderValues.add(new BarEntry(
                        i,
                        totalOrderPerDayInCurrentMonth.get(i)));
                revenueValues.add(new BarEntry(
                        i,
                        totalRevenuePerDayInCurrentMonth.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> max date of the month)

        if (localDate.getDayOfMonth() < localDate.lengthOfMonth() - 3) {
            if (localDate.getDayOfMonth() > 4) {
                binding.bcVenderChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 3);
                binding.bcVenderChart.getXAxis().setAxisMaximum(localDate.getDayOfMonth() + 3);
            } else {
                binding.bcVenderChart.getXAxis().setAxisMinimum(1);
                binding.bcVenderChart.getXAxis().setAxisMaximum(7);
            }

        } else {
            binding.bcVenderChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());
            binding.bcVenderChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 7 - (localDate.lengthOfMonth() - localDate.getDayOfMonth()));
        }

        Description description = new Description();
        description.setText("Revenue this month");
        binding.bcVenderChart.setDescription(description);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetRevenue = new BarDataSet(revenueValues, "Revenue");
        barDataSetRevenue.setColors(ColorTemplate.JOYFUL_COLORS);

    }

    private void calculateRevenueInCurrentMonth() {

        orderValues.clear();
        revenueValues.clear();

        int currentMonth = localDate.getMonthValue();

        LocalDate orderedDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalOrderPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalRevenuePerDayInCurrentMonth = new HashMap<>();
        //Init base value
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            totalOrderPerDayInCurrentMonth.put(i, 0);
            totalRevenuePerDayInCurrentMonth.put(i, 0);
        }

        //Get date of order and put it in HashMap
        for (Order order : vender.getOrderHistory()) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            if (orderedMonth == currentMonth) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                totalOrderPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));

                //Add spending for this specific day
                tempValue = (long) (totalRevenuePerDayInCurrentMonth.get(orderedDay) + (order.getTotalAmount() * 0.97));
                totalRevenuePerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
            }
        }

        //Get max value for left and right columns
        maxOrderCount = 0;
        maxRevenueCount = 0;
        //Add entry
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            if (totalOrderPerDayInCurrentMonth.get(i) != 0) {
                if (totalOrderPerDayInCurrentMonth.get(i) > maxOrderCount) {
                    maxOrderCount = totalOrderPerDayInCurrentMonth.get(i);
                }
                if (totalRevenuePerDayInCurrentMonth.get(i) > maxRevenueCount) {
                    maxRevenueCount = totalRevenuePerDayInCurrentMonth.get(i);
                }
                orderValues.add(new BarEntry(
                        i,
                        totalOrderPerDayInCurrentMonth.get(i)));
                revenueValues.add(new BarEntry(
                        i,
                        totalRevenuePerDayInCurrentMonth.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> max date of the month)
        binding.bcVenderChart.getXAxis().setAxisMinimum(1);
        binding.bcVenderChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());

        Description description = new Description();
        description.setText("Spending this month");
        binding.bcVenderChart.setDescription(description);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetRevenue = new BarDataSet(revenueValues, "Spending");
        barDataSetRevenue.setColors(ColorTemplate.JOYFUL_COLORS);

    }


    private void calculateRevenueInCurrentYear() {

        orderValues.clear();
        revenueValues.clear();

        int currentYear = localDate.getYear();

        LocalDate orderedDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalOrderPerMonthInCurrentYear = new HashMap<>();
        HashMap<Integer, Integer> totalRevenuePerMonthInCurrentYear = new HashMap<>();
        //Init base value
        for (int i = 1; i <= 12; i++) {
            totalRevenuePerMonthInCurrentYear.put(i, 0);
            totalOrderPerMonthInCurrentYear.put(i, 0);
        }

        //Get date of order and put it in HashMap
        for (Order order : vender.getOrderHistory()) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedYear = orderedDate.getYear();

            if (orderedYear == currentYear) {
                //Add an order count for this specific day
                long tempValue = totalOrderPerMonthInCurrentYear.get(orderedMonth) + 1L;
                totalOrderPerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));

                //Add spending for this specific day
                tempValue = (long) (totalRevenuePerMonthInCurrentYear.get(orderedMonth) + (order.getTotalAmount() * 0.97));
                totalRevenuePerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));
            }
        }

        //Get max value for left and right columns
        maxOrderCount = 0;
        maxRevenueCount = 0;
        //Add entry
        for (int i = 1; i <= 12; i++) {
            if (totalOrderPerMonthInCurrentYear.get(i) != 0) {
                if (totalOrderPerMonthInCurrentYear.get(i) > maxOrderCount) {
                    maxOrderCount = totalOrderPerMonthInCurrentYear.get(i);
                }
                if (totalRevenuePerMonthInCurrentYear.get(i) > maxRevenueCount) {
                    maxRevenueCount = totalRevenuePerMonthInCurrentYear.get(i);
                }
                orderValues.add(new BarEntry(
                        i,
                        totalOrderPerMonthInCurrentYear.get(i)));
                revenueValues.add(new BarEntry(
                        i,
                        totalRevenuePerMonthInCurrentYear.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> 12)
        binding.bcVenderChart.getXAxis().setAxisMinimum(1);
        binding.bcVenderChart.getXAxis().setAxisMaximum(12);

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetRevenue = new BarDataSet(revenueValues, "Spending");
        barDataSetRevenue.setColors(ColorTemplate.JOYFUL_COLORS);
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