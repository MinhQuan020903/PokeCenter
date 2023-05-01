package com.example.pokecenter.customer.lam.CustomerTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Model.product.ProductAdapter;
import com.example.pokecenter.databinding.FragmentCustomerHomeBinding;

import java.util.ArrayList;

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
            CustomerFragment.customerBottomNavigationView.setSelectedItemId(R.id.customerProfileFragment);
        });

        binding.viewAllPokedex.setOnClickListener(view -> {

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_customerFragment_to_customerPokedexFragment);
        });

        // _______Pokedex________
        rcvPokemon = binding.rcvHorizontalPokemon;
        pokemonAdapter = new PokemonAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rcvPokemon.setLayoutManager(linearLayoutManager);
        rcvPokemon.setAdapter(pokemonAdapter);

//        if (PokeApiFetcher.pokemonHomeDemoData.isEmpty()) {
//            // Chỗ này là để set Data cho Adapter là những cái loading Card
//            ArrayList<Pokemon> loadingPokemons = new ArrayList<>();
//            for (int i = 1; i <= 10; ++i) {
//                loadingPokemons.add(new Pokemon("loading", "", ""));
//            }
//
//            pokemonAdapter.setData(loadingPokemons);
//
//
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            Handler handler = new Handler(Looper.getMainLooper());
//
//            for (int i = 0; i < loadingPokemons.size(); ++i) {
//                Pokemon poke = loadingPokemons.get(i);
//
//                int finalI = i;
//                executor.execute(() -> {
//                    Pokemon fetchedPokemon = PokeApiFetcher.fetchPokemonRandom();
//                    handler.post(() -> {
//                        poke.setName(fetchedPokemon.getName());
//                        poke.setImageUrl(fetchedPokemon.getImageUrl());
//                        poke.setType(fetchedPokemon.getType());
//                        pokemonAdapter.updateItem(finalI);
//                    });
//                });
//            }
//        }
//        else {
//            pokemonAdapter.setData(PokeApiFetcher.pokemonHomeDemoData);
//        }

        // _______Trending________
        rcvProduct = binding.rcvGridProduct;
        productAdapter = new ProductAdapter(getContext(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvProduct.setLayoutManager(gridLayoutManager);

        // Fake Trending Product
        productAdapter.setData(mockTrendingData());
        rcvProduct.setAdapter(productAdapter);

        return binding.getRoot();
    }

    private ArrayList<Product> mockTrendingData() {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 1;i <= 4; ++i) {
            ArrayList<String> additionalImageUrl = new ArrayList<>();
            additionalImageUrl.add("https://cdn0.fahasa.com/media/catalog/product/8/9/8935312500578-4.jpg");
            additionalImageUrl.add("https://cdn0.fahasa.com/media/catalog/product/8/9/8935312500578-3.jpg");
            additionalImageUrl.add("https://cdn0.fahasa.com/media/catalog/product/8/9/8935312500578-_2_.jpg");
            products.add(new Product(
                    "Keeppley B0104 - Mini Bulbasaur (74 Mảnh Ghép)",
                    "https://cdn0.fahasa.com/media/catalog/product/8/9/8935312500578.jpg",
                    229000,
                    4.9f,
                    20,
                    additionalImageUrl
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
        if (!pokemon.getImageUrl().isEmpty()) {
            NavDirections action = CustomerFragmentDirections.actionCustomerFragmentToProductByPokemonFragment(pokemon);

            NavHostFragment.findNavController(CustomerHomeFragment.this)
                    .navigate(action);

        }
    }

    @Override
    public void onProductCardClick(Product product) {
        if (!product.getDefaultImageUrl().isEmpty()) {
            NavDirections action = CustomerFragmentDirections.actionCustomerFragmentToProductDetailFragment(product);

            NavHostFragment.findNavController(CustomerHomeFragment.this)
                    .navigate(action);
        }
    }
}