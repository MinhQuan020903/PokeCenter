package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Cart.CheckoutActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.WishListActivity;
import com.example.pokecenter.customer.lam.Model.cart.Cart;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.review_product.ReviewProduct;
import com.example.pokecenter.customer.lam.Model.review_product.ReviewProductAdapter;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.customer.lam.Provider.WishListData;
import com.example.pokecenter.customer.lam.SliderAdapter;
import com.example.pokecenter.databinding.ActivityProductDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    Product receiveProduct;

    private boolean isFavourite = false;
    View viewDialog;
    BottomSheetDialog dialog;
    ArrayAdapter<String> adapterItems;
    Snackbar snackbar;

    int selectedOptionPosition = -1;

    List<ReviewProduct> reviewsProduct = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // binding
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.paddingBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getNavigationBarHeight()));

        // Pre setup viewDialog
        viewDialog = getLayoutInflater().inflate(R.layout.lam_bottom_sheet_place_order, null);

        optionCurrentQuantity = viewDialog.findViewById(R.id.product_current_quantity);

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);

        //
        receiveProduct = (Product) getIntent().getSerializableExtra("product object");
        adapterItems = new ArrayAdapter<>(this, R.layout.lam_text_option_list_item, receiveProduct.getAllOptionsName());
        setUpLogicForBottomSheet(receiveProduct);

        binding.backButton.setOnClickListener(view ->  {
            finish();
        });

        /* Set up Slider Image */
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
        /* --------------------- */

        binding.productName.setText(receiveProduct.getName());

        if (receiveProduct.getOptions().size() == 1) {
            binding.productPrice.setText(currencyFormatter.format(receiveProduct.getOptions().get(0).getPrice()));
            binding.dropListDownOptions.setVisibility(View.GONE);

            if (receiveProduct.getOptions().get(0).getCurrentQuantity() == 0) {
                binding.bottomBar.setVisibility(View.GONE);
                binding.outOfStock.setVisibility(View.VISIBLE);
            }

        } else {
            if (receiveProduct.getMinPrice() == receiveProduct.getMaxPrice()) {
                binding.productPrice.setText(currencyFormatter.format(receiveProduct.getMinPrice()));
            } else {
                binding.productPrice.setText(currencyFormatter.format(receiveProduct.getMinPrice()) + " - " + currencyFormatter.format(receiveProduct.getMaxPrice()));
            }
        }

        /* Set up Droplist down options */
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

                if (receiveProduct.getOptions().get(position).getCurrentQuantity() == 0) {
                    binding.bottomBar.setVisibility(View.GONE);
                    binding.outOfStock.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomBar.setVisibility(View.VISIBLE);
                    binding.outOfStock.setVisibility(View.GONE);
                }
            }
        });
        /* ------------------ */


        /* Set up Review data */
        fetchingAndSetUpReviewProduct();
        /* ------------------ */


        binding.productSold.setText("Sold " + receiveProduct.getProductSold());

        /* Logic favoriteButton & wishList */
        if (WishListData.fetchedWishList.containsKey(receiveProduct.getId())) {

            isFavourite = true;

            ColorFilter colorFilter = new PorterDuffColorFilter(getColor(R.color.dark_secondary), PorterDuff.Mode.SRC_IN);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.lam_baseline_favorite_28);
            drawable.setColorFilter(colorFilter);
            binding.favoriteButton.setImageDrawable(drawable);
        }
        binding.favoriteButton.setOnClickListener(view -> {

            isFavourite = !isFavourite;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            if (isFavourite) {
                // Create a color filter with the desired tint color
                ColorFilter colorFilter = new PorterDuffColorFilter(getColor(R.color.dark_secondary), PorterDuff.Mode.SRC_IN);
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.lam_baseline_favorite_28);
                drawable.setColorFilter(colorFilter);
                binding.favoriteButton.setImageDrawable(drawable);



                executor.execute(() -> {

                    WishListData.fetchedWishList.put(receiveProduct.getId(), true);

                    boolean isSuccessful = true;
                    try {
                        new FirebaseSupportCustomer().addWishListItem(receiveProduct.getId());
                    } catch (IOException e) {
                        isSuccessful = false;
                    }

                    boolean finalIsSuccessful = isSuccessful;
                    handler.post(() -> {

                        if (finalIsSuccessful) {
                            showSnackBar("Added product to the wish list", true);
                        } else {
                            showSnackBar("Failed to connect server", false);
                        }

                    });
                });

            } else {
                binding.favoriteButton.setImageDrawable(getDrawable(R.drawable.lam_baseline_favorite_border_28));
                executor.execute(() -> {

                    WishListData.fetchedWishList.remove(receiveProduct.getId());

                    boolean isSuccessful = true;
                    try {
                        new FirebaseSupportCustomer().removeWishListItem(receiveProduct.getId());
                    } catch (IOException e) {
                        isSuccessful = false;
                    }

                    boolean finalIsSuccessful = isSuccessful;
                    handler.post(() -> {

                        if (finalIsSuccessful) {
                            showSnackBar("Deleted product from the wish list", true);
                        } else {
                            showSnackBar("Failed to connect server", false);
                        }

                    });
                });
            }
        });
        /* ------------------------------- */

        /* Vender Setup */
        fetchingAndSetUpVenderInfo(receiveProduct.getVenderId());
        /* ------------ */

        /* Set up Bottom Bar */
        binding.shoppingCartButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("targetedFragment", R.id.customerShoppingCardFragment);
            startActivity(intent);
        });
        binding.addToCartButton.setOnClickListener(view -> {
            if (selectedOptionPosition == -1 && receiveProduct.getOptions().size() > 1) {
                binding.warning.setVisibility(View.VISIBLE);
            } else {
                if (selectedOptionPosition == -1) {
                    selectedOptionPosition = 0;
                }
                openAddToCartBottomSheet(receiveProduct);
            }


        });
        binding.orderNowButton.setOnClickListener(view -> {
            if (selectedOptionPosition == -1 && receiveProduct.getOptions().size() > 1) {
                binding.warning.setVisibility(View.VISIBLE);
            } else {
                if (selectedOptionPosition == -1) {
                    selectedOptionPosition = 0;
                }
                openOrderNowBottomSheet(receiveProduct);
            }
        });
        /* ----------------- */

        binding.productDesc.setText(receiveProduct.getDesc().replace("\\n", System.getProperty("line.separator")));

        /*
        setup snack bar
        setUpSnackbar() phải để sau setContentView
         */
        setUpSnackbar();
    }

    private void fetchingAndSetUpReviewProduct() {

        //ListView lvReviews = binding.lvReviewProduct;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            boolean isSuccessful = true;
            List<ReviewProduct> fetchedReviewsProduct = null;

            try {
                fetchedReviewsProduct = new FirebaseSupportCustomer().fetchingReviewsForProductId(receiveProduct.getId());
            } catch (IOException e) {
                isSuccessful = false;
            }

            boolean finalIsSuccessful = isSuccessful;
            List<ReviewProduct> finalFetchedReviewsProduct = fetchedReviewsProduct;
            handler.post(() -> {

                if (finalIsSuccessful) {

                    reviewsProduct = finalFetchedReviewsProduct;

                    binding.productReviewCount.setText(reviewsProduct.size() + " reviews");

                    int sumRating = 0;
                    for (int i = 0; i < reviewsProduct.size(); ++i) {
                        sumRating += reviewsProduct.get(i).getRate();
                    }
                    if (sumRating == 0) {
                        binding.productRate.setText("5.0");
                    } else {
                        binding.productRate.setText(String.format("%.1f", (double)sumRating / reviewsProduct.size()));
                    }


                    for (int i = 0; i < reviewsProduct.size(); ++i) {
                        View view = getLayoutInflater().inflate(R.layout.lam_review_product_item, null);
                        ReviewProduct review = reviewsProduct.get(i);

                        ImageView avatarImage = view.findViewById(R.id.avatarImage);
                        Picasso.get().load(review.getCustomerImage()).into(avatarImage);

                        TextView customerName = view.findViewById(R.id.customerName);
                        customerName.setText(review.getCustomerName());

                        TextView reviewTitle = view.findViewById(R.id.review_title);
                        reviewTitle.setText(review.getTitle());

                        LinearLayout rate = view.findViewById(R.id.product_rate);

                        // Set layout params with end margin of 8dp
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));

                        // Add the appropriate number of stars to the LinearLayout
                        for (int j = 1; j <= review.getRate(); ++j) {

                            // Create a new ImageView object for gold fill star
                            ImageView starGoldFill = new ImageView(this);
                            starGoldFill.setImageResource(R.drawable.lam_star_fill_gold_24);

                            rate.addView(starGoldFill, layoutParams);

                        }

                        for (int j = 1; j <= 5 - review.getRate(); ++j) {

                            // Create a new ImageView object for black outline star
                            ImageView starBlackOutline = new ImageView(this);
                            starBlackOutline.setImageResource(R.drawable.lam_star_outline_black_24);

                            rate.addView(starBlackOutline, layoutParams);
                        }

                        TextView reviewContent = view.findViewById(R.id.review_content);
                        reviewContent.setText(review.getContent());


                        TextView createDate = view.findViewById(R.id.review_create_date);
                        createDate.setText(review.getCreateDate());

                        binding.reviewList.addView(view);

                    }

                } else {

                    Toast.makeText(this, "Failed to loading reviews product", Toast.LENGTH_SHORT).show();

                }

                // binding.progressBarReview.setVisibility(View.INVISIBLE);

            });
        });

    }

    private Vender fetchedVender;

    private void fetchingAndSetUpVenderInfo(String venderId) {

        binding.progressBar.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {

            Vender vender = null;
            boolean isSuccessful = true;
            try {
                vender = new FirebaseSupportCustomer().fetchingVenderById(venderId);
            } catch (IOException e) {
                isSuccessful = false;
            }

            Vender finalVender = vender;
            boolean finalIsSuccessful = isSuccessful;

            handler.post(() -> {
                if (finalIsSuccessful) {

                    Picasso.get().load(finalVender.getAvatar()).into(binding.venderAvatar);
                    binding.shopName.setText(finalVender.getShopName());
                    binding.registrationDate.setText("Joined: " + finalVender.getRegistrationDate());
                    binding.totalProduct.setText(String.valueOf(finalVender.getTotalProduct()));
                    binding.followCount.setText(String.valueOf(finalVender.getFollowCount()));

                    fetchedVender = finalVender;

                    binding.venderInfo.setOnClickListener(view -> {
                        Intent intent = new Intent(this, VenderInformationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("vender object", fetchedVender);
                        startActivity(intent);
                    });

                } else {
                    binding.informText.setVisibility(View.VISIBLE);
                }

                binding.progressBar.setVisibility(View.INVISIBLE);

            });
        });


    }


    private void openAddToCartBottomSheet(Product product) {
        viewDialog.findViewById(R.id.add_to_cart_button).setVisibility(View.VISIBLE);
        viewDialog.findViewById(R.id.order_now_button).setVisibility(View.GONE);

        setUpData(product);

        dialog.show();
    }

    private void openOrderNowBottomSheet(Product product) {
        viewDialog.findViewById(R.id.add_to_cart_button).setVisibility(View.GONE);
        viewDialog.findViewById(R.id.order_now_button).setVisibility(View.VISIBLE);

        setUpData(product);

        dialog.show();
    }

    TextView optionCurrentQuantity;

    private void setUpData(Product product) {
        Option selectedOption = product.getOptions().get(selectedOptionPosition);

        TextView optionName = viewDialog.findViewById(R.id.option_name);


        ImageView optionImage = viewDialog.findViewById(R.id.option_image);

        if (product.getOptions().size() == 1) {
            Picasso.get().load(product.getImages().get(0)).into(optionImage);
            optionName.setText(product.getName());
        } else {
            optionName.setText(product.getOptions().get(selectedOptionPosition).getOptionName());
            if (selectedOption.getOptionImage().isEmpty()) {
                Picasso.get().load(product.getImages().get(0)).into(optionImage);
            } else {
                Picasso.get().load(selectedOption.getOptionImage()).into(optionImage);
            }
        }

        TextView priceTextView = viewDialog.findViewById(R.id.option_price);

        priceTextView.setText(currencyFormatter.format(selectedOption.getPrice()));
        optionCurrentQuantity.setText("Stock: " + selectedOption.getCurrentQuantity());
    }

    private void setUpLogicForBottomSheet(Product product) {

        TextView productCount = viewDialog.findViewById(R.id.product_count);
        ImageButton incButton = viewDialog.findViewById(R.id.inc_button);
        ImageButton decButton = viewDialog.findViewById(R.id.dec_button);

        incButton.setOnClickListener(view -> {
            int count = Integer.parseInt(productCount.getText().toString());

            if (!optionCurrentQuantity.getText().toString().isEmpty()) {
                // optionCurrentQuantity = Stock: 123
                String numberStr = optionCurrentQuantity.getText().toString().replaceAll("\\D+", "");
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
            addToCart.setEnabled(false);
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
                        addToCart.setEnabled(true);
                        dialog.dismiss();
                        showSnackBar("Added to cart", false);
                    } else {

                    }
                });
            });

        });

        /* Logic Order Button in Bottom Sheet */
        Button orderNow = viewDialog.findViewById(R.id.order_now_button);
        orderNow.setOnClickListener(view -> {

            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("orderNowCart", new Cart(product, Integer.parseInt(productCount.getText().toString()), selectedOptionPosition));

            dialog.dismiss();

            startActivity(intent);
        });
    }

    private void setUpSnackbar() {
        snackbar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_SHORT);

        final View snackBarView = snackbar.getView();
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 150 + getNavigationBarHeight());

        snackBarView.setLayoutParams(params);
    }

    private void showSnackBar(String text, boolean hasAction) {
        snackbar.setText(text);

        if (hasAction) {
            snackbar.setAction("View more", view -> {

                Intent intent = new Intent(this, WishListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            })
                    .setActionTextColor(getColor(R.color.light_primary));
        }

        snackbar.show();
    }

    protected void onRestart() {
        super.onRestart();

        /* Recreate this Activity when backbutton on VenderInformationActivity */

        /*
        Trong VenderInformationActivity có thể thay đổi số follower được hiển thị ở màn hình này
        nên cần reload để update lại số follower
        */

        fetchingAndSetUpVenderInfo(receiveProduct.getVenderId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        Ở đây không set binding = null
        vì page này cần đợi API call rồi mới loading dữ liệu
        Giả sử khi API call chưa xong mà người dùng nhấn backbutton dẫn đến destroy -> binding = null
        lúc API trả về data và những đoạn code như Picasso.get().load(finalVender.getAvatar()).into(binding.venderAvatar);
        sẽ được gọi. mà lúc này binding = null -> sinh ra lỗi ObjectNullException
         */
    }

    public int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}