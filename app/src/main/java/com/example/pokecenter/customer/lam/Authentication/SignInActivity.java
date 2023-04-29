package com.example.pokecenter.customer.lam.Authentication;

import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getDrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivitySignInBinding;
import com.example.pokecenter.databinding.ActivitySignUpBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.white));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /* SigUp Button */
        binding.signUpTextView.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.signInScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        // Mỗi lần password được nhập thì sẽ xuất hiện Button ở cuối để hide or show password
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    binding.eyeButton.setVisibility(View.VISIBLE);
                } else {
                    binding.eyeButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /* eyeButton */
        binding.eyeButton.setOnClickListener(view -> {
            if (binding.editTextPassword.getInputType() == 129) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.eyeButton.setImageDrawable(getDrawable(R.drawable.lam_eye_blind));
            }
            else
            {
                // binding.editTextPassword.getInputType() = InputType.TYPE_CLASS_TEXT = 1
                binding.editTextPassword.setInputType(129);
                binding.eyeButton.setImageDrawable(getDrawable(R.drawable.lam_eye));
            }
        });

        binding.loginButton.setOnClickListener(view -> {

        });

        binding.forgotPasswordTextView.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}