package com.example.pokecenter.vender.VenderTab.Home.Product;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityVenderProductBinding;

public class VenderProductActivity extends AppCompatActivity {
ActivityVenderProductBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenderProductBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.white)));

        /* Set color to title */
        getSupportActionBar().setTitle("My Product List");
        // getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#027B96\">Profile</font>", Html.FROM_HTML_MODE_LEGACY));

        /* Set up back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.lam_round_arrow_back_secondary_24);

        setContentView(binding.getRoot());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ninh_menu_only_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addButton) {
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}