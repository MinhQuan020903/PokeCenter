package com.example.pokecenter.customer.lam.CustomerTab.Cart;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import android.widget.FrameLayout;
import android.widget.ListView;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CustomerShoppingCartFragment extends Fragment implements CartRecyclerViewInterface {

    private FragmentCustomerShoppingCartBinding binding;

    public static List<Cart> myCarts = new ArrayList<>();

    private CartAdapter cartAdapter;

    private Snackbar snackbar;
    AtomicInteger totalPrice = new AtomicInteger(0);
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCustomerShoppingCartBinding.inflate(inflater, container, false);

        /* Set up Cart RecyclerView */
        setUpCartRecyclerView();

        binding.totalPrice.setText(currencyFormatter.format(0));
        binding.checkoutButton.setOnClickListener(view -> {
            if (totalPrice.get() > 0) {

                Intent intent = new Intent(getActivity(), CheckoutActivity.class);

                // Filter the list to only include the Cart objects with isChecked = true
                List<Cart> checkedCarts = myCarts.stream()
                        .filter(Cart::isChecked)
                        .collect(Collectors.toList());

                // Put the filtered list as an extra in the Intent
                intent.putExtra("checkedCarts", new ArrayList<>(checkedCarts));

                startActivity(intent);
            }
        });



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
                    });
                    cartAdapter.notifyDataSetChanged();
                    if (myCarts.size() == 0) {
                        binding.informText.setText("You haven't added anything to your cart.");
                        binding.informText.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.informText.setText("Failed to connect server.");
                    binding.informText.setVisibility(View.VISIBLE);
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
        snackbar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_SHORT);

        final View snackBarView = snackbar.getView();
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 290);

        snackBarView.setLayoutParams(params);
    }

    private void showSnackBar(String text) {
        snackbar.setText(text);
        snackbar.show();
    }

    @Override
    public void onCheckedChange(int position, boolean isChecked) {
        calculatePrice();
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
        OptionAdapter optionAdapter = new OptionAdapter(getActivity(), myCarts.get(position).getProduct().getOptions(), myCarts.get(position).getProduct().getImages().get(0));

        if(optionAdapter.getCount() > 4){
            ViewGroup.LayoutParams params = lvOption.getLayoutParams();
            params.height = 11 * 100;
            lvOption.setLayoutParams(params);
        }
        lvOption.setAdapter(optionAdapter);


        lvOption.setOnItemClickListener((adapterView, view, selectedItemPosition, l) -> {

            /*
            Note: muốn sử dụng setOnItemClickListener thì item trong listView đó không được set thuộc tính android:clickable="true"
             */

            // Reset background color for all items
            for(int i = 0; i < adapterView.getChildCount(); i++) {
                adapterView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }

            // Set background color for the selected item
            view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.lam_background_outline_secondary));
            myCarts.get(position).setSelectedOption(selectedItemPosition);



        });

        Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(view -> {

            dialog.dismiss();
            cartAdapter.notifyItemChanged(position);

            if (myCarts.get(position).isChecked()) {
                calculatePrice();
            }

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

                    calculatePrice();
                    if (myCarts.size() == 0) {
                        binding.informText.setText("You haven't added anything to your cart.");
                        binding.informText.setVisibility(View.VISIBLE);
                    }
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

    @Override
    public void onIncButtonClick(int position) {
        int totalPrice = 0;
        try {
            totalPrice = currencyFormatter.parse(binding.totalPrice.getText().toString()).intValue();
        } catch (ParseException e) {

        }

        int selectedOptionPosition = myCarts.get(position).getSelectedOption();
        Option selectedOption = myCarts.get(position).getProduct().getOptions().get(selectedOptionPosition);

        totalPrice += selectedOption.getPrice();
        binding.totalPrice.setText(currencyFormatter.format(totalPrice));
    }

    @Override
    public void onDecButtonClick(int position) {
        int totalPrice = 0;
        try {
            totalPrice = currencyFormatter.parse(binding.totalPrice.getText().toString()).intValue();
        } catch (ParseException e) {

        }

        int selectedOptionPosition = myCarts.get(position).getSelectedOption();
        Option selectedOption = myCarts.get(position).getProduct().getOptions().get(selectedOptionPosition);

        totalPrice -= selectedOption.getPrice();
        binding.totalPrice.setText(currencyFormatter.format(totalPrice));
    }

    private void calculatePrice() {
        totalPrice.set(0);
        myCarts.forEach(cart -> {
            if (cart.isChecked()) {
                totalPrice.addAndGet(cart.getProduct().getOptions().get(cart.getSelectedOption()).getPrice() * cart.getQuantity());
            }
        });
        binding.totalPrice.setText(currencyFormatter.format(totalPrice.get()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                new FirebaseSupportCustomer().updateAllCarts(myCarts);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> {
                myCarts = new ArrayList<>();
            });
        });

        binding = null;
    }

}