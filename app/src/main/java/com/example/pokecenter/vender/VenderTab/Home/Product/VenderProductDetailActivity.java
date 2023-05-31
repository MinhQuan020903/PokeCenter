package com.example.pokecenter.vender.VenderTab.Home.Product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.VenderInformationActivity;
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
import com.example.pokecenter.databinding.ActivityVenderProductDetailBinding;
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

public class VenderProductDetailActivity extends AppCompatActivity {

    ActivityVenderProductDetailBinding binding;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    Product receiveProduct;

    private boolean isFavourite = false;
    View viewDialog;
    BottomSheetDialog dialog;
    ArrayAdapter<String> adapterItems;
    Snackbar snackbar;

    int selectedOptionPosition = -1;

    List<ReviewProduct> reviewsProduct = new ArrayList<>();
    ReviewProductAdapter reviewProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // binding
        binding = ActivityVenderProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Pre setup viewDialog
        viewDialog = getLayoutInflater().inflate(R.layout.lam_bottom_sheet_place_order, null);

        optionCurrentQuantity = viewDialog.findViewById(R.id.product_current_quantity);

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);

        //
        receiveProduct = (Product) getIntent().getSerializableExtra("product object");
        adapterItems = new ArrayAdapter<>(this, R.layout.lam_text_option_list_item, receiveProduct.getAllOptionsName());

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

        } else {
            binding.productPrice.setText(currencyFormatter.format(receiveProduct.getOptions().get(0).getPrice()) + " - " + currencyFormatter.format(receiveProduct.getOptions().get(receiveProduct.getOptions().size() - 1).getPrice()));
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
        setUpSnackbar();
    }

    private void fetchingAndSetUpReviewProduct() {

        ListView lvReviews = binding.lvReviewProduct;

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

                    reviewProductAdapter = new ReviewProductAdapter(this, reviewsProduct);
                    lvReviews.setAdapter(reviewProductAdapter);

                    int lvReviewsHeight = 0;
                    for (int i = 0; i < reviewsProduct.size(); ++i ) {
                        View item  = reviewProductAdapter.getView(i, null, lvReviews);

                        item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        int itemHeight = item.getMeasuredHeight();

                        lvReviewsHeight += itemHeight;
                    }
                    ViewGroup.LayoutParams params = lvReviews.getLayoutParams();
                    params.height = lvReviewsHeight;

                    lvReviews.setLayoutParams(params);

                } else {

                    Toast.makeText(this, "Failed to loading reviews product", Toast.LENGTH_SHORT).show();

                }

                // binding.progressBarReview.setVisibility(View.INVISIBLE);

            });
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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