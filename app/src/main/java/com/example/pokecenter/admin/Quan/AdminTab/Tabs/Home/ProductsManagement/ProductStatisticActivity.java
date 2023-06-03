package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminOption.AdminOption;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProduct;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.OrderDetail;
import com.example.pokecenter.databinding.ActivityProductStatisticBinding;
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
import java.util.Map;

public class ProductStatisticActivity extends AppCompatActivity {

    private ActivityProductStatisticBinding binding;
    private AdminProduct adminProduct;
    private ArrayList<Order> orderList;
    private ArrayList<String> dates;

    private int quantity;
    private int orderCount;
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

                //Set product name, image and store name
                Picasso.get().load(adminProduct.getImages().get(0)).into(binding.ivProductImage);
                binding.tvProductName.setText(adminProduct.getName());
                binding.tvProductVenderId.setText(adminProduct.getVenderId());

                //Set quantity statistic
                quantity = 0;
                for (AdminOption adminOption : adminProduct.getOptions()) {
                    quantity += adminOption.getCurrentQuantity();
                }
                binding.tvProductQuantityStatistic.setText(Integer.toString(quantity));

                //Set order count statistic
                orderCount = orderList.size();
                binding.tvProductOrderStatistic.setText(String.valueOf(orderCount));

                //Set option count statistic
                binding.tvProductOptionStatistic.setText(String.valueOf(adminProduct.getOptions().size()));

                //Set total revenue statistic
                totalRevenue = 0;
                for (Order order : orderList) {
                    for (OrderDetail orderDetail : order.getDetails()) {
                        if (orderDetail.getProductId().equals(adminProduct.getId())) {
                            //Get buying amount
                            int amount = orderDetail.getQuantity();
                            //Get price of selected option
                            int selectedOption = orderDetail.getSelectedOption();
                            long price = adminProduct.getOptions().get(selectedOption).getPrice();
                            totalRevenue += price * amount;
                        }
                    }
                }
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(totalRevenue);
                binding.tvProductRevenueStatistic.setText(String.valueOf(currencyString));

                //Determine current day
                Date date = new Date();
                localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                //Setup Data for BarChart
                orderValues = new ArrayList<>();
                revenueValues = new ArrayList<>();

                //Check if customer's account has any order already
                if (orderList != null && orderList.size() != 0) {
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
                binding.bcProductChart.getAxisRight().setEnabled(false);

                //Personalized chart
                binding.bcProductChart.setDrawBorders(true);
                binding.bcProductChart.setBorderWidth(1);

                // Apply the custom ValueFormatter to the Y-axis
                binding.bcProductChart.getAxisLeft().setGranularityEnabled(true);
                binding.bcProductChart.getAxisLeft().setValueFormatter(valueFormatter);
                binding.bcProductChart.getXAxis().setValueFormatter(valueFormatter);

                //Set max value to display on screen
                binding.bcProductChart.setVisibleXRangeMaximum(12);

                binding.spChooseDateForStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (orderList != null && orderList.size() != 0) {
                            switch(position) {
                                case 0: {
                                    calculateRevenueInRecentDays();
                                    showOrderStatistic();
                                    break;
                                }
                                case 1: {
                                    calculateRevenueInCurrentMonth();
                                    showOrderStatistic();
                                    break;
                                }
                                case 2: {
                                    calculateRevenueInCurrentYear();
                                    showOrderStatistic();
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });

                binding.cvProductOrderStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOrderStatistic();
                    }
                });

                binding.cvProductRevenueStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRevenueStatistic();
                    }
                });

                binding.clProductProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProductStatisticActivity.this, AdminProductDetailActivity.class);
                        intent.putExtra("AdminProduct", adminProduct);
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

        binding.bcProductChart.getAxisLeft().setAxisMinimum(0);
        binding.bcProductChart.getAxisLeft().setAxisMaximum(maxOrderCount * 3);

        BarData data = new BarData(barDataSetOrder);
        binding.bcProductChart.setData(data);
        binding.bcProductChart.invalidate();

    }

    private void showRevenueStatistic() {

        binding.bcProductChart.getAxisLeft().setAxisMinimum(0);
        binding.bcProductChart.getAxisLeft().setAxisMaximum(maxRevenueCount * 2);

        BarData data = new BarData(barDataSetRevenue);
        binding.bcProductChart.setData(data);
        binding.bcProductChart.invalidate();
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
        for (Order order : orderList) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            //Check if this order was sent in this month
            if (orderedMonth == currentMonth) {
                for (OrderDetail orderDetail : order.getDetails()) {
                    //If this order has the product, add this order to HashMap
                    if (orderDetail.getProductId().equals(adminProduct.getId())) {
                        //Add an order count for this specific day
                        long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                        totalOrderPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));

                        //Add revenue for this specific day
                        int amount = orderDetail.getQuantity();
                        //Get price of selected option
                        int selectedOption = orderDetail.getSelectedOption();
                        long price = adminProduct.getOptions().get(selectedOption).getPrice();
                        long revenue = price * amount;
                        tempValue = (long) (totalRevenuePerDayInCurrentMonth.get(orderedDay) + revenue);
                        totalRevenuePerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
                    }
                }

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
                binding.bcProductChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 3);
                binding.bcProductChart.getXAxis().setAxisMaximum(localDate.getDayOfMonth() + 3);
            } else {
                binding.bcProductChart.getXAxis().setAxisMinimum(1);
                binding.bcProductChart.getXAxis().setAxisMaximum(7);
            }

        } else {
            binding.bcProductChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());
            binding.bcProductChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 7 - (localDate.lengthOfMonth() - localDate.getDayOfMonth()));
        }

        barDataSetOrder = new BarDataSet(orderValues, "Order");
        barDataSetOrder.setColors(ColorTemplate.COLORFUL_COLORS);

        Description description = new Description();
        description.setText("Revenue in recent days");
        binding.bcProductChart.setDescription(description);
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
        for (Order order : orderList) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedDay = orderedDate.getDayOfMonth();

            //Check if this order was sent in this month
            if (orderedMonth == currentMonth) {
                for (OrderDetail orderDetail : order.getDetails()) {
                    //If this order has the product, add this order to HashMap
                    if (orderDetail.getProductId().equals(adminProduct.getId())) {
                        //Add an order count for this specific day
                        long tempValue = totalOrderPerDayInCurrentMonth.get(orderedDay) + 1;
                        totalOrderPerDayInCurrentMonth.put(orderedDay, (int)(tempValue));

                        //Add revenue for this specific day
                        int amount = orderDetail.getQuantity();
                        //Get price of selected option
                        int selectedOption = orderDetail.getSelectedOption();
                        long price = adminProduct.getOptions().get(selectedOption).getPrice();
                        long revenue = price * amount;
                        tempValue = (long) (totalRevenuePerDayInCurrentMonth.get(orderedDay) + revenue);
                        totalRevenuePerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
                    }
                }

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
        binding.bcProductChart.getXAxis().setAxisMinimum(1);
        binding.bcProductChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());

        Description description = new Description();
        description.setText("Revenue this month");
        binding.bcProductChart.setDescription(description);
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
        for (Order order : orderList) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedYear = orderedDate.getYear();

            //Check if this order was sent in this month
            if (orderedYear == currentYear) {
                for (OrderDetail orderDetail : order.getDetails()) {
                    //If this order has the product, add this order to HashMap
                    if (orderDetail.getProductId().equals(adminProduct.getId())) {
                        //Add an order count for this specific day
                        long tempValue = totalOrderPerMonthInCurrentYear.get(orderedMonth) + 1;
                        totalOrderPerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));

                        //Add revenue for this specific day
                        int amount = orderDetail.getQuantity();
                        //Get price of selected option
                        int selectedOption = orderDetail.getSelectedOption();
                        long price = adminProduct.getOptions().get(selectedOption).getPrice();
                        long revenue = price * amount;
                        tempValue = (long) (totalRevenuePerMonthInCurrentYear.get(orderedMonth) + revenue);
                        totalRevenuePerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));
                    }
                }

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
        binding.bcProductChart.getXAxis().setAxisMinimum(1);
        binding.bcProductChart.getXAxis().setAxisMaximum(12);

        Description description = new Description();
        description.setText("Revenue this year");
        binding.bcProductChart.setDescription(description);
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