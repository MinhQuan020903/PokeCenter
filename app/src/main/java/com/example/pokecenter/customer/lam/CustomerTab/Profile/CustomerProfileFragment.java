package com.example.pokecenter.customer.lam.CustomerTab.Profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Authentication.SignInActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyProductReviewsActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.StartSellingActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.Suport.CustomerSupportActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.WishListActivity;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.databinding.FragmentCustomerProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class CustomerProfileFragment extends Fragment {

    private FragmentCustomerProfileBinding binding;

    public static Account currentAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerProfileBinding.inflate(inflater, container, false);

        binding.username.setText(currentAccount.getUsername());
        Picasso.get().load(currentAccount.getAvatar()).into(binding.customerAvatar);

        binding.accountInformationItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CustomerAccountInfoActivity.class));
        });

        binding.myAddressesItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), MyAddressesActivity.class));
        });

        binding.changePassword.setOnClickListener(view -> {
            popUpDialogToChangePassword();
        });

        binding.wishListItem.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), WishListActivity.class));
        });

        binding.productReviews.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), MyProductReviewsActivity.class));
        });

        binding.starSelling.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), StartSellingActivity.class));
        });

        binding.support.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CustomerSupportActivity.class));
        });

        binding.logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finishAffinity();

            FollowData.hasData = false;
            WishListData.hasData = false;
        });

        return binding.getRoot();
    }

    private Dialog dialog;
    private void popUpDialogToChangePassword() {

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.lam_dialog_change_password);

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

        Button okButton = dialog.findViewById(R.id.ok_button);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        okButton.setOnClickListener(view -> {

            TextView currentPassword = dialog.findViewById(R.id.current_password);
            TextView newPassword = dialog.findViewById(R.id.new_password);
            TextView confirmNewPassword = dialog.findViewById(R.id.confirm_new_password);


            if (!validate(currentPassword, newPassword, confirmNewPassword)) {
                return;
            }

            updatePassword(currentPassword, newPassword.getText().toString(), okButton, progressBar);
        });

        dialog.show();

    }

    private boolean validate(TextView currentPassword, TextView newPassword, TextView confirmNewPassword) {
        boolean isValid = true;

        if (currentPassword.getText().toString().isEmpty()) {
            currentPassword.setError("You have not entered current password");
            isValid = false;
        }

        if (newPassword.getText().toString().isEmpty()) {
            newPassword.setError("You have not entered new password");
            return false;
        }

        if (newPassword.getText().toString().length() < 6) {
            newPassword.setError("Password length should not be less than 6");
            return false;
        }

        if (confirmNewPassword.getText().toString().isEmpty()) {
            confirmNewPassword.setError("You have not confirmed new password");
            return false;
        }

        if (!confirmNewPassword.getText().toString().equals(newPassword.getText().toString())) {
            confirmNewPassword.setError("The confirmation password does not match");
            isValid = false;
        }

        return isValid;
    }

    private void updatePassword(TextView currentPassword, String newPassword, Button okButton, ProgressBar progressBar) {

        okButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword.getText().toString());

        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Password update successful", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        });

                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                currentPassword.setError(e.getLocalizedMessage());
                            }
                        });

                        okButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}