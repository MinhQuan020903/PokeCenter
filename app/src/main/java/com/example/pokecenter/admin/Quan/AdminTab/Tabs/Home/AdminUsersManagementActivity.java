package com.example.pokecenter.admin.Quan.AdminTab.Tabs.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.Quan.AdminTab.FirebaseAPI.FirebaseFetchUser;
import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.MessageSenderAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.User;
import com.example.pokecenter.admin.Quan.AdminTab.Model.User.UserAdapter;
import com.example.pokecenter.admin.Quan.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.databinding.ActivityAdminUsersManagementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminUsersManagementActivity extends AppCompatActivity {

    private ArrayList<User> usersList;
    private UserAdapter userAdapter;
    private ActivityAdminUsersManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminUsersManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("accounts");
        usersList = new ArrayList<>();

        FirebaseFetchUser firebaseFetchUser = new FirebaseFetchUser(this);
        firebaseFetchUser.getUsersListFromFirebase(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<User> users) {
                usersList = users;
                setUpRecyclerView();
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }
    public void setUpRecyclerView() {
        userAdapter = new UserAdapter(usersList, AdminUsersManagementActivity.this, R.layout.quan_user_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvUsersManagement.addItemDecoration(itemSpacingDecoration);
        binding.rvUsersManagement.setLayoutManager(new LinearLayoutManager(AdminUsersManagementActivity.this));
        binding.rvUsersManagement.setAdapter(userAdapter);
    }
}