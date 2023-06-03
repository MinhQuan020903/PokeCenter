package com.example.pokecenter.customer.lam.Authentication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.option.OptionAdapter;
import com.example.pokecenter.databinding.ActivitySignUpBinding;
import com.example.pokecenter.customer.lam.API.FirebaseSupportAccount;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private int role = 0;

    private String gender = "male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.white));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            customerRole.setBackground(getDrawable(R.drawable.lam_background_raised_secondary_corner_8));
            customerRole.setTypeface(null, Typeface.BOLD);

            venderRole.setTextColor(getColor(R.color.light_secondary));
            venderRole.setBackground(getDrawable(R.drawable.lam_background_outline_secondary_corner_8));
            venderRole.setTypeface(null, Typeface.NORMAL);

            binding.onlyVenderPart.setVisibility(View.GONE);

            role = 0;
        });

        venderRole.setOnClickListener(view -> {
            venderRole.setTextColor(getColor(R.color.white));
            venderRole.setBackground(getDrawable(R.drawable.lam_background_raised_secondary_corner_8));
            venderRole.setTypeface(null, Typeface.BOLD);

            customerRole.setTextColor(getColor(R.color.light_secondary));
            customerRole.setBackground(getDrawable(R.drawable.lam_background_outline_secondary_corner_8));
            customerRole.setTypeface(null, Typeface.NORMAL);

            binding.shopNameEditText.setText(binding.fullNameEditText.getText().toString());
            binding.onlyVenderPart.setVisibility(View.VISIBLE);

            role = 1;
        });



        ImageView genderBoy = binding.genderBoy;
        ImageView genderGirl = binding.genderGirl;

        genderBoy.setOnClickListener(view -> {

            genderBoy.setBackground(getDrawable(R.drawable.lam_background_outline_secondary_corner_8));
            genderGirl.setBackground(getDrawable(R.drawable.lam_background_white_border_1_corner_8));

            gender = "male";

        });

        genderGirl.setOnClickListener(view -> {

            genderBoy.setBackground(getDrawable(R.drawable.lam_background_white_border_1_corner_8));
            genderGirl.setBackground(getDrawable(R.drawable.lam_background_outline_secondary_corner_8));

            gender = "female";

        });

    }

    private void onClickSignUp() {

        String username = binding.fullNameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String shopName = binding.shopNameEditText.getText().toString().trim();
        String phoneNumber = binding.phoneNumberEditText.getText().toString().trim();

        if (!validateData(username, email, password, shopName, phoneNumber)) {
            return;
        }

        changeInProgress(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    changeInProgress(false);
                    if (task.isSuccessful()) {

                        auth.getCurrentUser().sendEmailVerification();
                        auth.signOut();
                        popUpDialogToInform();

                        /* Save User Info */
                        /* Cách 1 */
                        new FirebaseSupportAccount().addNewAccount(email, username, role, gender);

                        /* Cách 2: Using API + new Thread
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            try {
                                new FirebaseSupportAccount().saveUserUsingApi(email, username, role);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                         */

                    } else {
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    void popUpDialogToInform() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lam_dialog_success_sign_up);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        /*
         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); khá quan trọng. THỬ BỎ ĐI SẼ HIỂU =)))
         nếu bỏ dòng này đi thì các thuộc tính của LinearLayout mẹ trong todo_dialog.xml sẽ mất hết
         thay vào đó sẽ là thuộc tính mặc định của dialog, còn nội dung vẫn giữ nguyên
         */
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button close = dialog.findViewById(R.id.close_button);
        close.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    boolean validateData(String username, String email, String password, String shopName, String phoneNumber) {
        boolean isValid = true;

        /* Check empty */
        if (username.isEmpty()) {
            binding.fullNameEditText.setError("You have not entered username");
            isValid = false;
        }

        if (email.isEmpty()) {
            binding.emailEditText.setError("You have not entered email");
            isValid = false;
        }
        if (password.isEmpty()) {
            binding.passwordEditText.setError("You have not entered password");
            isValid = false;
        }

        /* Check valid */
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Email is invalid");
            isValid = false;
        }
        if (password.length() < 6) {
            binding.passwordEditText.setError("Password length should not be less than 6");
            isValid = false;
        }
        return isValid;
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