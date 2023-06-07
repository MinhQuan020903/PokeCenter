package com.example.pokecenter.vender.VenderTab.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.ActivityAddProductBinding;
import com.example.pokecenter.databinding.ActivityChatRoomBinding;
import com.example.pokecenter.vender.Model.Chat.Message;
import com.example.pokecenter.vender.Model.Chat.MessageAdapter;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoom;
import com.example.pokecenter.vender.VenderTab.VenderProfileFragment;
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
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {
    ActivityChatRoomBinding binding;
    Account senderAccount;
    String currentId= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    private RecyclerView rcvMessage;
    private MessageAdapter messageAdapter;

    private List<Message> messageList = new ArrayList<>();

    DatabaseReference databaseReference, messageReference;
    String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        senderAccount = (Account)getIntent().getSerializableExtra("senderAccount");

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });
        Picasso.get().load(senderAccount.getAvatar()).into(binding.profileImage);
        binding.tvName.setText(senderAccount.getUsername());

        rcvMessage = binding.RclMessageList;
        messageAdapter = new MessageAdapter(this, messageList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvMessage.setLayoutManager(linearLayoutManager);
        rcvMessage.setAdapter(messageAdapter);
        if(senderAccount.getId() == "doquan020903@gmail,com" || currentId == "doquan020903@gmail,com") roomId = currentId + senderAccount.getId();
        else if(senderAccount.getRole() == 0) roomId = senderAccount.getId() + currentId;
        else roomId = currentId + senderAccount.getId();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        messageReference = FirebaseDatabase.getInstance().getReference("chats");
        Instant currentTimestamp = Instant.now();

        // Get the timestamp in milliseconds
        long timestampMillis = currentTimestamp.toEpochMilli();
//        databaseReference.child("chats").child(roomId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                messageList.clear();
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    Message message = data.getValue(Message.class);
//                    messageList.add(message);
//                }
//                messageAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("Data fetching cancelled: " + databaseError.getMessage());
//            }
//        });
        databaseReference.child("chats").child(roomId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        
        binding.cvBtnSend.setOnClickListener(view -> {
            Message MessageObject = new Message(currentId,binding.etMessage.getText().toString(),timestampMillis);
            databaseReference.child("chats").child(roomId).child("messages").push().setValue(MessageObject);
            databaseReference.child("chats").child(roomId).child("lastMessage").setValue(MessageObject.getMessageText());
            databaseReference.child("chats").child(roomId).child("lastMessageTimeStamp").setValue(MessageObject.getSendingTime());
            databaseReference.child("chats").child(roomId).child("senderId").setValue(currentId);
            binding.etMessage.setText("");
            //messageList.add(MessageObject);
            //messageAdapter.notifyDataSetChanged();
        });
        
        setContentView(binding.getRoot());
    }
}