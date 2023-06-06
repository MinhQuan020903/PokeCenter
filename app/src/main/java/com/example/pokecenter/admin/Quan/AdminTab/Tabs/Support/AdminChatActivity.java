package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Support;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchChat;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.AdminChatUser;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.DateUtils;
import com.example.pokecenter.databinding.ActivityAdminChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class AdminChatActivity extends AppCompatActivity {

    private ActivityAdminChatBinding binding;
    private InputMethodManager inputMethodManager;
    private AdminChatUser user;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide default ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding = ActivityAdminChatBinding.inflate(getLayoutInflater());

        //Get user from intent
        Intent intent = getIntent();
        user = (AdminChatUser)intent.getSerializableExtra("AdminChatUser");

        //Bind views of user
        Picasso.get().load(user.getAvatar()).into(binding.ivUserAvatar);
        binding.tvUserName.setText(user.getName());

        //Get current account's email
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        currentEmail = firebaseUser.getEmail();

        FirebaseFetchChat firebaseFetchChat = new FirebaseFetchChat(this);


        //Turn EditText down if click outside EditText
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //Close activity when click Back
        binding.bChatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Send message
        binding.bChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Turn down EditText
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String message = binding.etMessage.getText().toString();
                if (!message.equals("")) {
                    firebaseFetchChat.sendMessage(user.getEmail(), message, DateUtils.getCurrentDateTime());
                    binding.etMessage.setText("");
                }
            }
        });
        setContentView(binding.getRoot());
    }
}