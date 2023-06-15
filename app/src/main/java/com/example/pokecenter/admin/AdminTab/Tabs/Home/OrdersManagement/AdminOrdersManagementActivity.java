package com.example.pokecenter.admin.AdminTab.Tabs.Home.OrdersManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportOrder;
import com.example.pokecenter.admin.AdminTab.Model.Order.AdminOrderAdapter;
import com.example.pokecenter.admin.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminOrdersManagementBinding;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class AdminOrdersManagementActivity extends AppCompatActivity {

    private ActivityAdminOrdersManagementBinding binding;
    private ArrayList<Order> orderList;
    private ArrayList<String> orderSortByAlphabet;
    private ArrayList<String> orderSortByPrice;
    private AdminOrderAdapter adminOrderAdapter;
    private Collator collator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.quan_light_green));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.quan_light_green;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        getSupportActionBar().setTitle("Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAdminOrdersManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Create a comparator for Vietnamese
        collator = Collator.getInstance(new Locale("vi"));

        orderList = new ArrayList<>();

        FirebaseSupportOrder firebaseSupportOrder = new FirebaseSupportOrder(this);
        firebaseSupportOrder.getOrderListFromFirebase(new FirebaseCallback<ArrayList<Order>>() {
            @Override
            public void onCallback(ArrayList<Order> orders) {
                orderList = orders;
                //Set up spinner
                setUpRoleSpinner();
                //Set up recyclerview for user
                setUpRecyclerView();

                //Sort products by alphabet by default
                //Collections.sort(orderList, Comparator.comparing(Order::getTotalAmount, collator));


                //Set onitemclick when user choose role filter
                binding.spOrderSortByAlphabet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //Oldest -> Newest
                                Collections.sort(orderList, new Comparator<Order>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

                                    @Override
                                    public int compare(Order o1, Order o2) {
                                        try {
                                            Date date1 = dateFormat.parse(o1.getCreateDate());
                                            Date date2 = dateFormat.parse(o2.getCreateDate());
                                            assert date1 != null;
                                            return date1.compareTo(date2);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        return 0;
                                    }
                                });
                                break;
                            }
                            case 1: {   //Z-A
                                Collections.sort(orderList, new Comparator<Order>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

                                    @Override
                                    public int compare(Order o1, Order o2) {
                                        try {
                                            Date date1 = dateFormat.parse(o1.getCreateDate());
                                            Date date2 = dateFormat.parse(o2.getCreateDate());
                                            assert date1 != null;
                                            return date2.compareTo(date1);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        return 0;
                                    }
                                });
                                break;
                            }
                        }
                        adminOrderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.spOrderSortByPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //Ascending by price
                                Collections.sort(orderList, new Comparator<Order>() {
                                    @Override
                                    public int compare(Order o1, Order o2) {
                                        Long totalAmount1 = o1.getTotalAmount();
                                        Long totalAmount2 = o2.getTotalAmount();
                                        return totalAmount1.compareTo(totalAmount2);
                                    }
                                });
                                break;
                            }
                            case 1: {   //Descending by price
                                Collections.sort(orderList, new Comparator<Order>() {
                                    @Override
                                    public int compare(Order o1, Order o2) {
                                        Long totalAmount1 = o1.getTotalAmount();
                                        Long totalAmount2 = o2.getTotalAmount();
                                        return totalAmount2.compareTo(totalAmount1);
                                    }
                                });
                                break;
                            }
                        }
                        adminOrderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.etOrdersManagementSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not used in this case
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchQuery = s.toString().toLowerCase();
                        //Get position of role spinner

                        ArrayList<Order> filteredList = new ArrayList<>();
                        for (Order order : orderList) {
                            String customerId = order.getCustomerId().toLowerCase();
                            String venderId = order.getVenderId().toLowerCase();
                            if (customerId.contains(searchQuery) || venderId.contains(searchQuery)) {
                                filteredList.add(order);
                            }
                        }
                        adminOrderAdapter.setOrderList(filteredList);
                        adminOrderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

                adminOrderAdapter.setOnItemClickListener(new OnItemClickListener<Order>() {
                    @Override
                    public void onItemClick(Order order, int position) {
                        Intent intent = new Intent(AdminOrdersManagementActivity.this, AdminOrderDetailActivity.class);
                        intent.putExtra("Order", order);
                        startActivity(intent);
                    }
                });

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setUpRoleSpinner() {
        orderSortByAlphabet = new ArrayList<>();
        orderSortByAlphabet.add("Oldest");
        orderSortByAlphabet.add("Newest");

        orderSortByPrice = new ArrayList<>();
        orderSortByPrice.add("Lowest Price");
        orderSortByPrice.add("Highest Price");
        //Init Spinners
        ArrayAdapter orderSortByAlphabetSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, orderSortByAlphabet);
        binding.spOrderSortByAlphabet.setAdapter(orderSortByAlphabetSpinner);
        ArrayAdapter orderSortByPriceSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, orderSortByPrice);
        binding.spOrderSortByPrice.setAdapter(orderSortByPriceSpinner);
    }
    public void setUpRecyclerView() {
        adminOrderAdapter = new AdminOrderAdapter(orderList, AdminOrdersManagementActivity.this, R.layout.quan_order_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvOrdersManagement.addItemDecoration(itemSpacingDecoration);
        binding.rvOrdersManagement.setLayoutManager(new LinearLayoutManager(AdminOrdersManagementActivity.this));
        binding.rvOrdersManagement.setAdapter(adminOrderAdapter);
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