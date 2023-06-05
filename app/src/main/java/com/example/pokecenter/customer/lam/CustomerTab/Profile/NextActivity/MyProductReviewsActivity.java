package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.PurchasedProductRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.purchasedProduct.PurchasedProduct;
import com.example.pokecenter.customer.lam.Model.purchasedProduct.PurchasedProductAdapter;
import com.example.pokecenter.databinding.ActivityMyProductReviewsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyProductReviewsActivity extends AppCompatActivity implements PurchasedProductRecyclerViewInterface {

    private ActivityMyProductReviewsBinding binding;
    private List<PurchasedProduct> myPurchasedProducts;

    private RecyclerView rcvPurchasedProduct;
    private PurchasedProductAdapter purchasedProductAdapter;
    private View viewDialog;
    private BottomSheetDialog dialog;
    private int rate = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyProductReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getSupportActionBar().setTitle("Product Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchingAndSetupRecyclerView();

        setupBottomSheet();

    }

    private void setupBottomSheet() {
        viewDialog = getLayoutInflater().inflate(R.layout.lam_bottom_sheet_review_purchased_product, null);

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        viewDialog.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        ImageButton star1 = viewDialog.findViewById(R.id.star1);
        ImageButton star2 = viewDialog.findViewById(R.id.star2);
        ImageButton star3 = viewDialog.findViewById(R.id.star3);
        ImageButton star4 = viewDialog.findViewById(R.id.star4);
        ImageButton star5 = viewDialog.findViewById(R.id.star5);

        star1.setOnClickListener(view -> {
            star2.setImageResource(R.drawable.lam_star_outline_black_24);
            star3.setImageResource(R.drawable.lam_star_outline_black_24);
            star4.setImageResource(R.drawable.lam_star_outline_black_24);
            star5.setImageResource(R.drawable.lam_star_outline_black_24);

            rate = 1;
        });

        star2.setOnClickListener(view -> {
            star1.setImageResource(R.drawable.lam_star_fill_gold_24);
            star2.setImageResource(R.drawable.lam_star_fill_gold_24);
            star3.setImageResource(R.drawable.lam_star_outline_black_24);
            star4.setImageResource(R.drawable.lam_star_outline_black_24);
            star5.setImageResource(R.drawable.lam_star_outline_black_24);

            rate = 2;
        });

        star3.setOnClickListener(view -> {
            star1.setImageResource(R.drawable.lam_star_fill_gold_24);
            star2.setImageResource(R.drawable.lam_star_fill_gold_24);
            star3.setImageResource(R.drawable.lam_star_fill_gold_24);
            star4.setImageResource(R.drawable.lam_star_outline_black_24);
            star5.setImageResource(R.drawable.lam_star_outline_black_24);

            rate = 3;
        });

        star4.setOnClickListener(view -> {
            star1.setImageResource(R.drawable.lam_star_fill_gold_24);
            star2.setImageResource(R.drawable.lam_star_fill_gold_24);
            star3.setImageResource(R.drawable.lam_star_fill_gold_24);
            star4.setImageResource(R.drawable.lam_star_fill_gold_24);
            star5.setImageResource(R.drawable.lam_star_outline_black_24);

            rate = 4;
        });

        star5.setOnClickListener(view -> {
            star1.setImageResource(R.drawable.lam_star_fill_gold_24);
            star2.setImageResource(R.drawable.lam_star_fill_gold_24);
            star3.setImageResource(R.drawable.lam_star_fill_gold_24);
            star4.setImageResource(R.drawable.lam_star_fill_gold_24);
            star5.setImageResource(R.drawable.lam_star_fill_gold_24);

            rate = 5;
        });

    }

    private void fetchingAndSetupRecyclerView() {

        rcvPurchasedProduct = binding.rcvPurchasedProduct;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvPurchasedProduct.setLayoutManager(linearLayoutManager);

        purchasedProductAdapter = new PurchasedProductAdapter(this, myPurchasedProducts, this);
        rcvPurchasedProduct.setAdapter(purchasedProductAdapter);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            boolean isSuccess = true;
            List<PurchasedProduct> fetchedPurchasedProducts = null;

            try {
                fetchedPurchasedProducts = new FirebaseSupportCustomer().fetchingPurchasedProducts();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            List<PurchasedProduct> finalFetchedPurchasedProducts = fetchedPurchasedProducts;
            handler.post(() -> {

                if (finalIsSuccess) {

                    myPurchasedProducts = finalFetchedPurchasedProducts;
                    purchasedProductAdapter.setData(myPurchasedProducts);

                    if (finalFetchedPurchasedProducts.size() == 0) {
                        binding.informText.setText("You haven't purchased any products yet.");
                        binding.informText.setVisibility(View.VISIBLE);
                    }

                } else {

                    binding.informText.setText("Failed to connect server");
                    binding.informText.setVisibility(View.VISIBLE);

                }

                binding.progressBar.setVisibility(View.INVISIBLE);
            });
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onReviewButtonClick(int position) {

        EditText title = viewDialog.findViewById(R.id.review_title);
        TextView titleErrorText = viewDialog.findViewById(R.id.title_error_text);

        EditText content = viewDialog.findViewById(R.id.review_content);
        Button finishButton = viewDialog.findViewById(R.id.finish_button);

        // Refresh Bottom sheet
        title.setText("");
        titleErrorText.setVisibility(View.GONE);
        content.setText("");
        finishButton.setText("Finish");

        ImageButton star1 = viewDialog.findViewById(R.id.star1);
        ImageButton star2 = viewDialog.findViewById(R.id.star2);
        ImageButton star3 = viewDialog.findViewById(R.id.star3);
        ImageButton star4 = viewDialog.findViewById(R.id.star4);
        ImageButton star5 = viewDialog.findViewById(R.id.star5);

        star1.setImageResource(R.drawable.lam_star_fill_gold_24);
        star2.setImageResource(R.drawable.lam_star_fill_gold_24);
        star3.setImageResource(R.drawable.lam_star_fill_gold_24);
        star4.setImageResource(R.drawable.lam_star_fill_gold_24);
        star5.setImageResource(R.drawable.lam_star_fill_gold_24);
        // end.

        finishButton.setOnClickListener(view -> {

            if (title.getText().toString().isEmpty()) {
                titleErrorText.setVisibility(View.VISIBLE);
                return;
            } else {
                titleErrorText.setVisibility(View.GONE);
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            finishButton.setVisibility(View.INVISIBLE);

            executor.execute(() -> {

                boolean isSuccess = true;

                try {
                    new FirebaseSupportCustomer().addProductReview(
                            myPurchasedProducts.get(position).getProductId(),
                            title.getText().toString(),
                            content.getText().toString(),
                            rate
                    );
                } catch (IOException e) {
                    isSuccess = false;
                }

                boolean finalIsSuccess = isSuccess;
                handler.post(() -> {
                    
                    if (finalIsSuccess) {

                        myPurchasedProducts.get(position).setReviewed(true);
                        purchasedProductAdapter.notifyItemChanged(position);
                        dialog.dismiss();
                        Toast.makeText(this, "Thank you for your feedback on the product.", Toast.LENGTH_SHORT).show();
                        
                    } else {

                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        finishButton.setText("Try again!");

                    }

                    finishButton.setVisibility(View.VISIBLE);
                });
            });

            
        });

        dialog.show();

    }
}