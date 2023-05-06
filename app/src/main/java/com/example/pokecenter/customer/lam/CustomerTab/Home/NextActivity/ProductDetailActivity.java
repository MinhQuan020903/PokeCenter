package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.SliderAdapter;
import com.example.pokecenter.databinding.ActivityProductDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private boolean isFavourite = false;
    View viewDialog;
    BottomSheetDialog dialog;
    ArrayAdapter<String> adapterItems;
    Snackbar snackbar;

    int selectedOptionPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.light_canvas));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // binding
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pre setup viewDialog
        viewDialog = getLayoutInflater().inflate(R.layout.lam_bottom_sheet_place_order, null);

        productCurrentQuantity = viewDialog.findViewById(R.id.product_current_quantity);

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);

        //
        Product receiveProduct = (Product) getIntent().getSerializableExtra("product object");
        adapterItems = new ArrayAdapter<>(this, R.layout.lam_text_option_list_item, receiveProduct.getAllOptionsName());
        setUpLogicForBottomSheet(receiveProduct);

        binding.backButton.setOnClickListener(view ->  {
            finish();
        });

        List<String> displayImageUrl = receiveProduct.copyListImage();
        receiveProduct.getOptions().forEach(option -> {
            if (!option.getOptionImage().isEmpty()) {
                displayImageUrl.add(option.getOptionImage());
            }
        });

        SliderAdapter sliderAdapter = new SliderAdapter(displayImageUrl);

        binding.productImageSliderView.setSliderAdapter(sliderAdapter);
        binding.productImageSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        binding.productImageSliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        binding.productImageSliderView.startAutoCycle();


        binding.productName.setText(receiveProduct.getName());

        if (receiveProduct.getOptions().size() == 1) {
            binding.productPrice.setText(currencyFormatter.format(receiveProduct.getOptions().get(0).getPrice()));
            binding.dropListDownOptions.setVisibility(View.GONE);
        } else {
            binding.productPrice.setText(currencyFormatter.format(receiveProduct.getOptions().get(0).getPrice()) + " - " + currencyFormatter.format(receiveProduct.getOptions().get(receiveProduct.getOptions().size() - 1).getPrice()));
        }


        binding.optionsAutoCompleteTextView.setAdapter(adapterItems);

        binding.optionsAutoCompleteTextView.setOnClickListener(view -> {
            binding.warning.setVisibility(View.INVISIBLE);
        });

        binding.optionsAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                selectedOptionPosition = position;

                binding.optionsAutoCompleteTextView.clearFocus();
                binding.productPrice.setText(currencyFormatter.format(receiveProduct.getOptions().get(position).getPrice()));
                binding.productImageSliderView.stopAutoCycle();
                if (receiveProduct.getOptions().get(position).getOptionImage().isEmpty()) {
                    binding.productImageSliderView.setCurrentPagePosition(0);
                } else {
                    binding.productImageSliderView.setCurrentPagePosition(receiveProduct.getImages().size() + position);
                }
            }
        });

        binding.favoriteButton.setOnClickListener(view -> {

            isFavourite = !isFavourite;

            if (isFavourite) {
                // Create a color filter with the desired tint color
                ColorFilter colorFilter = new PorterDuffColorFilter(getColor(R.color.dark_secondary), PorterDuff.Mode.SRC_IN);
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.lam_baseline_favorite_28);
                drawable.setColorFilter(colorFilter);
                binding.favoriteButton.setImageDrawable(drawable);

            } else {
                binding.favoriteButton.setImageDrawable(getDrawable(R.drawable.lam_baseline_favorite_border_28));
            }
        });

        binding.shoppingCartButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("targetedFragment", R.id.customerShoppingCardFragment);
            startActivity(intent);
        });

        binding.addToCartButton.setOnClickListener(view -> {
            if (selectedOptionPosition == -1) {
                binding.warning.setVisibility(View.VISIBLE);
            } else {
                openAddToCartBottomSheet(receiveProduct, selectedOptionPosition);
            }


        });

        binding.orderNowButton.setOnClickListener(view -> {
            if (selectedOptionPosition == -1) {
                binding.warning.setVisibility(View.VISIBLE);
            } else {
                openOrderNowBottomSheet(receiveProduct, selectedOptionPosition);
            }
        });

        setContentView(binding.getRoot());

        /*
        setup snack bar
        setUpSnackbar() phải để sau setContentView
         */
        setUpSnackbar();
    }


    private void openAddToCartBottomSheet(Product product, int selectedOptionPosition) {
        viewDialog.findViewById(R.id.add_to_cart_button).setVisibility(View.VISIBLE);
        viewDialog.findViewById(R.id.order_now_button).setVisibility(View.GONE);

        setUpData(product, selectedOptionPosition);

        dialog.show();
    }

    private void openOrderNowBottomSheet(Product product, int selectedOptionPosition) {
        viewDialog.findViewById(R.id.add_to_cart_button).setVisibility(View.GONE);
        viewDialog.findViewById(R.id.order_now_button).setVisibility(View.VISIBLE);

        setUpData(product, selectedOptionPosition);

        dialog.show();
    }

    TextView productCurrentQuantity;

    private void setUpData(Product product, int selectedOptionPosition) {
        ImageView productImage = viewDialog.findViewById(R.id.product_image);

        Option selectedOption = product.getOptions().get(selectedOptionPosition);

        if (product.getOptions().size() == 1) {
            Picasso.get().load(product.getImages().get(0)).into(productImage);
        } else {
            if (selectedOption.getOptionImage().isEmpty()) {
                Picasso.get().load(product.getImages().get(0)).into(productImage);
            } else {
                Picasso.get().load(selectedOption.getOptionImage()).into(productImage);
            }
        }

        TextView priceTextView = viewDialog.findViewById(R.id.product_price);


        priceTextView.setText(currencyFormatter.format(selectedOption.getPrice()));
        productCurrentQuantity.setText("Stock: " + selectedOption.getCurrentQuantity());
    }

    private void setUpLogicForBottomSheet(Product product) {

        TextView productCount = viewDialog.findViewById(R.id.product_count);
        ImageButton incButton = viewDialog.findViewById(R.id.inc_button);
        ImageButton decButton = viewDialog.findViewById(R.id.dec_button);

        incButton.setOnClickListener(view -> {
            int count = Integer.parseInt(productCount.getText().toString());

            if (!productCurrentQuantity.getText().toString().isEmpty()) {
                String numberStr = productCurrentQuantity.getText().toString().replaceAll("\\D+", "");
                if (count < Integer.parseInt(numberStr)) {
                    productCount.setText(String.valueOf(count + 1));
                }
            } else {
                productCount.setText(String.valueOf(count + 1));
            }

        });

        decButton.setOnClickListener(view -> {
            int count = Integer.parseInt(productCount.getText().toString());
            if (count >= 2) {
                productCount.setText(String.valueOf(count - 1));
            }
        });

        /* Logic Add to Cart Button in Bottom Sheet */
        Button addToCart = viewDialog.findViewById(R.id.add_to_cart_button);
        addToCart.setOnClickListener(view -> {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {

                boolean isSuccessful = true;
                try {
                    new FirebaseSupportCustomer().addNewCartUsingApi(product.getId(), Integer.parseInt(productCount.getText().toString()), selectedOptionPosition);
                } catch (IOException e) {
                    isSuccessful = false;
                }

                boolean finalIsSuccessful = isSuccessful;
                handler.post(() -> {
                    if (finalIsSuccessful) {
                        dialog.dismiss();
                        showSnackBar("Added to cart");
                    } else {

                    }
                });
            });

        });

        /* Logic Order Button in Bottom Sheet */
        Button orderNow = viewDialog.findViewById(R.id.order_now_button);
        orderNow.setOnClickListener(view -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {

                boolean isSuccessful = true;
                try {
                    new FirebaseSupportCustomer().addNewCartUsingApi(product.getId(), Integer.parseInt(productCount.getText().toString()), selectedOptionPosition);
                } catch (IOException e) {
                    isSuccessful = false;
                }

                boolean finalIsSuccessful = isSuccessful;
                handler.post(() -> {
                    if (finalIsSuccessful) {
                        dialog.dismiss();
                        Intent intent = new Intent(this, CustomerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        /* xem giải thích FLAG_ACTIVITY_CLEAR_TOP ở đây:
                        https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                        hoặc rê chuột lên trên để đọc docs
                         */
                        intent.putExtra("targetedFragment", R.id.customerShoppingCardFragment);
                        startActivity(intent);
                    } else {
                        showSnackBar("Failed to connect sever");
                    }
                });
            });
        });
    }

    private void setUpSnackbar() {
        snackbar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_SHORT);

        final View snackBarView = snackbar.getView();
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 140);

        snackBarView.setLayoutParams(params);
    }

    private void showSnackBar(String text) {
        snackbar.setText(text);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}