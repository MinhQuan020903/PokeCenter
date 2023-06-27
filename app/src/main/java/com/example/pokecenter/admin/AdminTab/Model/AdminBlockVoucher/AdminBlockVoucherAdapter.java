package com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseCallback;
import com.example.pokecenter.admin.AdminTab.FirebaseAPI.FirebaseSupportVoucher;
import com.example.pokecenter.admin.AdminTab.Tabs.Home.VoucherManagement.AddBlockVoucherActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminBlockVoucherAdapter extends RecyclerView.Adapter<AdminBlockVoucherAdapter.ViewHolder> {

    private Dialog adminAuthDialog;
    private InputMethodManager inputMethodManager;
    private ArrayList<AdminBlockVoucher> blockVouchers;
    private int resource;
    private Context context;

    public AdminBlockVoucherAdapter(ArrayList<AdminBlockVoucher> blockVouchers, int resource, Context context) {
        this.blockVouchers = blockVouchers;
        this.resource = resource;
        this.context = context;
    }

    public void setBlockVouchers(ArrayList<AdminBlockVoucher> blockVouchers) {
        this.blockVouchers = blockVouchers;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBlockVoucherId;
        private TextView tvBlockVoucherName;
        private TextView tvBlockVoucherStartDate;
        private TextView tvBlockVoucherEndDate;
        private TextView tvBlockVoucherCurrentQuantity;
        private TextView tvBlockVoucherValue;
        private Button bBlockVoucherPublish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBlockVoucherId = itemView.findViewById(R.id.tvBlockVoucherId);
            tvBlockVoucherName = itemView.findViewById(R.id.tvBlockVoucherName);
            tvBlockVoucherStartDate = itemView.findViewById(R.id.tvBlockVoucherStartDate);
            tvBlockVoucherEndDate = itemView.findViewById(R.id.tvBlockVoucherEndDate);
            tvBlockVoucherCurrentQuantity = itemView.findViewById(R.id.tvBlockVoucherCurrentQuantity);
            tvBlockVoucherValue = itemView.findViewById(R.id.tvBlockVoucherValue);
            bBlockVoucherPublish = itemView.findViewById(R.id.bBlockVoucherPublish);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminBlockVoucher blockVoucher = blockVouchers.get(position);
        if (blockVoucher != null) {
            try {
                holder.tvBlockVoucherId.setText(blockVoucher.getId());
                holder.tvBlockVoucherName.setText(blockVoucher.getName());
                holder.tvBlockVoucherStartDate.setText(blockVoucher.getStartDate());
                holder.tvBlockVoucherEndDate.setText(blockVoucher.getEndDate());
                holder.tvBlockVoucherCurrentQuantity.setText(String.valueOf(blockVoucher.getCurrentQuantity()));

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                // Convert the long value to a currency string
                String currencyString = currencyFormat.format(blockVoucher.getValue());
                holder.tvBlockVoucherValue.setText(currencyString);

                holder.bBlockVoucherPublish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        publishBlockVoucherCodeToAllCustomer(blockVoucher);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void publishBlockVoucherCodeToAllCustomer(AdminBlockVoucher blockVoucher){
        adminAuthDialog = new Dialog(context);
        adminAuthDialog.setContentView(R.layout.quan_dialog_admin_auth);
        adminAuthDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Window window = adminAuthDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch event is an ACTION_DOWN event
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        TextView tvAdminAuthFailed = adminAuthDialog.findViewById(R.id.tvAdminAuthFailed);
        tvAdminAuthFailed.setVisibility(View.INVISIBLE);

        adminAuthDialog.show();

        EditText etAdminAuthPassword = adminAuthDialog.findViewById(R.id.etAdminAuthPassword);
        Button bCancel = adminAuthDialog.findViewById(R.id.bAuthCancel);
        Button bAccept = adminAuthDialog.findViewById(R.id.bAuthAccept);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminAuthDialog.dismiss();
            }
        });
        bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etAdminAuthPassword.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseSupportVoucher firebaseSupportVoucher = new FirebaseSupportVoucher(context);
                                firebaseSupportVoucher.sendVoucherForAllCustomer(blockVoucher, new FirebaseCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean done) {
                                        if (done) {
                                            Toast.makeText(context, "Publish block voucher successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Publish block voucher failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                adminAuthDialog.dismiss();

                            } else {
                                tvAdminAuthFailed.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockVouchers.size();
    }
}
