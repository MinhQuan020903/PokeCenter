package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.databinding.ActivitySearchProductBinding;

import java.io.IOException;

public class SearchProductActivity extends AppCompatActivity {

    private ActivitySearchProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.light_canvas));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivitySearchProductBinding.inflate(getLayoutInflater());

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        /* search bar logic */
        binding.searchProductBar.setText(getIntent().getStringExtra("searchText"));

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.searchProductBar.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = binding.searchProductBar.getText().toString();
                if (!searchText.isEmpty()) {
                    getCurrentFocus().clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(binding.searchProductBar.getWindowToken(), 0);
                    searchProduct(searchText);
                }
                return true;
            }
            return false;
        });

        binding.searchButton.setOnClickListener(view -> {
            String searchText = binding.searchProductBar.getText().toString();
            if (!searchText.isEmpty()) {
                getCurrentFocus().clearFocus();
                inputMethodManager.hideSoftInputFromWindow(binding.searchProductBar.getWindowToken(), 0);
                searchProduct(searchText);
            }
        });

        setContentView(binding.getRoot());
    }

    private void searchProduct(String searchText) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}