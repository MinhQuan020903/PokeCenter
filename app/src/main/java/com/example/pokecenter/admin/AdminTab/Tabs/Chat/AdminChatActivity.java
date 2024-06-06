package com.example.pokecenter.admin.AdminTab.Tabs.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.ActivityAdminChatBinding;
import com.example.pokecenter.databinding.ActivityAdminUsersManagementBinding;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AdminChatActivity extends AppCompatActivity implements ChatRoomInterface {
    ActivityAdminChatBinding binding;
    String currentId= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    private RecyclerView rcvChatRoom;
    private ChatRoomAdapter chatRoomAdapter;

    DatabaseReference databaseReference;
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
        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(binding.getRoot());
        binding = ActivityAdminChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rcvChatRoom = binding.rcvInboxList;
        chatRoomAdapter = new ChatRoomAdapter(this, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvChatRoom.setLayoutManager(linearLayoutManager);
        rcvChatRoom.setAdapter(chatRoomAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        ArrayList<ChatRoom> listChatRoom = new ArrayList<>();
        databaseReference.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    if (!chatSnapshot.getKey().contains(currentId)) {
                        continue; // Skip to the next iteration if the chat snapshot doesn't contain the current ID
                    }
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setId(chatSnapshot.getKey());
                    chatRoom.setSenderId(chatSnapshot.child("senderId").getValue(String.class));
                    chatRoom.setLastMessage(chatSnapshot.child("lastMessage").getValue(String.class));
                    chatRoom.setLastMessageTimeStamp(chatSnapshot.child("lastMessageTimeStamp").getValue(Long.class));

                    String id = chatSnapshot.getKey().replaceAll(currentId, "");
                    databaseReference.child("accounts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String avatar = dataSnapshot.child("avatar").getValue(String.class);
                            String username = dataSnapshot.child("username").getValue(String.class);
                            int role = dataSnapshot.child("role").getValue(Integer.class);
                            // chatRoom.setSenderAccount(new Account(avatar, username, role, id));
                            chatRoom.setSenderAccount(new Account.Builder()
                                    .withAvatar(avatar)
                                    .withUsername(username)
                                    .withRole(role)
                                    .withId(id)
                                    .build()
                            );

                            // After setting the sender account, add the chat room to the list
                            listChatRoom.add(chatRoom);
                            chatRoomAdapter.addData(listChatRoom);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that occur during the query
                        }
                    });
                    // No need to add the chat room here as it will be added in the onDataChange() method
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the query
            }
        });
        Set<String> addedChatRoomKeys = new HashSet<>();
        ChildEventListener chatroomListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
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
                        chatRoom.setSenderAccount(new Account.Builder()
                                .withAvatar(avatar)
                                .withUsername(username)
                                .withRole(role)
                                .withId(id)
                                .build()
                        );

                        // After setting the sender account, add the chat room to the list
                        listChatRoom.add(chatRoom);
                        chatRoomAdapter.addData(listChatRoom);
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
                        updatedChatRoom.setSenderAccount(new Account.Builder()
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

        // Attach the listener to the "chats" node in the database
        databaseReference.child("chats").addChildEventListener(chatroomListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onChatRoomItemClick(ChatRoom c) {
        if (c!=null) {
            Intent intent = new Intent(this, ChatRoomActivity.class);
            intent.putExtra("senderAccount", c.getSenderAccount());
            startActivity(intent);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}