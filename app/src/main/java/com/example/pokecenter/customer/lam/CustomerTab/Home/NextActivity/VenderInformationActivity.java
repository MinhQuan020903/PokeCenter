package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Model.vender.Vender;
import com.example.pokecenter.customer.lam.Provider.FollowData;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivityVenderInformationBinding;
import com.example.pokecenter.vender.VenderTab.Chat.ChatRoomActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VenderInformationActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {

    private ActivityVenderInformationBinding binding;

    Vender receiveVender;

    private RecyclerView rcvProduct;

    private ProductAdapter productAdapter;

    private List<Product> venderProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        binding = ActivityVenderInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.outbox.setPadding(0, 0, 0, getNavigationBarHeight());

        receiveVender = (Vender) getIntent().getParcelableExtra("vender object");

        binding.backButton.setOnClickListener(view -> {
            binding.progressBar.setVisibility(View.VISIBLE);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                try {
                    new FirebaseSupportCustomer().updateFollowCount(receiveVender.getVenderId(), receiveVender.getFollowCount());
                } catch (IOException e) {

                }

                handler.post(() -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    finish();
                });
            });
        });

        setUpdataVenderInfo();

        setUpVenderProductRcv(receiveVender.getVenderId());

        /* Set up search bar */
        binding.searchProductBar.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = binding.searchProductBar.getText().toString();
                searchProduct(searchText);
                return true;
            }
            return false;

        });

        binding.searchButton.setOnClickListener(view -> {

            String searchText = binding.searchProductBar.getText().toString();
            searchProduct(searchText);

        });

        binding.venderInfoScreen.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(binding.venderInfoScreen.getWindowToken(), 0);
        });

        /* Set up follow button */

        if (FollowData.hasData) {
            binding.progressBarFollow.setVisibility(View.INVISIBLE);
            if (FollowData.fetchedFollowing.containsKey(receiveVender.getVenderId())) {
                binding.iconFollow.setImageDrawable(getDrawable(R.drawable.lam_followed_24));
                binding.statusFollow.setText("Followed");
            } else {
                binding.iconFollow.setImageDrawable(getDrawable(R.drawable.lam_follow_24));
                binding.statusFollow.setText("Follow");
            }
        } else {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                boolean isSuccessful = true;
                try {
                    new FirebaseSupportCustomer().fetchingFollowData();
                } catch (IOException e) {
                    isSuccessful = false;
                }
                boolean finalIsSuccessful = isSuccessful;
                handler.post(() -> {
                    if (finalIsSuccessful) {
                        FollowData.hasData = true;

                        binding.progressBarFollow.setVisibility(View.INVISIBLE);
                        if (FollowData.fetchedFollowing.containsKey(receiveVender.getVenderId())) {
                            binding.iconFollow.setImageDrawable(getDrawable(R.drawable.lam_followed_24));
                            binding.statusFollow.setText("Followed");
                        } else {
                            binding.iconFollow.setImageDrawable(getDrawable(R.drawable.lam_follow_24));
                            binding.statusFollow.setText("Follow");
                        }

                    } else {
                        FollowData.hasData = false;
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    binding.progressBarFollow.setVisibility(View.INVISIBLE);
                });
            });
        }

        binding.followButton.setOnClickListener(view -> {
            if (FollowData.fetchedFollowing.containsKey(receiveVender.getVenderId())) {
                /* Đang follow -> Bỏ follow */
                binding.iconFollow.setImageDrawable(getDrawable(R.drawable.lam_follow_24));
                binding.statusFollow.setText("Follow");

                FollowData.fetchedFollowing.remove(receiveVender.getVenderId());

                receiveVender.setFollowCount(receiveVender.getFollowCount() - 1);

            } else {

                /* Không follow -> Following */
                binding.iconFollow.setImageDrawable(getDrawable(R.drawable.lam_followed_24));
                binding.statusFollow.setText("Followed");

                FollowData.fetchedFollowing.put(receiveVender.getVenderId(), true);

                receiveVender.setFollowCount(receiveVender.getFollowCount() + 1);
            }

            binding.followCount.setText(String.valueOf(receiveVender.getFollowCount()));
        });
    }

    private void searchProduct(String searchText) {

        if (!searchText.isEmpty()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            binding.searchProductBar.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(binding.searchProductBar.getWindowToken(), 0);

            productAdapter.setData(venderProduct.stream().filter(product -> product.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList()));

        }

    }

    private void setUpVenderProductRcv(String venderId) {

        rcvProduct = binding.rcvGridProduct;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        venderProduct = ProductData.getListProducts().stream().filter(product -> product.getVenderId().equals(venderId)).collect(Collectors.toList());

        productAdapter = new ProductAdapter(this, venderProduct, this);
        rcvProduct.setAdapter(productAdapter);
    }

    private void setUpdataVenderInfo() {
        if(receiveVender.getBackground()!=null) {
            Picasso.get().load(receiveVender.getBackground()).into(binding.backgroundImage);
        }
        Picasso.get().load(receiveVender.getAvatar()).into(binding.venderAvatar);
        binding.shopName.setText(receiveVender.getShopName());
        binding.registrationDate.setText("Registration: " + receiveVender.getRegistrationDate());
        binding.totalProduct.setText(String.valueOf(receiveVender.getTotalProduct()));
        binding.followCount.setText(String.valueOf(receiveVender.getFollowCount()));

    }



    public int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onPokemonCardClick(Pokemon pokemon) {

    }

    @Override
    public void onProductCardClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product object", product);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        if (binding.statusFollow.getText().toString().equals("Follow")) {

            executor.execute(() -> {
                try {
                    new FirebaseSupportCustomer().deleteFollowing(receiveVender.getVenderId());
                } catch (IOException e) {

                }
            });

        } else {

            executor.execute(() -> {
                try {
                    new FirebaseSupportCustomer().addFollowing(receiveVender.getVenderId());
                } catch (IOException e) {

                }
            });

        }
    }
}