package com.example.pokecenter.customer.lam.CustomerTab.Cart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.MyAddressesActivity;
import com.example.pokecenter.customer.lam.Interface.AddressRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.address.Address;
import com.example.pokecenter.customer.lam.Model.address.AddressArrayAdapter;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.databinding.ActivityCheckoutBinding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckoutActivity extends AppCompatActivity implements AddressRecyclerViewInterface {

    private ActivityCheckoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Set statusBar Color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.white)));

        /* Set color to title */
        getSupportActionBar().setTitle("Checkout");
        // getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#027B96\">Profile</font>", Html.FROM_HTML_MODE_LEGACY));

        /* Set up back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Cart orderNowCart = (Cart) getIntent().getSerializableExtra("orderNowCart");
        List<Cart> checkedCarts = (List<Cart>) getIntent().getSerializableExtra("checkedCarts");


        /* Delivery Address logic */
        deliveryAddressLogic();

    }

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

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

        binding.changeDeliveryAddress.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
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

            ListView lvAddress = dialog.findViewById(R.id.lv_options);
            AddressArrayAdapter addressArrayAdapter = new AddressArrayAdapter(this, MyAddressesActivity.myAddresses);

            if(addressArrayAdapter.getCount() > 2){
                ViewGroup.LayoutParams params = lvAddress.getLayoutParams();
                params.height = 10 * 100;
                lvAddress.setLayoutParams(params);
            }
            lvAddress.setAdapter(addressArrayAdapter);

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
            });

            Button okButton = dialog.findViewById(R.id.okButton);
            okButton.setOnClickListener(view -> {

                Address selectedAddress = MyAddressesActivity.myAddresses.get(selectedAddressPosition.get());

                binding.receiverName.setText(selectedAddress.getReceiverName());
                binding.numberStreetAddress.setText(selectedAddress.getNumberStreetAddress());
                binding.address2.setText(selectedAddress.getAddress2());

                dialog.dismiss();
            });

            dialog.show();
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

        /* Recreate this Activity when backbutton on MyAddressesActivity */

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
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