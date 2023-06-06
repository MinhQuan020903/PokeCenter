package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Support;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchChat;
import com.example.pokecenter.admin.Quan.AdminTab.Model.AdminProduct.AdminProductAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.AdminChatUser;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.AdminChatUserAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home.ProductsManagement.AdminProductsManagementActivity;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.databinding.FragmentAdminSupportBinding;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AdminSupportFragment extends Fragment implements View.OnTouchListener {

    private Context context;
    private FragmentAdminSupportBinding binding;
    private ArrayList<String> userRoles;
    private ArrayList<AdminChatUser> chatUsers;
    private AdminChatUserAdapter adminChatUserAdapter;
    private InputMethodManager inputMethodManager;

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

        FirebaseFetchChat firebaseFetchChat = new FirebaseFetchChat(context);
        firebaseFetchChat.getChatUserListFromFirebase(new FirebaseCallback<ArrayList<AdminChatUser>>() {
            @Override
            public void onCallback(ArrayList<AdminChatUser> user) {
                chatUsers = user;

                setUpSpinner();

                setUpRecyclerView();

                adminChatUserAdapter.setOnItemClickListener(new OnItemClickListener<AdminChatUser>() {


                    @Override
                    public void onItemClick(AdminChatUser user, int position) {

                        //Set hasSeen states of this user's messages to true
                        firebaseFetchChat.updateHasSeenStateForMessage(user.getEmail());
                        Intent intent = new Intent(getActivity(), AdminChatActivity.class);
                        intent.putExtra("AdminChatUser", user);
                        startActivity(intent);
                    }
                });
            }
        });

        binding.spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0: {   //View All
                        adminChatUserAdapter.setChatUsers(chatUsers);
                        break;
                    }
                    case 1: {   //View Customer
                        adminChatUserAdapter.setChatUsers(chatUsers.stream()
                                .filter(v -> v.getRole() == 0)
                                .collect(Collectors.toCollection(ArrayList::new)));
                        break;
                    }
                    case 2: {   //View Customer
                        adminChatUserAdapter.setChatUsers(chatUsers.stream()
                                .filter(v -> v.getRole() == 1)
                                .collect(Collectors.toCollection(ArrayList::new)));
                        break;
                    }
                }
                adminChatUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.etSenderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = s.toString().toLowerCase();
                //Get position of role spinner
                int role = binding.spUserRole.getSelectedItemPosition();

                ArrayList<AdminChatUser> filteredList = new ArrayList<>();
                for (AdminChatUser user : chatUsers) {
                    String userName = user.getName().toLowerCase();

                    //If selected role in spinner is "All"
                    if (role == 0) {
                        if (userName.contains(searchQuery)) {
                            filteredList.add(user);
                        }
                    } else {    //If selected role in spinner is "Customer", "Vender" or "Admin"
                        if (userName.contains(searchQuery) && user.getRole() == role - 1) {
                            filteredList.add(user);
                        }
                    }

                }
                adminChatUserAdapter.setChatUsers(filteredList);
                adminChatUserAdapter.notifyDataSetChanged();
            }

            // user types in email
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            // Get the focus of the EditText
            binding.etSenderSearch.requestFocus();

            // Show the soft keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(binding.etSenderSearch, InputMethodManager.SHOW_IMPLICIT);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // Check if the touch event is outside the EditText
            if (!isTouchInsideView(motionEvent, binding.etSenderSearch)) {
                // Hide the soft keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Clear the focus from the EditText
                binding.etSenderSearch.clearFocus();
            }
        }
        return false;
    }

    private boolean isTouchInsideView(MotionEvent motionEvent, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        int touchX = (int) motionEvent.getRawX();
        int touchY = (int) motionEvent.getRawY();

        return touchX >= viewX && touchX <= (viewX + view.getWidth()) && touchY >= viewY && touchY <= (viewY + view.getHeight());
    }

    private void setUpSpinner() {
        //Bind spinner
        userRoles = new ArrayList<>();
        userRoles.add("All");
        userRoles.add("Customer");
        userRoles.add("Vender");

        //Init UserRoleSpinner
        ArrayAdapter userRoleSpinner = new ArrayAdapter<>(context, R.layout.quan_sender_role_spinner_item, userRoles);
        binding.spUserRole.setAdapter(userRoleSpinner);
    }

    private void setUpRecyclerView() {
        adminChatUserAdapter = new AdminChatUserAdapter(context, R.layout.quan_admin_chat_user_item , chatUsers);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvMessageSenders.addItemDecoration(itemSpacingDecoration);
        binding.rvMessageSenders.setLayoutManager(new LinearLayoutManager(context));
        binding.rvMessageSenders.setAdapter(adminChatUserAdapter);
    }
}