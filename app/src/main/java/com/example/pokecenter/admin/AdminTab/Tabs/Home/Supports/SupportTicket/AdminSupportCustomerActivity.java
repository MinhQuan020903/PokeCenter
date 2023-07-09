package com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.SupportTicket;

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
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportUser;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequest;
import com.example.pokecenter.admin.AdminTab.Model.AdminSupportTicket.AdminSupportTicket;
import com.example.pokecenter.admin.AdminTab.Model.AdminSupportTicket.AdminSupportTicketAdapter;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.ProductRequest.AdminResponseToRequestActivity;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.Supports.ProductRequest.AdminSupportProductRequestActivity;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.ActivityAdminSupportCustomerBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AdminSupportCustomerActivity extends AppCompatActivity {
    private ActivityAdminSupportCustomerBinding binding;
    private ArrayList<AdminSupportTicket> supportTickets;
    private ArrayList<AdminSupportTicket> filteredList;
    private ArrayList<String> ticketSortByDate;
    private AdminSupportTicketAdapter adminSupportTicketAdapter;
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.light_primary;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        getSupportActionBar().setTitle("Product Support Tickets");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = ActivityAdminSupportCustomerBinding.inflate(getLayoutInflater());

        FirebaseSupportUser firebaseSupportUser = new FirebaseSupportUser(this);
        firebaseSupportUser.getUserSupportTicketList(new FirebaseCallback<ArrayList<AdminSupportTicket>>() {
            @Override
            public void onCallback(ArrayList<AdminSupportTicket> tickets) {
                supportTickets = tickets;

                setUpSpinner();
                setUpRecyclerView();
                binding.progressBar.setVisibility(View.INVISIBLE);

                adminSupportTicketAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object object, int position) {
                        Intent intent  = new Intent(AdminSupportCustomerActivity.this, AdminResponseToSupportTicketActivity.class);
                        intent.putExtra("AdminSupportTicket", (AdminSupportTicket)object);
                        startActivity(intent);
                    }
                });

                binding.spTicketSortByDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        binding.getRoot().clearFocus();

                        ArrayList<AdminSupportTicket> sortedList;
                        if (filteredList != null) {
                            sortedList = new ArrayList<>(filteredList);
                        } else {
                            sortedList = new ArrayList<>(supportTickets);
                        }
                        switch(position) {
                            case 0: {   //Oldest -> Newest
                                Collections.sort(sortedList, new Comparator<AdminSupportTicket>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

                                    @Override
                                    public int compare(AdminSupportTicket o1, AdminSupportTicket o2) {
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
                                Collections.sort(sortedList, new Comparator<AdminSupportTicket>() {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

                                    @Override
                                    public int compare(AdminSupportTicket o1, AdminSupportTicket o2) {
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
                        adminSupportTicketAdapter.setSupportTickets(sortedList);
                        adminSupportTicketAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                binding.etSupportSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not used in this case
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String searchQuery = s.toString().toLowerCase();
                        //Get position of role spinner

                        filteredList = new ArrayList<>();
                        for (AdminSupportTicket ticket : supportTickets) {
                            String customerId = ticket.getCustomerId().toLowerCase();
                            if (customerId.contains(searchQuery)) {
                                filteredList.add(ticket);
                            }
                        }
                        adminSupportTicketAdapter.setSupportTickets(filteredList);
                        adminSupportTicketAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        binding.etSupportSearch.clearFocus();
                        binding.spTicketSortByDate.clearFocus();
                    }
                });
            }
        });


        setContentView(binding.getRoot());
    }

    public void setUpSpinner() {
        ticketSortByDate = new ArrayList<>();
        ticketSortByDate.add("Oldest");
        ticketSortByDate.add("Newest");
        //Init Spinners
        ArrayAdapter ticketSortByDateSpinner = new ArrayAdapter<>(this, R.layout.quan_sender_role_spinner_item, ticketSortByDate);
        binding.spTicketSortByDate.setAdapter(ticketSortByDateSpinner);
    }

    public void setUpRecyclerView() {
        adminSupportTicketAdapter = new AdminSupportTicketAdapter(supportTickets, AdminSupportCustomerActivity.this, R.layout.quan_support_ticket_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvTicketsManagement.addItemDecoration(itemSpacingDecoration);
        binding.rvTicketsManagement.setLayoutManager(new LinearLayoutManager(AdminSupportCustomerActivity.this));
        binding.rvTicketsManagement.setAdapter(adminSupportTicketAdapter);
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