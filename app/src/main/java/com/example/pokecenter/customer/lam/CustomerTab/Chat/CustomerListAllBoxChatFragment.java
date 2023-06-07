package com.example.pokecenter.customer.lam.CustomerTab.Chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.FragmentCustomerListAllBoxChatBinding;
import com.example.pokecenter.databinding.FragmentVenderChatBinding;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoom;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoomAdapter;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoomInterface;
import com.example.pokecenter.vender.VenderTab.Chat.ChatRoomActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;

public class CustomerListAllBoxChatFragment extends Fragment implements ChatRoomInterface {

    FragmentCustomerListAllBoxChatBinding binding;
    String currentId= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    private RecyclerView rcvChatRoom;
    private ChatRoomAdapter chatRoomAdapter;

    DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerListAllBoxChatBinding.inflate(inflater, container, false);
        rcvChatRoom = binding.rcvInboxList;
        chatRoomAdapter = new ChatRoomAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvChatRoom.setLayoutManager(linearLayoutManager);
        rcvChatRoom.setAdapter(chatRoomAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("accounts");

        ArrayList<ChatRoom> listChatRoom = new ArrayList<>();
        Instant currentTimestamp = Instant.now();

        // Get the timestamp in milliseconds
        long timestampMillis = currentTimestamp.toEpochMilli();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listChatRoom.clear();
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    String keyId = accountSnapshot.getKey();
                    String avatar = accountSnapshot.child("avatar").getValue(String.class);
                    String username = accountSnapshot.child("username").getValue(String.class);
                    int role = accountSnapshot.child("role").getValue(Integer.class);
                    if(role == 0)continue;
                    if(keyId.equals(currentId))continue;
                    Account newAccount = new Account(avatar,username,role,keyId);
                    ChatRoom chatRoom = new ChatRoom(null,keyId,newAccount,"",timestampMillis);
                    listChatRoom.add(chatRoom);
                }
                chatRoomAdapter.addData(listChatRoom);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Data fetching cancelled: " + databaseError.getMessage());
            }
        });
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
}