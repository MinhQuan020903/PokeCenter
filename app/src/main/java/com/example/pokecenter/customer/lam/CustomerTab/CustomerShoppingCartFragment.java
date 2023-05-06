package com.example.pokecenter.customer.lam.CustomerTab;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.ProductDetailActivity;
import com.example.pokecenter.customer.lam.Interface.CartRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.cart.CartAdapter;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.option.OptionAdapter;
import com.example.pokecenter.databinding.FragmentCustomerShoppingCartBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerShoppingCartFragment extends Fragment implements CartRecyclerViewInterface {

    private FragmentCustomerShoppingCartBinding binding;

    private List<Cart> myCarts = new ArrayList<>();

    private CartAdapter cartAdapter;

    private View customize;

    private Snackbar snackbar;

    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCustomerShoppingCartBinding.inflate(inflater, container, false);

        /* Set up Cart RecyclerView */
        setUpCartRecyclerView();

        binding.totalPrice.setText(currencyFormatter.format(0));

        return binding.getRoot();
    }

    private void setUpCartRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.rcvCarts.setLayoutManager(linearLayoutManager);

        cartAdapter = new CartAdapter(getActivity(), myCarts, this);
        binding.rcvCarts.setAdapter(cartAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Cart> fetchedCartsData;
            try {
                fetchedCartsData = new FirebaseSupportCustomer().fetchingAllCarts();
            } catch (IOException e) {
                fetchedCartsData = null;
            }
            List<Cart> finalFetchedCartsData = fetchedCartsData;
            handler.post(() -> {
                if (finalFetchedCartsData != null) {
                    finalFetchedCartsData.forEach(cart -> {
                        myCarts.add(cart);
                        cartAdapter.setData(myCarts);
                    });
                } else {
                    binding.informFail.setVisibility(View.VISIBLE);
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpSnackbar();
    }

    private void setUpSnackbar() {
        customize = getLayoutInflater().inflate(R.layout.lam_custom_only_text_snack_bar, null);
        snackbar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0 , 140);

        snackbarLayout.addView(customize, 0);
    }

    private void showSnackBar(String text) {
        TextView textView = customize.findViewById(R.id.text_inform);
        textView.setText(text);
        snackbar.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCheckedChange(int position, boolean isChecked) {

        int totalPrice = 0;
        try {
            totalPrice = currencyFormatter.parse(binding.totalPrice.getText().toString()).intValue();
        } catch (ParseException e) {

        }

        int selectedOptionPosition = myCarts.get(position).getSelectedOption();
        Option selectedOption = myCarts.get(position).getProduct().getOptions().get(selectedOptionPosition);

        if (isChecked) {

            totalPrice += selectedOption.getPrice() * myCarts.get(position).getQuantity();

        } else {

            totalPrice -= selectedOption.getPrice() * myCarts.get(position).getQuantity();
        }

        binding.totalPrice.setText(currencyFormatter.format(totalPrice));
    }

    @Override
    public void onSelectOptionsButtonClick(int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.lam_dialog_change_option_of_cart);

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

        ListView lvOption = dialog.findViewById(R.id.lv_options);

        OptionAdapter optionAdapter = new OptionAdapter(getActivity(), myCarts.get(position).getProduct().getOptions());
        lvOption.setAdapter(optionAdapter);

        Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDeleteButtonClick(int position) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBarBg.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                new FirebaseSupportCustomer().deleteCart(myCarts.get(position).getProduct().getId());
            } catch (IOException e) {
                isSuccessful = false;
            }
            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {

                    showSnackBar("Cart has been cleared");
                    myCarts.remove(position);
                    cartAdapter.notifyDataSetChanged();

                } else {
                    showSnackBar("Delete cart failed, try again later!");
                }
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.progressBarBg.setVisibility(View.INVISIBLE);
            });
        });
    }

    @Override
    public void onCartItemClick(int position) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra("product object", myCarts.get(position).getProduct());
        startActivity(intent);
    }
}