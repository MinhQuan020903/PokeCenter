package com.example.pokecenter.admin.AdminTab.Tabs.Home.VoucherManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.Order.Order;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucher;
import com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher.AdminBlockVoucherAdapter;
import com.example.pokecenter.databinding.ActivityAdminVoucherManagementBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AdminVoucherManagementActivity extends AppCompatActivity {

    private ArrayList<AdminBlockVoucher> blockVouchers;
    private ArrayList<AdminBlockVoucher> filteredList;
    private ArrayList<String> orderSortByDate;
    private ArrayList<String> orderSortByPrice;

    private AdminBlockVoucherAdapter adminBlockVoucherAdapter;
    private ActivityAdminVoucherManagementBinding binding;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminVoucherManagementBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.light_primary;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        getSupportActionBar().setTitle("Block Vouchers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        blockVouchers = new ArrayList<>();

        FirebaseSupportVoucher firebaseSupportVoucher = new FirebaseSupportVoucher(this);
        firebaseSupportVoucher.getBlockVoucherList(new FirebaseCallback<ArrayList<AdminBlockVoucher>>() {
            @Override
            public void onCallback(ArrayList<AdminBlockVoucher> blockVouchers1) {
                blockVouchers = blockVouchers1;
                setUpRoleSpinner();
                setUpRecyclerView();

                binding.spSortByDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        binding.getRoot().clearFocus();

                        ArrayList<AdminBlockVoucher> sortedList;
                        //If user has search in EditText at least 1 time,
                        //Use the filteredList for spinner
                        //Since the original List was modified
                        if (filteredList != null) {
                            sortedList = new ArrayList<>(filteredList);
                        } else {
                            sortedList = new ArrayList<>(blockVouchers);
                        }
                        switch(position) {
                            case 0: {   //Oldest -> Newest
                                Collections.sort(sortedList, new Comparator<AdminBlockVoucher>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                    @Override
                                    public int compare(AdminBlockVoucher o1, AdminBlockVoucher o2) {
                                        try {
                                            Date date1 = dateFormat.parse(o1.getStartDate());
                                            Date date2 = dateFormat.parse(o2.getStartDate());
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
                                Collections.sort(sortedList, new Comparator<AdminBlockVoucher>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                    @Override
                                    public int compare(AdminBlockVoucher o1, AdminBlockVoucher o2) {
                                        try {
                                            Date date1 = dateFormat.parse(o1.getStartDate());
                                            Date date2 = dateFormat.parse(o2.getStartDate());
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
                        adminBlockVoucherAdapter.setBlockVouchers(sortedList);
                        adminBlockVoucherAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.spSortByPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        binding.getRoot().clearFocus();

                        ArrayList<AdminBlockVoucher> sortedList;
                        //If user has search in EditText at least 1 time,
                        //Use the filteredList for spinner
                        //Since the original List was modified
                        if (filteredList != null) {
                            sortedList = new ArrayList<>(filteredList);
                        } else {
                            sortedList = new ArrayList<>(blockVouchers);
                        }
                        switch(position) {
                            case 0: {   //Ascending by price
                                Collections.sort(sortedList, new Comparator<AdminBlockVoucher>() {
                                    @Override
                                    public int compare(AdminBlockVoucher o1, AdminBlockVoucher o2) {
                                        Integer totalAmount1 = o1.getValue();
                                        Integer totalAmount2 = o2.getValue();
                                        return totalAmount1.compareTo(totalAmount2);
                                    }
                                });
                                break;
                            }
                            case 1: {   //Descending by price
                                Collections.sort(sortedList, new Comparator<AdminBlockVoucher>() {
                                    @Override
                                    public int compare(AdminBlockVoucher o1, AdminBlockVoucher o2) {
                                        Integer totalAmount1 = o1.getValue();
                                        Integer totalAmount2 = o2.getValue();
                                        return totalAmount2.compareTo(totalAmount1);
                                    }
                                });
                                break;
                            }
                        }

                        adminBlockVoucherAdapter.setBlockVouchers(sortedList);
                        adminBlockVoucherAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.etVoucherBlockManagementSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not used in this case
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchQuery = s.toString().toLowerCase();
                        //Get position of role spinner

                        filteredList = new ArrayList<>();

                        for (AdminBlockVoucher blockVoucher : blockVouchers) {
                            String name = blockVoucher.getName().toLowerCase();
                            if (name.contains(searchQuery)) {
                                filteredList.add(blockVoucher);
                            }
                        }
                        adminBlockVoucherAdapter.setBlockVouchers(filteredList);
                        adminBlockVoucherAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

                binding.clBlockVoucherActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        binding.etVoucherBlockManagementSearch.clearFocus();
                        binding.spSortByPrice.clearFocus();
                        binding.spSortByDate.clearFocus();
                    }
                });


                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });




        binding.ivAddVoucherBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminVoucherManagementActivity.this, AddBlockVoucherActivity.class);
                startActivity(intent);
            }
        });

        setContentView(binding.getRoot());
    }


    public void setUpRoleSpinner() {
        orderSortByDate = new ArrayList<>();
        orderSortByDate.add("Oldest");
        orderSortByDate.add("Newest");

        orderSortByPrice = new ArrayList<>();
        orderSortByPrice.add("Lowest Price");
        orderSortByPrice.add("Highest Price");
        //Init Spinners
        ArrayAdapter orderSortByDateSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, orderSortByDate);
        binding.spSortByDate.setAdapter(orderSortByDateSpinner);
        ArrayAdapter orderSortByPriceSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, orderSortByPrice);
        binding.spSortByPrice.setAdapter(orderSortByPriceSpinner);
    }

    public void setUpRecyclerView() {

        adminBlockVoucherAdapter = new AdminBlockVoucherAdapter(blockVouchers,  R.layout.quan_block_voucher_item, AdminVoucherManagementActivity.this);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvVoucherBlock.addItemDecoration(itemSpacingDecoration);
        binding.rvVoucherBlock.setLayoutManager(new LinearLayoutManager(AdminVoucherManagementActivity.this));
        binding.rvVoucherBlock.setAdapter(adminBlockVoucherAdapter);
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