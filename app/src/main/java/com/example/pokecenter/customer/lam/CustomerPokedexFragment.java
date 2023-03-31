package com.example.pokecenter.customer.lam;

import static com.example.pokecenter.customer.lam.API.PokeApiFetcher.allPokeName;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.example.pokecenter.databinding.FragmentCustomerPokedexBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerPokedexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerPokedexFragment extends Fragment {

    private FragmentCustomerPokedexBinding binding;

    private RecyclerView rcvGridPokemon;
    private PokemonAdapter pokemonAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerPokedexFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerPokedexFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerPokedexFragment newInstance(String param1, String param2) {
        CustomerPokedexFragment fragment = new CustomerPokedexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        System.out.println("recreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerPokedexBinding.inflate(inflater, container, false);

        rcvGridPokemon = binding.rcvGridPokemon;
        pokemonAdapter = new PokemonAdapter(getContext());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rcvGridPokemon.setLayoutManager(gridLayoutManager);

        rcvGridPokemon.setAdapter(pokemonAdapter);

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);



        binding.searchNameButton.setOnClickListener(view -> {
            // Ẩn Keyboard
            inputMethodManager.hideSoftInputFromWindow(binding.searchNameButton.getWindowToken(), 0);

            String inputText = String.valueOf(binding.searchNamePokemonBar.getText());
            ProgressBar progressBar = binding.fetchPokemonByNameProgressBar;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());


            ArrayList<Pokemon> pokemonLoading = new ArrayList<>();

            for (int i=0; i<=900; ++i) {
                if (allPokeName[i].contains(inputText)) {
                    pokemonLoading.add(new Pokemon(allPokeName[i], "", ""));
                }
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

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}