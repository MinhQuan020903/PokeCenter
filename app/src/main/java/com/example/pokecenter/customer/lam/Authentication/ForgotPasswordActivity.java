package com.example.pokecenter.customer.lam.Authentication;

import static androidx.core.content.ContextCompat.getColor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.white));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        binding.forgotPasswordScreen.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        int indicator = getColor(R.color.light_border_shape);
        binding.emailEditText.setBackgroundTintList(ColorStateList.valueOf(indicator));

        binding.sendMailButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString();
            if (!validateData(email)) {
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        });

        setContentView(binding.getRoot());
    }

    boolean validateData(String email) {

        /* Check empty */
        if (email.isEmpty()) {
            binding.emailEditText.setError("You have not entered email");
            return false;
        }

        /* Check valid */
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Email is invalid");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}