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
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
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
import com.squareup.picasso.Picasso;

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

        Picasso.get().load(CustomerProfileFragment.currentAccount.getAvatar()).into(binding.avatarImage);

        rcvChatRoom = binding.rcvInboxList;
        chatRoomAdapter = new ChatRoomAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
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
                            chatRoom.setSenderAccount(new Account(avatar, username, role, id));

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

                binding.progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the query
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