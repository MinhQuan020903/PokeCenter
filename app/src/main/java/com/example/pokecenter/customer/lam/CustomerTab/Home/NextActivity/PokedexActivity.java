package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import static com.example.pokecenter.customer.lam.API.PokeApiFetcher.allPokeName;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.databinding.ActivityPokedexBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PokedexActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {

    private ActivityPokedexBinding binding;
    private RecyclerView rcvGridPokemon;
    private PokemonAdapter pokemonAdapter;
    int index = (int) (Math.random() * 900) + 1;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    String inputText = "";

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

        binding = ActivityPokedexBinding.inflate(getLayoutInflater());

        rcvGridPokemon = binding.rcvGridPokemon;
        pokemonAdapter = new PokemonAdapter(this,  this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rcvGridPokemon.setLayoutManager(gridLayoutManager);

        rcvGridPokemon.setAdapter(pokemonAdapter);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        if (!inputText.isEmpty()) {
            binding.viewMoreButton.setVisibility(View.INVISIBLE);
        }

        if (!PokeApiFetcher.pokemonSearchData.isEmpty()) {
            pokemonAdapter.setData(PokeApiFetcher.pokemonSearchData);
        }
        else if (!PokeApiFetcher.pokeDexDemoData.isEmpty()) {
            pokemonAdapter.setData(PokeApiFetcher.pokeDexDemoData);
        }
        else {
            // Lần đầu truy cập fragment
            AddPokemonToRecyclerView();
        }

        binding.viewMoreButton.setOnClickListener(view -> {
            AddPokemonToRecyclerView();
        });

        // Tìm kiếm pokemon bằng tên
        binding.searchNameButton.setOnClickListener(view -> {

            inputText = binding.searchNamePokemonBar.getText().toString();

            if (inputText.isEmpty()) {
                return;
            }

            binding.viewMoreButton.setVisibility(View.INVISIBLE);

            // Ẩn Keyboard
            inputMethodManager.hideSoftInputFromWindow(binding.searchNameButton.getWindowToken(), 0);

            // Clear pokemonApapter's Data;
            pokemonAdapter.clearData();

            // Clear pokemonSearchData;
            PokeApiFetcher.pokemonSearchData.clear();

            ArrayList<Pokemon> pokemonLoading = new ArrayList<>();

            int limit = 50;

            for (int i=0; i<=900; ++i) {
                if (allPokeName[i].contains(inputText)) {
                    pokemonLoading.add(new Pokemon(allPokeName[i], "", ""));
                    limit--;
                    if (limit == 0) {
                        break;
                    }
                }
            }

            if (pokemonLoading.isEmpty()) {
                return;
            }

            pokemonAdapter.setData(pokemonLoading);

            for (int i = 0; i < pokemonLoading.size(); ++i) {
                Pokemon poke = pokemonLoading.get(i);

                int finalI = i;
                executor.execute(() -> {
                    Pokemon fetchedPokemon = PokeApiFetcher.fetchPokemonByName(poke.getName());
                    handler.post(() -> {
                        poke.setName(fetchedPokemon.getName());
                        poke.setImageUrl(fetchedPokemon.getImageUrl());
                        poke.setType(fetchedPokemon.getType());
                        pokemonAdapter.updateItem(finalI);
                    });
                });
            }
        });

        setContentView(binding.getRoot());
    }

    private void AddPokemonToRecyclerView() {
        ArrayList<Pokemon> pokemonLoading = new ArrayList<>();

        for (int i=1; i<=9; ++i) {
            pokemonLoading.add(new Pokemon("", "", ""));
        }

        pokemonAdapter.addData(pokemonLoading);

        binding.viewMoreButton.setEnabled(false);

        for (int i = 0; i < pokemonLoading.size(); ++i) {
            Pokemon poke = pokemonLoading.get(i);

            int position = pokemonAdapter.getItemCount() - 9 + i;

            executor.execute(() -> {
                index = (index + 1) % 902;
                Pokemon fetchedPokemon = PokeApiFetcher.fetchPokemonById(index);
                handler.post(() -> {
                    poke.setName(fetchedPokemon.getName());
                    poke.setImageUrl(fetchedPokemon.getImageUrl());
                    poke.setType(fetchedPokemon.getType());
                    pokemonAdapter.updateItem(position);
                    if (position + 1 == pokemonAdapter.getItemCount()) {
                        binding.viewMoreButton.setEnabled(true);
                    }
                });
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onPokemonCardClick(Pokemon pokemon) {
        if (!pokemon.getName().isEmpty()) {
            Intent intent = new Intent(this, ProductByPokemonActivity.class);
            intent.putExtra("pokemonName", pokemon.getName());
            startActivity(intent);
        }
    }

    @Override
    public void onProductCardClick(Product product) {

    }
}