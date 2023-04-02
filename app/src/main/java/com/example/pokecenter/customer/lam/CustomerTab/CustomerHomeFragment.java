package com.example.pokecenter.customer.lam.CustomerTab;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.Interface.RecyclerViewInterface;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonAdapter;
import com.example.pokecenter.customer.lam.ProductByPokemonFragment;
import com.example.pokecenter.databinding.FragmentCustomerHomeBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHomeFragment extends Fragment implements RecyclerViewInterface {

    private FragmentCustomerHomeBinding binding;
    private RecyclerView rcvPokemon;
    private PokemonAdapter pokemonAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerHomeFragment newInstance(String param1, String param2) {
        CustomerHomeFragment fragment = new CustomerHomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentCustomerHomeBinding.inflate(inflater, container, false);

        // if statement checks if the device is running Android Marshmallow (API level 23) or higher,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();

            // change StatusBarColor
            window.setStatusBarColor(getColor(requireContext(), R.color.light_canvas));

            // change color of icons in status bar
            // C1:
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            /*
            C2:
            add: <item name="android:windowLightStatusBar">true</item>
            in the <style name="Theme.PokeCenter" in themes.xml
             */
        }

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

        if (PokeApiFetcher.pokemonDemoData.isEmpty()) {
            // Chỗ này là để set Data cho Adapter là những cái loading Card
            ArrayList<Pokemon> loadingPokemons = new ArrayList<>();
            for (int i = 1; i <= 10; ++i) {
                loadingPokemons.add(new Pokemon("loading", "", ""));
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
            pokemonAdapter.setData(PokeApiFetcher.pokemonDemoData);
        }


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        Pokemon pokemon = pokemonAdapter.getItem(position);
        if (!pokemon.getImageUrl().isEmpty()) {
            NavDirections action = CustomerFragmentDirections.actionCustomerFragmentToProductByPokemonFragment(pokemon, "CustomerHomeFragment");

            NavHostFragment.findNavController(CustomerHomeFragment.this)
                    .navigate(action);

        }
    }
}