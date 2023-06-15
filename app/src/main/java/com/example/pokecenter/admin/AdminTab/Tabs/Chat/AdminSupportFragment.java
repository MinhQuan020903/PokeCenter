package com.example.pokecenter.admin.AdminTab.Tabs.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.User.Customer.Customer;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.example.pokecenter.admin.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.FragmentAdminSupportBinding;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoom;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoomAdapter;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoomInterface;
import com.example.pokecenter.vender.VenderTab.Chat.ChatRoomActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminSupportFragment extends Fragment implements View.OnTouchListener, ChatRoomInterface {

    private ArrayList<ChatRoom> listChatRoom;
    private ArrayList<String> userRoles;
    private Context context;

    private FragmentAdminSupportBinding binding;
    String currentId= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    private RecyclerView rcvChatRoom;
    private ChatRoomAdapter chatRoomAdapter;

    DatabaseReference databaseReference;

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

        rcvChatRoom = binding.rvMessageSenders;
        chatRoomAdapter = new ChatRoomAdapter(getContext(), this);
        context = container.getContext();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvChatRoom.setLayoutManager(linearLayoutManager);
        rcvChatRoom.setAdapter(chatRoomAdapter);


        binding.progressBar.setVisibility(View.VISIBLE);
        setUpRoleSpinner();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        listChatRoom = new ArrayList<>();
        Instant currentTimestamp = Instant.now();

        long timestampMillis = currentTimestamp.toEpochMilli();
        // Step 1: Declare a Set to store the keys of chat rooms already added
        Set<String> addedChatRoomKeys = new HashSet<>();
        ChildEventListener chatroomListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot1, String previousChildKey) {
                String chatRoomKey = dataSnapshot1.getKey();

                // Step 2: Check if the chat room key is already in the set
                if (addedChatRoomKeys.contains(chatRoomKey)) {
                    // Chat room is already added, no need to process further
                    return;
                }
                if (!chatRoomKey.contains(currentId)) {
                    return; // Skip to the next iteration if the chat snapshot doesn't contain the current ID
                }
                // Handle new chatroom added
                ChatRoom chatRoom = dataSnapshot1.getValue(ChatRoom.class);

                try {
                    chatRoom.getId();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String id = chatRoomKey.replaceAll(currentId, "");
                    databaseReference.child("accounts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String avatar = dataSnapshot.child("avatar").getValue(String.class);
                            String username = dataSnapshot.child("username").getValue(String.class);
                            int role = 0;
                            try {
                                role = dataSnapshot.child("role").getValue(Integer.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            chatRoom.setSenderAccount(new Account(avatar, username, role, id));
                            // After setting the sender account, add the chat room to the list
                            listChatRoom.add(chatRoom);
                            chatRoomAdapter.addData(listChatRoom);
                            addedChatRoomKeys.add(chatRoomKey);
                            binding.progressBar.setVisibility(View.INVISIBLE);

                            //!!Because of asynchronous with fetching from Firebase,
                            // Only bind Spinner when listChatRoom is initialized and fetched
                            binding.spUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    switch(position) {
                                        case 0: {   //View All
                                            if (listChatRoom != null) {
                                                chatRoomAdapter.setList(listChatRoom);
                                            }
                                            break;
                                        }
                                        case 1: {   //View Customer
                                            if (listChatRoom != null) {
                                                chatRoomAdapter.setList(listChatRoom.stream()
                                                        .filter(v -> v.getSenderAccount().getRole() == 0)
                                                        .collect(Collectors.toCollection(ArrayList::new)));
                                            }
                                            break;
                                        }
                                        case 2: {   //View Vender
                                            if (listChatRoom != null) {
                                                chatRoomAdapter.setList(listChatRoom.stream()
                                                        .filter(v -> v.getSenderAccount().getRole() == 1)
                                                        .collect(Collectors.toCollection(ArrayList::new)));
                                            }
                                            break;
                                        }
                                    }
                                    chatRoomAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            //!!Because of asynchronous with fetching from Firebase,
                            //Only bind SearchText when listChatRoom is initialized and fetched from Firebase
                            binding.etSenderSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    // Not used in this case
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    try {
                                        String searchQuery = s.toString().toLowerCase();
                                        //Get position of role spinner
                                        int role = binding.spUserRole.getSelectedItemPosition();

                                        ArrayList<ChatRoom> filteredList = new ArrayList<>();
                                        for (ChatRoom chatRoom : listChatRoom) {
                                            String userName = chatRoom.getSenderAccount().getUsername().toLowerCase();

                                            //If selected role in spinner is "All"
                                            if (role == 0) {
                                                if (userName.contains(searchQuery)) {
                                                    filteredList.add(chatRoom);
                                                }
                                            } else {    //If selected role in spinner is "Customer", "Vender" or "Admin"
                                                if (userName.contains(searchQuery) && chatRoom.getSenderAccount().getRole() == role - 1) {
                                                    filteredList.add(chatRoom);
                                                }
                                            }

                                        }
                                        chatRoomAdapter.setList(filteredList);
                                        chatRoomAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                // user types in email
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that occur during the query
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot1, String previousChildKey) {
                String chatRoomKey = dataSnapshot1.getKey();

                assert chatRoomKey != null;
                if (!chatRoomKey.contains(currentId)) {
                    return; // Skip to the next iteration if the chat snapshot doesn't contain the current ID
                }
                // Step 3: Check if the chat room key is in the list
                boolean isChatRoomInList = false;
                int chatRoomIndex = -1;
                for (int i = 0; i < listChatRoom.size(); i++) {
                    if (listChatRoom.get(i).getId().equals(chatRoomKey)) {
                        isChatRoomInList = true;
                        chatRoomIndex = i;
                        break;
                    }
                }

                if (!isChatRoomInList) {
                    // Chat room is not in the list, no need to process further
                    return;
                }

                // Handle chatroom changed
                ChatRoom updatedChatRoom = dataSnapshot1.getValue(ChatRoom.class);
                String id = chatRoomKey.replaceAll(currentId, "");
                int finalChatRoomIndex = chatRoomIndex;
                try {
                    databaseReference.child("accounts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String avatar = dataSnapshot.child("avatar").getValue(String.class);
                            String username = dataSnapshot.child("username").getValue(String.class);
                            int role = dataSnapshot.child("role").getValue(Integer.class);
                            updatedChatRoom.setSenderAccount(new Account(avatar, username, role, id));
                            listChatRoom.set(finalChatRoomIndex, updatedChatRoom);
                            chatRoomAdapter.addData(listChatRoom);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that occur during the query
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Update the chatroom in the list
                // After setting the sender account, add the chat room to the list
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                // Handle chatroom removed
//                ChatRoom removedChatRoom = dataSnapshot.getValue(ChatRoom.class);
//                // Remove the chatroom from the list
//                listChatRoom.remove(removedChatRoom);
//                // Remove the chat room key from the set
//                addedChatRoomKeys.remove(removedChatRoom.getId());
//                chatRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // Other overridden methods (onChildMoved, onCancelled) can be left empty or implemented if needed
        };

        // Attach the listener to the "chats" node in the database
        databaseReference.child("chats").addChildEventListener(chatroomListener);

        return binding.getRoot();
    }

    @Override
    public void onChatRoomItemClick(ChatRoom c) {
        if (c!=null) {
            Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
            intent.putExtra("senderAccount", c.getSenderAccount());
            startActivity(intent);
        }
    }

    public void setUpRoleSpinner() {
        userRoles = new ArrayList<>();
        userRoles.add("All");
        userRoles.add("Customer");
        userRoles.add("Vender");

        //Init UserRoleSpinner
        ArrayAdapter userRoleSpinner = new ArrayAdapter<>(getContext(), R.layout.quan_sender_role_spinner_item, userRoles);
        binding.spUserRole.setAdapter(userRoleSpinner);

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

}