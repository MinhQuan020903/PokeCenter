package com.example.pokecenter.customer.lam.CustomerTab.Cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.Interface.AddressRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.address.AddressArrayAdapter;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.checkout_item.CheckoutItem;
import com.example.pokecenter.customer.lam.Model.checkout_item.CheckoutProductAdapter;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.review_product.ReviewProductAdapter;
import com.example.pokecenter.customer.lam.Model.voucher.VoucherInfo;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivityCheckoutBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CheckoutActivity extends AppCompatActivity implements AddressRecyclerViewInterface {

    private ActivityCheckoutBinding binding;
    InputMethodManager inputMethodManager;
    private List<Cart> checkedCarts;
    private List<CheckoutItem> checkoutItemList;
    private RecyclerView rcv_checkout;
    private CheckoutProductAdapter checkoutProductAdapter;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    int subTotal;
    int voucherValue;
    int deliveryCharge;

    VoucherInfo voucherInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.checkoutSrcreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        /* Set statusBar Color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Checkout");


        /* Set up back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Delivery Address logic */
        deliveryAddressLogic();


        Cart orderNowCart = (Cart) getIntent().getSerializableExtra("orderNowCart");
        checkedCarts = (List<Cart>) getIntent().getSerializableExtra("checkedCarts");

        if (checkedCarts == null) {
            checkedCarts = new ArrayList<>();
            checkedCarts.add(orderNowCart);
        }

        setUpCheckoutRecyclerView(checkedCarts);


        subTotal = subTotal();
        voucherValue = 0;
        deliveryCharge = 40000 * countShop();
        binding.subTotal.setText(currencyFormatter.format(subTotal));
        binding.voucherValue.setText(currencyFormatter.format(voucherValue));
        binding.deliveryCharge.setText(currencyFormatter.format(deliveryCharge));
        binding.total.setText(currencyFormatter.format(subTotal - voucherValue + deliveryCharge));

        /* Set up apply voucher logic */
        setUpPromoCode();

        setUpCheckoutButton();


    }

    private void setUpCheckoutButton() {
        binding.checkoutButton.setOnClickListener(view -> {
            binding.checkoutButton.setVisibility(View.GONE);
            binding.progressBarCheckout.setVisibility(View.VISIBLE);

            ExecutorService executor = Executors.newCachedThreadPool();
            Handler handler = new Handler(Looper.getMainLooper());

            if (voucherValue > 0){
                executor.execute(() -> {
                    try {
                        new FirebaseSupportCustomer().disableVoucher(voucherInfo.getKey());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }


            executor.execute(() -> {

                boolean isSuccessful = new FirebaseSupportCustomer().saveOrders(checkoutItemList);;

                handler.post(() -> {
                    if (isSuccessful) {


                        checkedCarts.forEach(removeCart -> {
                            CustomerShoppingCartFragment.myCarts.removeIf(cart -> cart.getProduct().getId().equals(removeCart.getProduct().getId()));
                        });
                        finishAffinity();   // finish để CustomerShoppingCartFragment thực hiện onDestroy
                        startActivity(new Intent(this, OrderConfirmedActivity.class));

                        // Move to Order Confirmed Activity
                    } else {
                        // popUp Dialog
                    }

                    binding.checkoutButton.setVisibility(View.VISIBLE);
                    binding.progressBarCheckout.setVisibility(View.GONE);
                });
            });

        });
    }

    private void setUpPromoCode() {

        binding.applyPromoCodeButton.setOnClickListener(view -> {
            binding.promoCode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            binding.voucherValue.setText(currencyFormatter.format(0));

            voucherValue = 0;
            binding.voucherValue.setText(currencyFormatter.format(voucherValue));
            binding.total.setText(currencyFormatter.format(subTotal + deliveryCharge));

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            getCurrentFocus().clearFocus();

            binding.applyPromoCodeButton.setVisibility(View.GONE);
            binding.progressBarApplyVoucher.setVisibility(View.VISIBLE);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {

                boolean isSuccessful = true;
                VoucherInfo fetchedVoucherInfo = null;
                try {
                    voucherInfo = new FirebaseSupportCustomer().fetchingVoucherInfo(binding.promoCode.getText().toString());
                } catch (IOException e) {
                    isSuccessful = false;
                }

                boolean finalIsSuccessful = isSuccessful;
                handler.post(() -> {
                    if (finalIsSuccessful) {

                        if (voucherInfo != null) {

                            if (voucherInfo.isStatus()) {

                                Date currentDate = new Date();

                                if (voucherInfo.getStartDate().getTime() <= currentDate.getTime()
                                        && currentDate.getTime() < voucherInfo.getEndDate().getTime()) {

                                    Drawable icon = getDrawable(R.drawable.lam_baseline_check_24);
                                    icon.setTint(getColor(R.color.light_secondary));
                                    binding.promoCode.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);

                                    voucherValue = voucherInfo.getValue();
                                    binding.voucherValue.setText(currencyFormatter.format(voucherValue));
                                    binding.total.setText(currencyFormatter.format(subTotal - voucherValue + deliveryCharge));


                                } else {
                                    Toast.makeText(this, "Promo code does not exist", Toast.LENGTH_SHORT)
                                            .show();
                                }

                            } else {
                                Toast.makeText(this, "Promo code already used", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        } else {
                            Toast.makeText(this, "Promo code does not exist", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } else {
                        Toast.makeText(this, "Failed to connect server", Toast.LENGTH_SHORT)
                                .show();
                    }

                    binding.applyPromoCodeButton.setVisibility(View.VISIBLE);
                    binding.progressBarApplyVoucher.setVisibility(View.GONE);
                });
            });

        });

    }

    private int countShop() {

        Map<String, Boolean> tick = new HashMap<>();
        int count = 0;

        for (int i = 0; i < checkoutItemList.size(); ++i) {
            if (!tick.containsKey(checkoutItemList.get(i).getVenderId())) {
                count++;
                tick.put(checkoutItemList.get(i).getVenderId(), true);
            }
        }
        return count;
    }

    private int subTotal() {

        int total = 0;
        for (int i = 0; i < checkoutItemList.size(); ++i) {
            total += checkoutItemList.get(i).getPrice() * checkoutItemList.get(i).getQuantity();
        }

        return total;
    }

    private void setUpCheckoutRecyclerView(List<Cart> checkedCarts) {

        rcv_checkout = binding.rcvCheckout;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_checkout.setLayoutManager(linearLayoutManager);

        checkoutItemList = checkedCarts.stream().map(cart -> {
                    CheckoutItem item = new CheckoutItem();
                    item.setProductId(cart.getProduct().getId());
                    item.setName(cart.getProduct().getName());

                    item.setOptionSize(cart.getProduct().getOptions().size());

                    if (cart.getProduct().getOptions().size() == 1) {
                        item.setImage(cart.getProduct().getImages().get(0));

                    } else {

                        Option selectedOption = cart.getProduct().getOptions().get(cart.getSelectedOption());
                        if (selectedOption.getOptionImage().isEmpty()) {
                            item.setImage(cart.getProduct().getImages().get(0));
                        } else {
                            item.setImage(selectedOption.getOptionImage());
                        }
                    }

                    item.setSelectedOption(cart.getSelectedOption());
                    item.setPrice(cart.getProduct().getOptions().get(cart.getSelectedOption()).getPrice());
                    item.setQuantity(cart.getQuantity());

                    item.setVenderId(cart.getProduct().getVenderId());

                    return item;
                }
        ).collect(Collectors.toList());

        checkoutProductAdapter = new CheckoutProductAdapter(this, checkoutItemList);
        rcv_checkout.setAdapter(checkoutProductAdapter);

        ViewGroup.LayoutParams params = rcv_checkout.getLayoutParams();
        params.height = (checkoutProductAdapter.getItemCount() - 1) * 350 + 366;

        rcv_checkout.setLayoutParams(params);

    }

    private Dialog changeAddressDialog;
    private void deliveryAddressLogic() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Address> fetchedAddressesData = null;

            boolean isSuccessful = true;
            try {
                fetchedAddressesData = new FirebaseSupportCustomer().fetchingAddressesData();
            } catch (IOException e) {
                isSuccessful = false;
            }


            boolean finalIsSuccessful = isSuccessful;
            List<Address> finalFetchedAddressesData = fetchedAddressesData;
            handler.post(() -> {
                if (finalIsSuccessful) {

                    if (finalFetchedAddressesData.size() == 0) {

                        popUpDialogToInform();

                    } else {

                        MyAddressesActivity.myAddresses = finalFetchedAddressesData;

                        Address deliveryAddress = MyAddressesActivity.getDeliveryAddress();

                        binding.receiverName.setText(deliveryAddress.getReceiverName());
                        binding.numberStreetAddress.setText(deliveryAddress.getNumberStreetAddress());
                        binding.address2.setText(deliveryAddress.getAddress2());

                    }

                } else {

                    Toast.makeText(this, "Failed to connect server!", Toast.LENGTH_SHORT)
                            .show();

                }

                binding.progressBarLoading.setVisibility(View.INVISIBLE);
            });
        });

        binding.changeDeliveryAddress.setOnClickListener(v -> {
            changeAddressDialog = new Dialog(this);
            changeAddressDialog.setContentView(R.layout.lam_dialog_change_address);

            Window window = changeAddressDialog.getWindow();

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

            ListView lvAddress = changeAddressDialog.findViewById(R.id.lv_options);
            AddressArrayAdapter addressArrayAdapter = new AddressArrayAdapter(this, MyAddressesActivity.myAddresses);

            if(addressArrayAdapter.getCount() > 2){
                ViewGroup.LayoutParams params = lvAddress.getLayoutParams();
                params.height = 10 * 100;
                lvAddress.setLayoutParams(params);
            }
            lvAddress.setAdapter(addressArrayAdapter);

            Button okButton = changeAddressDialog.findViewById(R.id.okButton);
            okButton.setEnabled(false);

            AtomicInteger selectedAddressPosition = new AtomicInteger();
            lvAddress.setOnItemClickListener((adapterView, view, selectedItemPosition, l) -> {
                /* Note: muốn sử dụng setOnItemClickListener thì item trong listView đó không được set thuộc tính android:clickable="true" */

                // Reset background color for all items
                for(int i = 0; i < adapterView.getChildCount(); i++) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.parseColor("#FBF9F9"));
                }

                // Set background color for the selected item
                view.setBackground(getDrawable(R.drawable.lam_background_outline_secondary));

                selectedAddressPosition.set(selectedItemPosition);
                okButton.setEnabled(true);
            });

            okButton.setOnClickListener(view -> {

                Address selectedAddress = MyAddressesActivity.myAddresses.get(selectedAddressPosition.get());

                binding.receiverName.setText(selectedAddress.getReceiverName());
                binding.numberStreetAddress.setText(selectedAddress.getNumberStreetAddress());
                binding.address2.setText(selectedAddress.getAddress2());

                changeAddressDialog.dismiss();
            });

            ImageButton addNewAddress = changeAddressDialog.findViewById(R.id.addNewAddress);
            addNewAddress.setOnClickListener(view -> {

                Intent intent = new Intent(this, MyAddressesActivity.class);
                intent.putExtra("add new address", true);
                startActivity(intent);

            });

            changeAddressDialog.show();
        });

    }

    private void popUpDialogToInform() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lam_dialog_inform_to_create_delivery_address);

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

        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MyAddressesActivity.class);
            intent.putExtra("add delivery address", true);
            startActivity(intent);
        });

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (changeAddressDialog != null) {
            changeAddressDialog.dismiss();
        }

        Address deliveryAddress = MyAddressesActivity.myAddresses.get(MyAddressesActivity.myAddresses.size() - 1);

        binding.receiverName.setText(deliveryAddress.getReceiverName());
        binding.numberStreetAddress.setText(deliveryAddress.getNumberStreetAddress());
        binding.address2.setText(deliveryAddress.getAddress2());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onAddressDeleteButtonClick(int position) {

    }

    @Override
    public void onAddressItemClick(int position) {

    }
}