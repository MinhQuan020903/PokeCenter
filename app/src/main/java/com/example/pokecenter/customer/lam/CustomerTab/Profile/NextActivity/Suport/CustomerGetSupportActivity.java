package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.Suport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.CustomerProfileFragment;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Singleton.UserInfo;
import com.example.pokecenter.databinding.ActivityCustomerGetSupportBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerGetSupportActivity extends AppCompatActivity {

    private ActivityCustomerGetSupportBinding binding;
    private ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerGetSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Submit Form");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.getSupportScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        final List<String> listItems = new ArrayList<>();
        listItems.add("Email");
        listItems.add("Phone");

        adapterItems = new ArrayAdapter<>(this, R.layout.lam_text_option_list_item, listItems);
        binding.contactMethodAutoCompleteTextView.setAdapter(adapterItems);
        binding.contactMethodAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    // Email selected;

                    binding.emailContactTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    binding.emailMethodPart.setVisibility(View.GONE);
                    binding.phoneMethodPart.setVisibility(View.VISIBLE);

                } else {
                    // Phone selected;
                    Account currentAccount = UserInfo.getInstance().getAccount();
                    binding.phoneContactTextView.setText(currentAccount.getPhoneNumber());

                    binding.emailMethodPart.setVisibility(View.GONE);
                    binding.phoneMethodPart.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.submitButton.setOnClickListener(view -> {
            if (validateInfo()) {
                saveSupportTicket();
            }
        });

    }

    private void saveSupportTicket() {

        binding.submitButton.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        String problemName = binding.problemNameEditText.getText().toString();
        String desc = binding.descEditText.getText().toString();
        String contactMethod = binding.contactMethodAutoCompleteTextView.getText().toString();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            
            boolean isSuccess = true;
            String generatedId = "";

            try {
                generatedId = new FirebaseSupportCustomer().addNewSupportTicket(problemName, desc, contactMethod);
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            String finalGeneratedId = generatedId;

            handler.post(() -> {
                if (finalIsSuccess) {
                    
                    showDialogToInformSuccess(finalGeneratedId);
                    
                } else {
                    Toast.makeText(this, "Failed to connect server", Toast.LENGTH_SHORT)
                            .show();
                }

                binding.submitButton.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                
            });
        });

    }

    private void showDialogToInformSuccess(String generatedId) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lam_dialog_succesful_send_support_ticket);

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

        TextView supportId = dialog.findViewById(R.id.support_id);
        supportId.setText(generatedId);

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();

    }


    private boolean validateInfo() {
        boolean isValid = true;

        if (binding.problemNameEditText.getText().length() == 0) {
            binding.problemNameEditText.setError("You have not entered problem name.");
            isValid = false;
        }

        if (binding.descEditText.getText().length() == 0) {
            binding.descEditText.setError("You have not entered description about problem.");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}