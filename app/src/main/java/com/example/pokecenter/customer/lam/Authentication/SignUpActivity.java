package com.example.pokecenter.customer.lam.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.CustomerActivity;
import com.example.pokecenter.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.signUpButton.setOnClickListener(view -> {
            onClickSignUp();
        });

        setContentView(binding.getRoot());
    }

    private void onClickSignUp() {

        changeInProgress(true);

        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();

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
                        Toast.makeText(this, "Sign Up failed.", Toast.LENGTH_SHORT)
                                .show();
                    }

                });

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