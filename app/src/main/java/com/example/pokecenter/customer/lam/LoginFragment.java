package com.example.pokecenter.customer.lam;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.pokecenter.Account;
import com.example.pokecenter.MainActivity;
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
            new Account("customer1", "123", 0),
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

        // Mỗi lần password được nhập thì sẽ xuất hiện Button ở cuối để clear
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    binding.clearButton.setVisibility(View.VISIBLE);
                } else {
                    binding.clearButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // clearButton onClick
        binding.clearButton.setOnClickListener(view -> {
            binding.editTextPassword.setText("");
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