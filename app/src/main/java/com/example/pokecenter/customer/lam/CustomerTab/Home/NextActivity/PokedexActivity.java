package com.example.pokecenter.customer.lam.CustomerTab.Home.NextActivity;

import static com.example.pokecenter.customer.lam.API.PokeApiFetcher.allPokeName;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.databinding.ActivityPokedexBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PokedexActivity extends AppCompatActivity implements PokemonRecyclerViewInterface {

    private ActivityPokedexBinding binding;
    private RecyclerView rcvGridPokemon;
    private PokemonAdapter pokemonAdapter;
    public static int index = 0;
    Button viewMoreButton;


    String inputText = "";
    InputMethodManager inputMethodManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        ExecutorService executor = Executors.newCachedThreadPool();
        Handler UiThread = new Handler(Looper.getMainLooper());



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.light_canvas));
            // change color of icons in status bar
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = ActivityPokedexBinding.inflate(getLayoutInflater());
        viewMoreButton = binding.viewMoreButton;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding.pokedexScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        rcvGridPokemon = binding.rcvGridPokemon;
        pokemonAdapter = new PokemonAdapter(this,  this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rcvGridPokemon.setLayoutManager(gridLayoutManager);

        rcvGridPokemon.setAdapter(pokemonAdapter);
        AddPokemonToRecyclerView();

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        // Tìm kiếm pokemon bằng tên
        binding.searchNamePokemonBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    inputText = binding.searchNamePokemonBar.getText().toString();
                    if (!inputText.isEmpty()) {
                        onSearchPokemon();
                    }

                    return true;
                }

                return false;
            }
        });

        binding.searchNameButton.setOnClickListener(view -> {

            inputText = binding.searchNamePokemonBar.getText().toString();

            if (inputText.isEmpty()) {
                return;
            }

            onSearchPokemon();
        });


        if (!inputText.isEmpty()) {
            viewMoreButton.setVisibility(View.INVISIBLE);
        }

        viewMoreButton.setOnClickListener(view -> {
            AddPokemonToRecyclerView();
        });

        setContentView(binding.getRoot());
    }

    void onSearchPokemon() {

        viewMoreButton.setVisibility(View.INVISIBLE);
        // Ẩn Keyboard
        inputMethodManager.hideSoftInputFromWindow(binding.searchNamePokemonBar.getWindowToken(), 0);

        // Clear pokemonApapter's Data;
        pokemonAdapter.clearData();

        // Clear pokemonSearchData;
        PokeApiFetcher.pokemonSearchData.clear();

        ArrayList<Pokemon> pokemonLoading = new ArrayList<>();

        int limit = 40;

        for (int i=0; i<=900; ++i) {
            if (allPokeName[i].contains(inputText)) {
                pokemonLoading.add(new Pokemon(-1, allPokeName[i], "", ""));
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

        ExecutorService executor = Executors.newCachedThreadPool();
        Handler handler = new Handler(Looper.getMainLooper());

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
    }

    void AddPokemonToRecyclerView() {
        ArrayList<Pokemon> pokemonLoading = new ArrayList<>();

        for (int i=1; i<=9; ++i) {
            pokemonLoading.add(new Pokemon(-1, "", "", ""));
        }

        pokemonAdapter.addData(pokemonLoading);

        viewMoreButton.setEnabled(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        for (int i = 0; i < pokemonLoading.size(); ++i) {
            Pokemon poke = pokemonLoading.get(i);

            int position = pokemonAdapter.getItemCount() - 9 + i;

            executor.execute(() -> {
                index = (index + 1) % 902;
                Pokemon fetchedPokemon = PokeApiFetcher.fetchPokemonById(index);
                handler.post(() -> {
                    poke.setId(fetchedPokemon.getId());
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