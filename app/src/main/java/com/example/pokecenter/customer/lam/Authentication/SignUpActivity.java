package com.example.pokecenter.customer.lam.Authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.admin.AdminActivity;
import com.example.pokecenter.customer.CustomerActivity;
import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivitySignUpBinding;
import com.example.pokecenter.vender.VenderActivity;
import com.example.pokecenter.customer.lam.API.FirebaseSupport;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private int role = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.signUpScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.signUpButton.setOnClickListener(view -> {
            onClickSignUp();
        });

        TextView customerRole = binding.customerRole;
        TextView venderRole = binding.venderRole;

        customerRole.setOnClickListener(view -> {
            customerRole.setTextColor(getColor(R.color.white));
            customerRole.setBackground(getDrawable(R.drawable.lam_background_raised_secondary));
            customerRole.setTypeface(null, Typeface.BOLD);

            venderRole.setTextColor(getColor(R.color.light_secondary));
            venderRole.setBackground(getDrawable(R.drawable.lam_background_outline_secondary));
            venderRole.setTypeface(null, Typeface.NORMAL);

            role = 0;
        });

        venderRole.setOnClickListener(view -> {
            venderRole.setTextColor(getColor(R.color.white));
            venderRole.setBackground(getDrawable(R.drawable.lam_background_raised_secondary));
            venderRole.setTypeface(null, Typeface.BOLD);

            customerRole.setTextColor(getColor(R.color.light_secondary));
            customerRole.setBackground(getDrawable(R.drawable.lam_background_outline_secondary));
            customerRole.setTypeface(null, Typeface.NORMAL);

            role = 1;
        });

        setContentView(binding.getRoot());
    }

    private void onClickSignUp() {

        String username = binding.fullNameEditText.getText().toString();
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();

        if (!validateData(email, password)) {
            return;
        }

        changeInProgress(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Successful registration.", Toast.LENGTH_SHORT)
                                .show();

                        goToNextActivityWith(role);

                        new FirebaseSupport().saveUser(email, username, role);

//                        ExecutorService executor = Executors.newSingleThreadExecutor();
//                        executor.execute(() -> {
//                            try {
//                                FirebaseSupport.saveUserUsingApi(email, username, role);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        });

                    } else {
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void goToNextActivityWith(int role) {
        switch (role) {
            case 0:
                startActivity(new Intent(this, CustomerActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, VenderActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, AdminActivity.class));
                break;
        }
        finishAffinity();
    }

    boolean validateData(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            binding.passwordEditText.setError("Password length should not be less than 6");
            return false;
        }
        return true;
    }

    private void changeInProgress(boolean inProgress) {
        if (inProgress) {
            binding.signUpButton.setVisibility(View.INVISIBLE);
        } else {
            binding.signUpButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}