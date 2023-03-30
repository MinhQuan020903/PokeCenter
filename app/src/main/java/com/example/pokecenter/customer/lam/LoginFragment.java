package com.example.pokecenter.customer.lam;

import static androidx.core.content.ContextCompat.getDrawable;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pokecenter.Account;
import com.example.pokecenter.R;
import com.example.pokecenter.databinding.FragmentLoginBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    private Account[] mockAccounts = {
            new Account("", "", 0),
            new Account("vender1", "123", 1),
            new Account("admin1", "123", 2)
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        // Move to Sign Up Fragment
        binding.signUpTextView.setOnClickListener(view -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
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

        // eyeButton onClick
        binding.eyeButton.setOnClickListener(view -> {
            if (binding.editTextPassword.getInputType() == 129) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.eyeButton.setImageDrawable(getDrawable(getContext(), R.drawable.lam_blind));
                // Notes: Thay getContext() bằng requireContext() vẫn chạy oke
            }
            else
            {
                // binding.editTextPassword.getInputType() = InputType.TYPE_CLASS_TEXT = 1
                binding.editTextPassword.setInputType(129);
                binding.eyeButton.setImageDrawable(getDrawable(getContext(), R.drawable.lam_eye));
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        // loginButton onClick
        binding.loginButton.setOnClickListener(view -> {

            // Ẩn Keyboard
            inputMethodManager.hideSoftInputFromWindow(binding.loginButton.getWindowToken(), 0);

            // Gỡ bỏ Focus trên 2 editText
            binding.editTextUsername.clearFocus();
            binding.editTextPassword.clearFocus();

            // Xử lí logic của login
            String inputUsername = String.valueOf(binding.editTextUsername.getText());
            String inputPassword = String.valueOf(binding.editTextPassword.getText());

            switch (CheckAccont(inputUsername, inputPassword)) {
                case 0:
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_loginFragment_to_customerFragment);
                    break;
                case 1:
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_loginFragment_to_venderFragment);
                    break;
                case 2:
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_loginFragment_to_adminFragment);
                    break;
                default:
                    Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Move to Forgot Password Fragment
        binding.forgotPasswordTextView.setOnClickListener(view -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });

        /*
        Ẩn Keyboard + Gỡ bỏ Focus trên 2 editText
        khi người dùng click ra ngoài
         */
        binding.loginFragment.setOnClickListener(view -> {
            // Ẩn Keyboard
            inputMethodManager.hideSoftInputFromWindow(binding.loginButton.getWindowToken(), 0);

            // Gỡ bỏ Focus trên 2 editText
            binding.editTextUsername.clearFocus();
            binding.editTextPassword.clearFocus();
        });

        return binding.getRoot();
    }

    public int CheckAccont(String username, String password) {
        for (Account acc: mockAccounts) {
            if (acc.username.equals(username) && acc.password.equals(password)) {
                return acc.role;
            }
        }
        return -1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}