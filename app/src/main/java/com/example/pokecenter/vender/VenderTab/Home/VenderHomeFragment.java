package com.example.pokecenter.vender.VenderTab.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.databinding.FragmentVenderHomeBinding;
import com.example.pokecenter.vender.Model.Chat.Message;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoom;
import com.example.pokecenter.vender.VenderTab.Chat.VenderChatFragment;
import com.example.pokecenter.vender.VenderTab.Home.Parcel.ParcelMainActivity;
import com.example.pokecenter.vender.VenderTab.VenderNotificationsFragment;
import com.example.pokecenter.vender.VenderTab.VenderProfileFragment;
import com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VenderHomeFragment extends Fragment {
    private FragmentVenderHomeBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private OnFragmentChangeListener fragmentChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Check if the parent activity implements the OnFragmentChangeListener interface
        if (context instanceof OnFragmentChangeListener) {
            fragmentChangeListener = (OnFragmentChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentChangeListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVenderHomeBinding.inflate(inflater, container, false);
        if (VenderProfileFragment.currentVender.getAvatar() != null)
            Picasso.get().load(VenderProfileFragment.currentVender.getAvatar()).into(binding.VenderProfileImage);
        else
            Picasso.get().load("https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png").into(binding.VenderProfileImage);
        binding.StatisticsFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), VenderStatisticsActivity.class);
            startActivity(intent);
        });
        binding.ProductFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), VenderProductActivity.class);
            startActivity(intent);
        });
        binding.NotificationFunction.setOnClickListener(view -> {
            fragmentChangeListener.onFragmentChange(new VenderNotificationsFragment());
        });
        binding.chatFunction.setOnClickListener(view -> {
            fragmentChangeListener.onFragmentChange(new VenderChatFragment());
        });
        binding.parcelFunction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ParcelMainActivity.class);
            startActivity(intent);
        });
        binding.voucherFunction.setOnClickListener(view -> {

        });
//        databaseReference.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                               @Override
//                                                                               public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                                   accounts.clear();
//                                                                                   for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
//                                                                                       String keyId = accountSnapshot.getKey();
//                                                                                       if (keyId.equals("doquan020903@gmail,com")) continue;
//                                                                                       String avatar = accountSnapshot.child("avatar").getValue(String.class);
//                                                                                       String username = accountSnapshot.child("username").getValue(String.class);
//                                                                                       int role = accountSnapshot.child("role").getValue(Integer.class);
//
//                                                                                       Account newAccount = new Account(avatar, username, role, keyId);
//                                                                                       newAccount.setRegistrationDate(accountSnapshot.child("registrationDate").getValue(String.class));
//                                                                                       accounts.add(newAccount);
//                                                                                   }
//                                                                               }
//
//                                                                               @Override
//                                                                               public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                               }
//                                                                           });
//        binding.chatFunction.setOnClickListener(view -> {
//                    for (Account a : accounts) {
//                        long timestamp = 0;
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                        try {
//                            Date date = dateFormat.parse(a.getRegistrationDate());
//                            timestamp = date.getTime();
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        String roomId = "doquan020903@gmail,com" + a.getId();
//                        Message MessageObject = new Message("doquan020903@gmail,com", "Cảm ơn bạn đã sử dụng ứng dụng PokeCenter", timestamp);
//                        databaseReference.child("chats").child(roomId).child("messages").push().setValue(MessageObject);
//                        databaseReference.child("chats").child(roomId).child("lastMessage").setValue(MessageObject.getMessageText());
//                        databaseReference.child("chats").child(roomId).child("lastMessageTimeStamp").setValue(MessageObject.getSendingTime());
//                        databaseReference.child("chats").child(roomId).child("senderId").setValue("doquan020903@gmail,com");
//                    }
//                }
//        );
        return binding.getRoot();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        fragmentChangeListener = null;
    }

    public interface OnFragmentChangeListener {
        void onFragmentChange(Fragment fragment);
    }

}