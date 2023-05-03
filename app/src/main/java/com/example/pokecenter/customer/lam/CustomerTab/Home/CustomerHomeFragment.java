package com.example.pokecenter.customer.lam.CustomerTab.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.CustomerActivity;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.PokedexActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.ProductByPokemonActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.SearchProductActivity;
import com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity.TrendingProductsActivity;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.FragmentCustomerHomeBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CustomerHomeFragment extends Fragment implements PokemonRecyclerViewInterface {

    private FragmentCustomerHomeBinding binding;
    private RecyclerView rcvPokemon;
    private PokemonAdapter pokemonAdapter;

    private RecyclerView rcvProduct;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentCustomerHomeBinding.inflate(inflater, container, false);

        // Move to Profile Fragment when User click on avatarImage
        binding.avatarImage.setOnClickListener(view -> {
            // Set selectedItem in Bottom Nav Bar
            CustomerActivity.customerBottomNavigationView.setSelectedItemId(R.id.customerProfileFragment);
        });

        /* search bar logic */
        binding.searchProductBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String searchText = binding.searchProductBar.getText().toString();
                    if (!searchText.isEmpty()) {
                        goToSearchActivity(searchText);
                    }
                    return true;
                }
                return false;
            }
        });

        binding.searchByTextButton.setOnClickListener(view -> {
            String searchText = binding.searchProductBar.getText().toString();
            if (!searchText.isEmpty()) {
                goToSearchActivity(searchText);
            }
        });

        binding.viewAllPokedex.setOnClickListener(view -> {

            startActivity(new Intent(getActivity(), PokedexActivity.class));
        });

        // _______Pokedex________
        rcvPokemon = binding.rcvHorizontalPokemon;
        pokemonAdapter = new PokemonAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rcvPokemon.setLayoutManager(linearLayoutManager);
        rcvPokemon.setAdapter(pokemonAdapter);

        if (PokeApiFetcher.pokemonHomeDemoData.isEmpty()) {
            // Chỗ này là để set Data cho Adapter là những cái loading Card
            ArrayList<Pokemon> loadingPokemons = new ArrayList<>();
            for (int i = 1; i <= 10; ++i) {
                loadingPokemons.add(new Pokemon(01, "", "", ""));
            }

            pokemonAdapter.setData(loadingPokemons);


            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            for (int i = 0; i < loadingPokemons.size(); ++i) {
                Pokemon poke = loadingPokemons.get(i);

                int finalI = i;
                executor.execute(() -> {
                    Pokemon fetchedPokemon = PokeApiFetcher.fetchPokemonRandom();
                    handler.post(() -> {
                        poke.setName(fetchedPokemon.getName());
                        poke.setImageUrl(fetchedPokemon.getImageUrl());
                        poke.setType(fetchedPokemon.getType());
                        pokemonAdapter.updateItem(finalI);
                    });
                });
            }
        }
        else {
            pokemonAdapter.setData(PokeApiFetcher.pokemonHomeDemoData);
        }

        // _______Trending________
        binding.viewAllTrending.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), TrendingProductsActivity.class));
        });

        rcvProduct = binding.rcvGridProduct;
        productAdapter = new ProductAdapter(getActivity(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        // Setup Loading Trending Product (UX)
        productAdapter.setData(mockTrendingData());
        rcvProduct.setAdapter(productAdapter);

        setTrendingProducts();

        return binding.getRoot();
    }

    private void setTrendingProducts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccessful = true;
            try {
                new FirebaseSupportCustomer().fetchingTrendingProductId();
            } catch (IOException e) {
                isSuccessful = false;
            }
            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {
                    List<Product> trendingProducts = ProductData.trendingProductsId.subList(0,4).stream().map(item -> ProductData.fetchedProducts.get(item)).collect(Collectors.toList());
                    productAdapter.setData(trendingProducts);
                } else {
                    Toast.makeText(getActivity(), "Fail to load trending products", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });

    }

    private void goToSearchActivity(String searchText) {
        Intent intent = new Intent(getActivity(), SearchProductActivity.class);
        intent.putExtra("searchText", searchText);
        startActivity(intent);
    }

    private ArrayList<Product> mockTrendingData() {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 1;i <= 4; ++i) {
            products.add(new Product(
                    null,
                    null,
                    null,
                    null
            ));
        }
        return products;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPokemonCardClick(Pokemon pokemon) {
        if (!pokemon.getName().isEmpty()) {
            Intent intent = new Intent(getActivity(), ProductByPokemonActivity.class);
            intent.putExtra("pokemonName", pokemon.getName());
            startActivity(intent);
        }
    }

    @Override
    public void onProductCardClick(Product product) {
//        if (!product.getDefaultImageUrl().isEmpty()) {
//            NavDirections action = CustomerFragmentDirections.actionCustomerFragmentToProductDetailFragment(product);
//
//            NavHostFragment.findNavController(CustomerHomeFragment.this)
//                    .navigate(action);
//        }
    }
}