package com.example.pokecenter.customer.lam.CustomerTab.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Singleton.UserInfo;
import com.example.pokecenter.databinding.FragmentCustomerListAllBoxChatBinding;
import com.example.pokecenter.databinding.FragmentVenderChatBinding;
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
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CustomerListAllBoxChatFragment extends Fragment implements View.OnTouchListener, ChatRoomInterface {

    FragmentCustomerListAllBoxChatBinding binding;
    private ArrayList<ChatRoom> listChatRoom;
    String currentId= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    private RecyclerView rcvChatRoom;
    private ChatRoomAdapter chatRoomAdapter;

    DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerListAllBoxChatBinding.inflate(inflater, container, false);

        Account currentAccount = UserInfo.getInstance().getAccount();
        Picasso.get().load(currentAccount.getAvatar()).into(binding.avatarImage);

        rcvChatRoom = binding.rcvInboxList;
        chatRoomAdapter = new ChatRoomAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvChatRoom.setLayoutManager(linearLayoutManager);
        rcvChatRoom.setAdapter(chatRoomAdapter);

        binding.progressBar.setVisibility(View.INVISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        listChatRoom = new ArrayList<>();
//        databaseReference.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
//                    if (!chatSnapshot.getKey().contains(currentId)) {
//                        continue; // Skip to the next iteration if the chat snapshot doesn't contain the current ID
//                    }
//                    ChatRoom chatRoom = new ChatRoom();
//                    chatRoom.setId(chatSnapshot.getKey());
//                    chatRoom.setSenderId(chatSnapshot.child("senderId").getValue(String.class));
//                    chatRoom.setLastMessage(chatSnapshot.child("lastMessage").getValue(String.class));
//                    chatRoom.setLastMessageTimeStamp(chatSnapshot.child("lastMessageTimeStamp").getValue(Long.class));
//
//                    String id = chatSnapshot.getKey().replaceAll(currentId, "");
//                    databaseReference.child("accounts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            String avatar = dataSnapshot.child("avatar").getValue(String.class);
//                            String username = dataSnapshot.child("username").getValue(String.class);
//                            int role = dataSnapshot.child("role").getValue(Integer.class);
//                            chatRoom.setSenderAccount(new Account(avatar, username, role, id));
//
//                            // After setting the sender account, add the chat room to the list
//                            listChatRoom.add(chatRoom);
//                            chatRoomAdapter.addData(listChatRoom);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            // Handle any errors that occur during the query
//                        }
//                    });
//                    // No need to add the chat room here as it will be added in the onDataChange() method
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle any errors that occur during the query
//            }
//        });
        Set<String> addedChatRoomKeys = new HashSet<>();
        ChildEventListener chatroomListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                binding.progressBar.setVisibility(View.VISIBLE);

                String chatRoomKey = dataSnapshot.getKey();

                // Step 2: Check if the chat room key is already in the set
                if (addedChatRoomKeys.contains(chatRoomKey)) {
                    // Chat room is already added, no need to process further
                    return;
                }
                if (!chatRoomKey.contains(currentId)) {
                    return; // Skip to the next iteration if the chat snapshot doesn't contain the current ID
                }
                // Handle new chatroom added
                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);

                String id = chatRoomKey.replaceAll(currentId, "");
                databaseReference.child("accounts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String avatar = dataSnapshot.child("avatar").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        int role = dataSnapshot.child("role").getValue(Integer.class);
                        // chatRoom.setSenderAccount(new Account(avatar, username, role, id));
                        chatRoom.setSenderAccount(new Account
                                .Builder()
                                .withAvatar(avatar)
                                .withUsername(username)
                                .withRole(role)
                                .withId(id)
                                .build()
                        );

                        // After setting the sender account, add the chat room to the list
                        listChatRoom.add(chatRoom);
                        chatRoomAdapter.addData(listChatRoom);

                        binding.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors that occur during the query
                    }
                });
                addedChatRoomKeys.add(chatRoomKey);

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
                String chatRoomKey = dataSnapshot.getKey();

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
                ChatRoom updatedChatRoom = dataSnapshot.getValue(ChatRoom.class);
                String id = chatRoomKey.replaceAll(currentId, "");
                int finalChatRoomIndex = chatRoomIndex;
                databaseReference.child("accounts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String avatar = dataSnapshot.child("avatar").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        int role = dataSnapshot.child("role").getValue(Integer.class);

                        // updatedChatRoom.setSenderAccount(new Account(avatar, username, role, id));
                        updatedChatRoom.setSenderAccount(new Account
                                .Builder()
                                .withAvatar(avatar)
                                .withUsername(username)
                                .withRole(role)
                                .withId(id)
                                .build()
                        );
                        listChatRoom.set(finalChatRoomIndex, updatedChatRoom);
                        chatRoomAdapter.addData(listChatRoom);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors that occur during the query
                    }
                });
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

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = s.toString().toLowerCase();
                //Get position of role spinner


                ArrayList<ChatRoom> filteredList = new ArrayList<>();
                for (ChatRoom chatRoom : listChatRoom) {
                    String userName = chatRoom.getSenderAccount().getUsername().toLowerCase();
                    if (userName.contains(searchQuery)) filteredList.add(chatRoom);
                }
                chatRoomAdapter.setList(filteredList);
                chatRoomAdapter.notifyDataSetChanged();
            }

            // user types in email
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            // Get the focus of the EditText
            binding.searchBar.requestFocus();

            // Show the soft keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(binding.searchBar, InputMethodManager.SHOW_IMPLICIT);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // Check if the touch event is outside the EditText
            if (!isTouchInsideView(motionEvent, binding.searchBar)) {
                // Hide the soft keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Clear the focus from the EditText
                binding.searchBar.clearFocus();
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