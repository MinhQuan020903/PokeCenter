package com.example.pokecenter.customer.lam.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.admin.AdminActivity;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.ActivitySignInBinding;
import com.example.pokecenter.vender.VenderActivity;
import com.example.pokecenter.vender.VenderTab.Home.Profile.VenderProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
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
            if (binding.passwordEditText.getInputType() == 129) {
                binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.eyeButton.setImageDrawable(getDrawable(R.drawable.lam_eye_blind));
            }
            else
            {
                // binding.editTextPassword.getInputType() = InputType.TYPE_CLASS_TEXT = 1
                binding.passwordEditText.setInputType(129);
                binding.eyeButton.setImageDrawable(getDrawable(R.drawable.lam_eye));
            }
        });

        binding.loginButton.setOnClickListener(view -> {
            onClickSignIn();
        });

        binding.forgotPasswordTextView.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        setContentView(binding.getRoot());
    }

    private void onClickSignIn() {

        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!validateData(email, password)) {
            return;
        }

        changeInProgress(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();


        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        if (auth.getCurrentUser().isEmailVerified()) {
                            WishListData.fetchDataFromSever();

                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());

                            executor.execute(() -> {

                                Account fetchedAccountInfo = null;
                                boolean isSuccessful = true;

                                try {
                                    fetchedAccountInfo = new FirebaseSupportAccount().fetchingCurrentAccount();
                                } catch (IOException e) {
                                    isSuccessful = false;
                                }

                                boolean finalIsSuccessful = isSuccessful;
                                Account finalFetchedAccountInfo = fetchedAccountInfo;
                                handler.post(() -> {
                                    if (finalIsSuccessful) {
                                        CustomerProfileFragment.currentAccount = finalFetchedAccountInfo;
                                        VenderProfileFragment.currentVender = finalFetchedAccountInfo;
                                        new Handler().postDelayed(() -> {
                                            determineActivityToNavigateBasedOnRole(email);
                                        }, 500);

                                    } else {
                                        Toast.makeText(SignInActivity.this, "Failed to connect server", Toast.LENGTH_LONG).show();
                                    }
                                });
                            });



                        } else {
                            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT)
                                    .show();
                            auth.signOut();
                        }

                    } else {
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();

                    }

                    changeInProgress(false);
                });
    }

    void determineActivityToNavigateBasedOnRole(String email) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            int fetchedRole;

            try {
                fetchedRole = new FirebaseSupportAccount().getRoleWithEmail(email);
            } catch (IOException e) {
                fetchedRole = -1;
            }

            int finalFetchedRole = fetchedRole;
            handler.post(() -> {
                if (finalFetchedRole == -1) {
                    Toast.makeText(this, "Connect sever fail", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                else {
                    gotToNextActivityWith(finalFetchedRole);
                    Toast.makeText(this, "Successful Login.", Toast.LENGTH_SHORT)
                            .show();

                    SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
                    sharedPreferences.edit().putInt("role", finalFetchedRole).apply();
                }
                changeInProgress(false);
            });
        });
    }

    void gotToNextActivityWith(int role) {
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

        /* Check empty */
        if (email.isEmpty()) {
            binding.emailEditText.setError("You have not entered email");
            return false;
        }
        if (password.isEmpty()) {
            binding.passwordEditText.setError("You have not entered password");
            return false;
        }

        /* Check valid */
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
            binding.loginButton.setVisibility(View.INVISIBLE);
        } else {
            binding.loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}