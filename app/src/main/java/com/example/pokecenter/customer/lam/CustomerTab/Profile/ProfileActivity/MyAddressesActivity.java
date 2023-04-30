package com.example.pokecenter.customer.lam.CustomerTab.Profile.ProfileActivity;

import static androidx.core.content.ContextCompat.getColor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.address.AddressAdapter;
import com.example.pokecenter.databinding.ActivityMyAddressesBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAddressesActivity extends AppCompatActivity {

    private ActivityMyAddressesBinding binding;

    private List<Address> myAddresses = new ArrayList<>(Arrays.asList(
            new Address("1", "Tran Le Hoang Lam", "0915203143", "506 Hung Vuong", "Phường Thanh Hà, Thành phố Hội An, Quảng Nam", true),
            new Address("1", "Tran Le Hoang Lam", "0915203143",
                    "506 Hung Vuong", "Phường Thanh Hà, Thành phố Hội An, Quảng Nam", false)
            ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set statusBar Color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.white)));

        /* Set color to title */
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#027B96\">Profile</font>", Html.FROM_HTML_MODE_LEGACY));

        /* Set up back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.lam_round_arrow_back_secondary_24);

        binding = ActivityMyAddressesBinding.inflate(getLayoutInflater());

        /* Set Address ListView */
        AddressAdapter addressAdapter = new AddressAdapter(this, myAddresses);
        binding.lvAdresses.setAdapter(addressAdapter);

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}