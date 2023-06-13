package com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.ProductRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportRequest;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequest;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequestAdapter;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminSupportProductRequestBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AdminSupportProductRequestActivity extends AppCompatActivity {

    private ActivityAdminSupportProductRequestBinding binding;
    private ArrayList<AdminRequest> requestList;
    private ArrayList<String> orderSortByDate;
    private AdminRequestAdapter requestAdapter;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Product Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityAdminSupportProductRequestBinding.inflate(getLayoutInflater());

        FirebaseSupportRequest firebaseSupportRequest = new FirebaseSupportRequest(this);
        firebaseSupportRequest.getRequestList(new FirebaseCallback<ArrayList<AdminRequest>>() {
            @Override
            public void onCallback(ArrayList<AdminRequest> list) {
                requestList = list;
                setUpRecyclerView();
                setUpRoleSpinner();
                binding.progressBar.setVisibility(View.INVISIBLE);

                requestAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object object, int position) {
                        Intent intent  = new Intent(AdminSupportProductRequestActivity.this, AdminResponseToRequestActivity.class);
                        intent.putExtra("AdminRequest", (AdminRequest)object);
                        startActivity(intent);
                    }
                });

                binding.spRequestSortByDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch(position) {
                            case 0: {   //Oldest -> Newest
                                Collections.sort(requestList, new Comparator<AdminRequest>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

                                    @Override
                                    public int compare(AdminRequest o1, AdminRequest o2) {
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
                                Collections.sort(requestList, new Comparator<AdminRequest>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

                                    @Override
                                    public int compare(AdminRequest o1, AdminRequest o2) {
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
                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.etRequestSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not used in this case
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchQuery = s.toString().toLowerCase();
                        //Get position of role spinner

                        ArrayList<AdminRequest> filteredList = new ArrayList<>();
                        for (AdminRequest request : requestList) {
                            String customerId = request.getCustomerId().toLowerCase();
                            if (customerId.contains(searchQuery)) {
                                filteredList.add(request);
                            }
                        }
                        requestAdapter.setRequestList(filteredList);
                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });
            }
        });

        setContentView(binding.getRoot());
    }

    public void setUpRoleSpinner() {
        orderSortByDate = new ArrayList<>();
        orderSortByDate.add("Oldest");
        orderSortByDate.add("Newest");
        //Init Spinners
        ArrayAdapter orderSortByDateSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, orderSortByDate);
        binding.spRequestSortByDate.setAdapter(orderSortByDateSpinner);
    }

    public void setUpRecyclerView() {
        requestAdapter = new AdminRequestAdapter(requestList, AdminSupportProductRequestActivity.this, R.layout.quan_request_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvProductsManagement.addItemDecoration(itemSpacingDecoration);
        binding.rvProductsManagement.setLayoutManager(new LinearLayoutManager(AdminSupportProductRequestActivity.this));
        binding.rvProductsManagement.setAdapter(requestAdapter);
    }


    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}