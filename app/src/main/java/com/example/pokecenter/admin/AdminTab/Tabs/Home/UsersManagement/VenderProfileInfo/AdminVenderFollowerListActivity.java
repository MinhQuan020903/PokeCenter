package com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.VenderProfileInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportVender;
import com.example.pokecenter.admin.AdminTab.Model.User.User;
import com.example.pokecenter.admin.AdminTab.Model.User.Vender.Vender;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.UsersManagement.AdminCustomerInfoAndStatisticActivity;
import com.example.pokecenter.admin.AdminTab.Utils.ItemSpacingDecoration;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;
import com.example.pokecenter.admin.AdminTab.Model.User.UserAdapter;
import com.example.pokecenter.databinding.ActivityAdminVenderFollowerListBinding;

import java.util.ArrayList;

public class AdminVenderFollowerListActivity extends AppCompatActivity {

    private ActivityAdminVenderFollowerListBinding binding;
    private Vender vender;
    private UserAdapter venderFollowerAdapter;

    private ArrayList<User> venderFollowerList;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminVenderFollowerListBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Change ActionBar color
        int colorResource = R.color.light_primary;
        int color = ContextCompat.getColor(this, colorResource);
        String hexColor = "#" + Integer.toHexString(color).substring(2); // Removing the alpha value
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
        getSupportActionBar().setTitle("Vender's Followers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Get Vender object
        Intent intent = getIntent();
        vender = (Vender) intent.getSerializableExtra("Vender");

        venderFollowerList = new ArrayList<>();

        FirebaseSupportVender firebaseSupportVender = new FirebaseSupportVender(this);
        firebaseSupportVender.getFollowingListFromFirebase(vender, new FirebaseCallback<ArrayList<User>>() {
            @Override
            public void onCallback(ArrayList<User> user) {

                venderFollowerList = user;

                setUpRecyclerView();

                venderFollowerAdapter.setOnItemClickListener(new OnItemClickListener<User>() {
                    @Override
                    public void onItemClick(User user, int position) {
                        Intent intent = new Intent(AdminVenderFollowerListActivity.this, AdminCustomerInfoAndStatisticActivity.class);
                        intent.putExtra("Vender_Customer", user);
                        startActivity(intent);
                    }
                });
            }
        });

        setContentView(binding.getRoot());
    }

    public void setUpRecyclerView() {

        venderFollowerAdapter = new UserAdapter(venderFollowerList, AdminVenderFollowerListActivity.this, R.layout.quan_user_item);
        //Add spacing to RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(spacingInPixels);
        binding.rvVenderFollowerList.addItemDecoration(itemSpacingDecoration);
        binding.rvVenderFollowerList.setLayoutManager(new LinearLayoutManager(AdminVenderFollowerListActivity.this));
        binding.rvVenderFollowerList.setAdapter(venderFollowerAdapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}