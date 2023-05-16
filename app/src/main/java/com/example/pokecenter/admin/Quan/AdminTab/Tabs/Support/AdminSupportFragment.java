package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Support;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.Model.Adapter.MessageSenderAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.FragmentAdminHomeBinding;
import com.example.pokecenter.databinding.FragmentAdminSupportBinding;

import java.util.ArrayList;

public class AdminSupportFragment extends Fragment {

    private Context context;
    private FragmentAdminSupportBinding binding;
    private ArrayList<MessageSender> messageSendersList;
    private ArrayList<MessageSender> messageCustomersList;
    private ArrayList<MessageSender> messageVendersList;
    private ArrayList<String> userRoles;
    private MessageSenderAdapter messageSenderAdapter;

    public AdminSupportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminSupportBinding.inflate(inflater, container, false);

        context = container.getContext();

        MessageSender ms1 = new MessageSender(R.drawable.lam_totoro, 0, "Do Mai Minh Quan",
                "8:46 PM", "Yowassup", 2);
        MessageSender ms2 = new MessageSender(R.drawable.lam_totoro, 1, "Tran Le Hoang Lam",
                "8:46 PM", "HMMMMM", 1);
        MessageSender ms3 = new MessageSender(R.drawable.lam_totoro, 0, "Nguyen Trong Ninh",
                "8:46 PM", "Yowassup", 2);
        messageSendersList = new ArrayList<>();
        messageCustomersList = new ArrayList<>();
        messageVendersList = new ArrayList<>();
        messageSendersList.add(ms1);
        messageSendersList.add(ms2);
        messageSendersList.add(ms3);

        for (MessageSender ms : messageSendersList) {
            if (ms.getSenderRole() == 0) {
                messageCustomersList.add(ms);
            } else if (ms.getSenderRole() == 1) {
                messageVendersList.add(ms);
            }
        }

        messageSenderAdapter = new MessageSenderAdapter(messageSendersList, context, R.layout.quan_chat_sender_item,0);
        //Add spacing to RecyclerView rvClass
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvMessageSenders.addItemDecoration(itemSpacingDecoration);

        userRoles = new ArrayList<>();
        userRoles.add("All");
        userRoles.add("Customer");
        userRoles.add("Vender");

        //Init UserRoleSpinner
        ArrayAdapter userRoleSpinner = new ArrayAdapter<>(context, R.layout.quan_sender_role_spinner_item, userRoles);
        binding.spUserRole.setAdapter(userRoleSpinner);

        binding.spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0: {   //View All
                        messageSenderAdapter.setMessageSendersList(messageSendersList);
                        break;
                    }
                    case 1: {   //View Customer
                        messageSenderAdapter.setMessageSendersList(messageCustomersList);
                        break;
                    }
                    case 2: {   //View Vender
                        messageSenderAdapter.setMessageSendersList(messageVendersList);
                        break;
                    }
                }
                messageSenderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.rvMessageSenders.setLayoutManager(new LinearLayoutManager(context));
        binding.rvMessageSenders.setAdapter(messageSenderAdapter);
        return binding.getRoot();

    }
}