package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.StoreStatistic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchUser;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Order.OrderDetail;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.databinding.ActivityAdminStoreStatisticBinding;
import com.example.pokecenter.databinding.ActivityAdminVenderInfoAndStatisticBinding;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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

public class AdminStoreStatisticActivity extends AppCompatActivity {

    private ActivityAdminStoreStatisticBinding binding;

    private ArrayList<User> userList;

    private int customerCount;
    private int storeCount;
    private long totalRevenue;

    private int maxCustomerCount;
    private int maxStoreCount;
    private long maxTotalRevenue;
    private ArrayList<Order> orderList;

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

        //Set progress bar shown by default
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.clStoreStatisticMainLayout.setVisibility(View.INVISIBLE);


        //Init orderList
        orderList = new ArrayList<>();

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
                        if (((Customer) user).getOrderHistory() != null) {
                            for (Order order : ((Customer) user).getOrderHistory()) {
                                orderList.add(order);
                                totalRevenue += (long)(order.getTotalAmount() * 0.03);
                            }
                        }
                    } else if (user instanceof Vender) {
                        storeCount++;
                    }
                }

                setUpDateSpinner();

                //Set progress bar off
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.clStoreStatisticMainLayout.setVisibility(View.VISIBLE);

                //Bind stats
                binding.tvCustomerCountStatistic.setText(String.valueOf(customerCount));
                binding.tvVenderCountStatistic.setText(String.valueOf(storeCount));

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(totalRevenue);
                binding.tvStoreRevenueStatistic.setText(currencyString);

                //Determine current day
                Date date = new Date();
                localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                //Setup data for Barchart
                customerValues = new ArrayList<>();
                storeValues = new ArrayList<>();
                revenueValues = new ArrayList<>();

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

                //Set RightYXis invisible
                binding.bcStoreStatisticChart.getAxisRight().setEnabled(false);
                // Apply the custom ValueFormatter to the Y-axis
                binding.bcStoreStatisticChart.getAxisLeft().setValueFormatter(valueFormatter);
                binding.bcStoreStatisticChart.getXAxis().setValueFormatter(valueFormatter);

                //Check if orderList has any order already
                if (orderList != null && orderList.size() != 0) {
                    calculateInRecentDays();
                    showRevenueStatistic();
                }

                binding.cvStoreRevenueStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRevenueStatistic();
                    }
                });
                binding.cvCustomerCountStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCustomerStatistic();
                    }
                });
                binding.cvVenderCountStatistic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStoreStatistic();
                    }
                });

                binding.spChooseDateForStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: {
                                calculateInRecentDays();
                                break;
                            }
                            case 1: {
                                calculateInThisMonth();
                                break;
                            }
                            case 2: {
                                calculateInThisYear();
                                break;
                            }
                        }
                        showRevenueStatistic();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        setContentView(binding.getRoot());
    }

    private void showRevenueStatistic() {

        Description description = new Description();
        description.setText("Revenue in Recent days");
        binding.bcStoreStatisticChart.setDescription(description);

        binding.bcStoreStatisticChart.getAxisLeft().setAxisMinimum(0);
        //If there's no revenue, set default YAxis
        if (maxTotalRevenue == 0) {
            binding.bcStoreStatisticChart.getAxisLeft().setAxisMaximum(1000000);
        } else {
            binding.bcStoreStatisticChart.getAxisLeft().setAxisMaximum(maxTotalRevenue * 2);
        }

        BarData data = new BarData(barDataSetRevenue);
        binding.bcStoreStatisticChart.setData(data);
        binding.bcStoreStatisticChart.invalidate();
    }

    private void showCustomerStatistic() {

        Description description = new Description();
        description.setText("Customer Registration in Recent days");
        binding.bcStoreStatisticChart.setDescription(description);

        binding.bcStoreStatisticChart.getAxisLeft().setAxisMinimum(0);
        //If there's no customer registration, set default YAxis
        if (maxCustomerCount == 0) {
            binding.bcStoreStatisticChart.getAxisLeft().setAxisMaximum(10000);
        } else {
            binding.bcStoreStatisticChart.getAxisLeft().setAxisMaximum(maxCustomerCount * 2);
        }

        BarData data = new BarData(barDataSetCustomer);
        binding.bcStoreStatisticChart.setData(data);
        binding.bcStoreStatisticChart.invalidate();
    }

    private void showStoreStatistic() {

        Description description = new Description();
        description.setText("Store Registration in Recent days");
        binding.bcStoreStatisticChart.setDescription(description);

        binding.bcStoreStatisticChart.getAxisLeft().setAxisMinimum(0);
        //If there's no store registration, set default YAxis
        if (maxStoreCount == 0) {
            binding.bcStoreStatisticChart.getAxisLeft().setAxisMaximum(10000);
        } else {
            binding.bcStoreStatisticChart.getAxisLeft().setAxisMaximum(maxStoreCount * 2);
        }

        BarData data = new BarData(barDataSetStore);
        binding.bcStoreStatisticChart.setData(data);
        binding.bcStoreStatisticChart.invalidate();
    }


    private void calculateInRecentDays() {

        //Clear all previous data (if needed)
        customerValues.clear();
        storeValues.clear();
        revenueValues.clear();
        maxTotalRevenue = 0;
        maxStoreCount = 0;
        maxCustomerCount = 0;

        int currentMonth = localDate.getMonthValue();

        LocalDate orderedDate = null;
        LocalDate registrationDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalCustomerSignUpPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalStoreSignUpPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalRevenuePerDayInCurrentMonth = new HashMap<>();

        //Init base value
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            totalCustomerSignUpPerDayInCurrentMonth.put(i, 0);
            totalStoreSignUpPerDayInCurrentMonth.put(i, 0);
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
                //Add revenue for this specific day
                long tempValue = (long) (totalRevenuePerDayInCurrentMonth.get(orderedDay) + (order.getTotalAmount() * 0.03));
                totalRevenuePerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
            }
        }

        for (User user : userList) {
            registrationDate = determineDateTime(user.getRegistrationDate());
            int registeredMonth = registrationDate.getMonthValue();
            int registeredDay = registrationDate.getDayOfMonth();

            //Check if user register this month
            if (registeredMonth == currentMonth) {
                //Add this user to userCount based on their role (Customer or Vender)
                if (user instanceof Customer) {
                    int customerCount = totalCustomerSignUpPerDayInCurrentMonth.get(registeredDay) + 1;
                    totalCustomerSignUpPerDayInCurrentMonth.put(registeredDay, customerCount);
                } else if (user instanceof Vender){
                    int customerCount = totalStoreSignUpPerDayInCurrentMonth.get(registeredDay) + 1;
                    totalStoreSignUpPerDayInCurrentMonth.put(registeredDay, customerCount);
                }
            }
        }

        //Get max value for left and right columns
        maxTotalRevenue = 0L;
        //Add entry
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            //Total Revenue
            if (totalRevenuePerDayInCurrentMonth.get(i) != 0) {
                if (totalRevenuePerDayInCurrentMonth.get(i) > maxTotalRevenue) {
                    maxTotalRevenue = totalRevenuePerDayInCurrentMonth.get(i);
                }
                revenueValues.add(new BarEntry(
                        i,
                        totalRevenuePerDayInCurrentMonth.get(i)));
            }
            //Customer
            if (totalCustomerSignUpPerDayInCurrentMonth.get(i) != 0) {
                if (totalCustomerSignUpPerDayInCurrentMonth.get(i) > maxCustomerCount) {
                    maxCustomerCount = totalCustomerSignUpPerDayInCurrentMonth.get(i);
                }
                customerValues.add(new BarEntry(
                        i,
                        totalCustomerSignUpPerDayInCurrentMonth.get(i)));
            }
            //Vender
            if (totalStoreSignUpPerDayInCurrentMonth.get(i) != 0) {
                if (totalStoreSignUpPerDayInCurrentMonth.get(i) > maxStoreCount) {
                    maxStoreCount = totalStoreSignUpPerDayInCurrentMonth.get(i);
                }
                storeValues.add(new BarEntry(
                        i,
                        totalStoreSignUpPerDayInCurrentMonth.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> max date of the month)

        if (localDate.getDayOfMonth() < localDate.lengthOfMonth() - 3) {
            if (localDate.getDayOfMonth() > 4) {
                binding.bcStoreStatisticChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 3);
                binding.bcStoreStatisticChart.getXAxis().setAxisMaximum(localDate.getDayOfMonth() + 3);
            } else {
                binding.bcStoreStatisticChart.getXAxis().setAxisMinimum(1);
                binding.bcStoreStatisticChart.getXAxis().setAxisMaximum(7);
            }

        } else {
            binding.bcStoreStatisticChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());
            binding.bcStoreStatisticChart.getXAxis().setAxisMinimum(localDate.getDayOfMonth() - 7 - (localDate.lengthOfMonth() - localDate.getDayOfMonth()));
        }

        barDataSetRevenue = new BarDataSet(revenueValues, "TotalRevenue");
        barDataSetRevenue.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetCustomer = new BarDataSet(customerValues, "Customer");
        barDataSetCustomer.setColors(ColorTemplate.JOYFUL_COLORS);

        barDataSetStore = new BarDataSet(storeValues, "Store");
        barDataSetStore.setColors(ColorTemplate.MATERIAL_COLORS);
    }

    private void calculateInThisMonth() {

        //Clear all previous data (if needed)
        customerValues.clear();
        storeValues.clear();
        revenueValues.clear();
        maxTotalRevenue = 0;
        maxStoreCount = 0;
        maxCustomerCount = 0;

        int currentMonth = localDate.getMonthValue();

        LocalDate orderedDate = null;
        LocalDate registrationDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalCustomerSignUpPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalStoreSignUpPerDayInCurrentMonth = new HashMap<>();
        HashMap<Integer, Integer> totalRevenuePerDayInCurrentMonth = new HashMap<>();

        //Init base value
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            totalCustomerSignUpPerDayInCurrentMonth.put(i, 0);
            totalStoreSignUpPerDayInCurrentMonth.put(i, 0);
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
                //Add revenue for this specific day
                long tempValue = (long) (totalRevenuePerDayInCurrentMonth.get(orderedDay) + (order.getTotalAmount() * 0.03));
                totalRevenuePerDayInCurrentMonth.put(orderedDay, (int)(tempValue));
            }
        }

        for (User user : userList) {
            registrationDate = determineDateTime(user.getRegistrationDate());
            int registeredMonth = registrationDate.getMonthValue();
            int registeredDay = registrationDate.getDayOfMonth();

            //Check if user register this month
            if (registeredMonth == currentMonth) {
                //Add this user to userCount based on their role (Customer or Vender)
                if (user instanceof Customer) {
                    int customerCount = totalCustomerSignUpPerDayInCurrentMonth.get(registeredDay) + 1;
                    totalCustomerSignUpPerDayInCurrentMonth.put(registeredDay, customerCount);
                } else if (user instanceof Vender){
                    int customerCount = totalStoreSignUpPerDayInCurrentMonth.get(registeredDay) + 1;
                    totalStoreSignUpPerDayInCurrentMonth.put(registeredDay, customerCount);
                }
            }
        }

        //Get max value for left and right columns
        maxTotalRevenue = 0L;
        //Add entry
        for (int i = 1; i <= localDate.lengthOfMonth(); i++) {
            //Total Revenue
            if (totalRevenuePerDayInCurrentMonth.get(i) != 0) {
                if (totalRevenuePerDayInCurrentMonth.get(i) > maxTotalRevenue) {
                    maxTotalRevenue = totalRevenuePerDayInCurrentMonth.get(i);
                }
                revenueValues.add(new BarEntry(
                        i,
                        totalRevenuePerDayInCurrentMonth.get(i)));
            }
            //Customer
            if (totalCustomerSignUpPerDayInCurrentMonth.get(i) != 0) {
                if (totalCustomerSignUpPerDayInCurrentMonth.get(i) > maxCustomerCount) {
                    maxCustomerCount = totalCustomerSignUpPerDayInCurrentMonth.get(i);
                }
                customerValues.add(new BarEntry(
                        i,
                        totalCustomerSignUpPerDayInCurrentMonth.get(i)));
            }
            //Vender
            if (totalStoreSignUpPerDayInCurrentMonth.get(i) != 0) {
                if (totalStoreSignUpPerDayInCurrentMonth.get(i) > maxStoreCount) {
                    maxStoreCount = totalStoreSignUpPerDayInCurrentMonth.get(i);
                }
                storeValues.add(new BarEntry(
                        i,
                        totalStoreSignUpPerDayInCurrentMonth.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> max date of the month)
        binding.bcStoreStatisticChart.getXAxis().setAxisMinimum(1);
        binding.bcStoreStatisticChart.getXAxis().setAxisMaximum(localDate.lengthOfMonth());

        barDataSetRevenue = new BarDataSet(revenueValues, "TotalRevenue");
        barDataSetRevenue.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetCustomer = new BarDataSet(customerValues, "Customer");
        barDataSetCustomer.setColors(ColorTemplate.JOYFUL_COLORS);

        barDataSetStore = new BarDataSet(storeValues, "Store");
        barDataSetStore.setColors(ColorTemplate.MATERIAL_COLORS);
    }

    private void calculateInThisYear() {

        //Clear all previous data (if needed)
        customerValues.clear();
        storeValues.clear();
        revenueValues.clear();
        maxTotalRevenue = 0;
        maxStoreCount = 0;
        maxCustomerCount = 0;

        int currentYear = localDate.getYear();

        LocalDate orderedDate = null;
        LocalDate registrationDate = null;

        //Hashmap to store dates and order counts of that date in month
        HashMap<Integer, Integer> totalCustomerSignUpPerMonthInCurrentYear = new HashMap<>();
        HashMap<Integer, Integer> totalStoreSignUpPerMonthInCurrentYear = new HashMap<>();
        HashMap<Integer, Integer> totalRevenuePerMonthInCurrentYear = new HashMap<>();

        //Init base value
        for (int i = 1; i <= 12; i++) {
            totalCustomerSignUpPerMonthInCurrentYear.put(i, 0);
            totalStoreSignUpPerMonthInCurrentYear.put(i, 0);
            totalRevenuePerMonthInCurrentYear.put(i, 0);
        }

        //Get date of order and put it in HashMap
        for (Order order : orderList) {

            //Get date of the order
            orderedDate = determineDateTime(order.getCreateDate());
            int orderedMonth = orderedDate.getMonthValue();
            int orderedYear = orderedDate.getYear();

            //Check if this order was sent in this year
            if (orderedYear == currentYear) {
                //Add revenue for this specific month
                long tempValue = (long) (totalRevenuePerMonthInCurrentYear.get(orderedMonth) + (order.getTotalAmount() * 0.03));
                totalRevenuePerMonthInCurrentYear.put(orderedMonth, (int)(tempValue));
            }
        }

        for (User user : userList) {
            registrationDate = determineDateTime(user.getRegistrationDate());
            int registeredMonth = registrationDate.getMonthValue();
            int registeredYear = registrationDate.getYear();

            //Check if user register this month
            if (registeredYear == currentYear) {
                //Add this user to userCount based on their role (Customer or Vender)
                if (user instanceof Customer) {
                    int customerCount = totalCustomerSignUpPerMonthInCurrentYear.get(registeredMonth) + 1;
                    totalCustomerSignUpPerMonthInCurrentYear.put(registeredMonth, customerCount);
                } else if (user instanceof Vender){
                    int customerCount = totalStoreSignUpPerMonthInCurrentYear.get(registeredMonth) + 1;
                    totalStoreSignUpPerMonthInCurrentYear.put(registeredMonth, customerCount);
                }
            }
        }

        //Get max value for left and right columns
        maxTotalRevenue = 0L;
        //Add entry
        for (int i = 1; i <= 12; i++) {
            //Total Revenue
            if (totalRevenuePerMonthInCurrentYear.get(i) != 0) {
                if (totalRevenuePerMonthInCurrentYear.get(i) > maxTotalRevenue) {
                    maxTotalRevenue = totalRevenuePerMonthInCurrentYear.get(i);
                }
                revenueValues.add(new BarEntry(
                        i,
                        totalRevenuePerMonthInCurrentYear.get(i)));
            }
            //Customer
            if (totalCustomerSignUpPerMonthInCurrentYear.get(i) != 0) {
                if (totalCustomerSignUpPerMonthInCurrentYear.get(i) > maxCustomerCount) {
                    maxCustomerCount = totalCustomerSignUpPerMonthInCurrentYear.get(i);
                }
                customerValues.add(new BarEntry(
                        i,
                        totalCustomerSignUpPerMonthInCurrentYear.get(i)));
            }
            //Vender
            if (totalStoreSignUpPerMonthInCurrentYear.get(i) != 0) {
                if (totalStoreSignUpPerMonthInCurrentYear.get(i) > maxStoreCount) {
                    maxStoreCount = totalStoreSignUpPerMonthInCurrentYear.get(i);
                }
                storeValues.add(new BarEntry(
                        i,
                        totalStoreSignUpPerMonthInCurrentYear.get(i)));
            }
        }

        //Set min and max XAxis points (1 -> max date of the month)
        binding.bcStoreStatisticChart.getXAxis().setAxisMinimum(1);
        binding.bcStoreStatisticChart.getXAxis().setAxisMaximum(12);

        barDataSetRevenue = new BarDataSet(revenueValues, "TotalRevenue");
        barDataSetRevenue.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSetCustomer = new BarDataSet(customerValues, "Customer");
        barDataSetCustomer.setColors(ColorTemplate.JOYFUL_COLORS);

        barDataSetStore = new BarDataSet(storeValues, "Store");
        barDataSetStore.setColors(ColorTemplate.MATERIAL_COLORS);
    }

    private void setUpDateSpinner() {
        dates = new ArrayList<>();
        dates.add("Recent days");
        dates.add("All month");
        dates.add("All year");
        ArrayAdapter dateAdapter = new ArrayAdapter(this, R.layout.quan_sender_role_spinner_item, dates);
        binding.spChooseDateForStatistic.setAdapter(dateAdapter);
    }

    public static LocalDate determineDateTime(String dateString) {

        LocalDate date = null;
        LocalTime time = null;
        String sDate = null;
        if (dateString.contains("at")) {
            sDate  = dateString.substring(0, dateString.indexOf("at") - 1);
        }
        else if (dateString.length() > 12){
            sDate = dateString.substring(0, dateString.length() - 12);
        } else {
            sDate = dateString;
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