package com.example.pokecenter.customer.lam;

import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getDrawable;
import static androidx.core.content.ContextCompat.startActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pokecenter.Account;
import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.databinding.FragmentLoginBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    private Account[] mockAccounts = {
            new Account("customer1", "123", 0),
            new Account("vender1", "123", 1),
            new Account("admin1", "123", 2)
    };

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        // if statement checks if the device is running Android Marshmallow (API level 23) or higher,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();

            // change StatusBarColor
            window.setStatusBarColor(getColor(requireContext(), R.color.white));

            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String loggedUsername = sharedPreferences.getString("username", "");
        String loggedPassword = sharedPreferences.getString("password", "");

//        if (!loggedUsername.isEmpty()) {
//            loginProcress(loggedUsername, loggedPassword);
//        }


        // Move to Sign Up Fragment
//        binding.signUpTextView.setOnClickListener(view -> {
//
//        });

//        // Mỗi lần password được nhập thì sẽ xuất hiện Button ở cuối để hide or show password
//        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.length() > 0) {
//                    binding.eyeButton.setVisibility(View.VISIBLE);
//                } else {
//                    binding.eyeButton.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        // eyeButton onClick
//        binding.eyeButton.setOnClickListener(view -> {
//            if (binding.editTextPassword.getInputType() == 129) {
//                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
//                binding.eyeButton.setImageDrawable(getDrawable(getContext(), R.drawable.lam_eye_blind));
//                // Notes: Thay getContext() bằng requireContext() vẫn chạy oke
//            }
//            else
//            {
//                // binding.editTextPassword.getInputType() = InputType.TYPE_CLASS_TEXT = 1
//                binding.editTextPassword.setInputType(129);
//                binding.eyeButton.setImageDrawable(getDrawable(getContext(), R.drawable.lam_eye));
//            }
//        });
//
//        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        // loginButton onClick
//        binding.loginButton.setOnClickListener(view -> {
//
//            // Ẩn Keyboard
//            inputMethodManager.hideSoftInputFromWindow(binding.loginButton.getWindowToken(), 0);
//
//            // Gỡ bỏ Focus trên 2 editText
//            binding.editTextUsername.clearFocus();
//            binding.editTextPassword.clearFocus();
//
//            // Xử lí logic của login
//            String inputUsername = String.valueOf(binding.editTextUsername.getText());
//            String inputPassword = String.valueOf(binding.editTextPassword.getText());
//
//            // loginProcress(inputUsername, inputPassword);
//        });
//
//        // Move to Forgot Password Fragment
//        binding.forgotPasswordTextView.setOnClickListener(view -> {
//
//        });
//
//        /*
//        Ẩn Keyboard + Gỡ bỏ Focus trên 2 editText
//        khi người dùng click ra ngoài
//         */
//        binding.loginFragment.setOnClickListener(view -> {
//            // Ẩn Keyboard
//            inputMethodManager.hideSoftInputFromWindow(binding.loginButton.getWindowToken(), 0);
//
//            // Gỡ bỏ Focus trên 2 editText
//            binding.editTextUsername.clearFocus();
//            binding.editTextPassword.clearFocus();
//        });
//
//        return binding.getRoot();
//    }

//    void loginProcress(String username, String password) {
//        int role = getRole(username, password);
//
//        switch (role) {
//            case 0:
//                NavHostFragment.findNavController(LoginFragment.this)
//                        .navigate(R.id.action_loginFragment_to_customerFragment);
//                break;
//            case 1:
//                NavHostFragment.findNavController(LoginFragment.this)
//                        .navigate(R.id.action_loginFragment_to_venderFragment);
//                break;
//            case 2:
//                NavHostFragment.findNavController(LoginFragment.this)
//                        .navigate(R.id.action_loginFragment_to_adminFragment);
//                break;
//            default:
//                Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        /*
//         Note:
//         Cả 3 R.id.action trong đã được thêm 2 thuộc tính trong nav_app.xml:
//            app:popUpTo="@id/nav_app"
//            app:popUpToInclusive="true"
//            => Công dụng: Clear sạch BackStack, khi user nhấn back button trên điện thoại sẽ thoát app chứ không quay lại LoginFragment
//
//         Có cách 2 tác dụng tương tự, được áp dụng ở CustomerProfileFragment
//         */
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//
//            try {
//                if (role != -1 && binding.rememberMeCheckBox.isChecked()) {
//                    rememberMeFunc(username, password);
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
//        });
        return binding.getRoot();
    }

    int getRole(String username, String password) {
        for (Account acc: mockAccounts) {
            if (acc.username.equals(username) && acc.password.equals(password)) {
                return acc.role;
            }
        }
        return -1;
    }

    void rememberMeFunc(String username, String password) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString("username", username);
        edit.putString("password", password);
        edit.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}