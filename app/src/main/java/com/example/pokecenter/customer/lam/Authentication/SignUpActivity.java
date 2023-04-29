package com.example.pokecenter.customer.lam.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.CustomerActivity;
import com.example.pokecenter.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

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

        setContentView(binding.getRoot());
    }

    private void onClickSignUp() {

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
                        startActivity(new Intent(this, CustomerActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }

                });

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