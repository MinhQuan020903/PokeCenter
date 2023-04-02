package com.example.pokecenter.customer.lam;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.PokeApiFetcher;
import com.example.pokecenter.customer.lam.CustomerTab.CustomerFragment;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.pokemon.PokemonBackgroundColor;
import com.example.pokecenter.databinding.FragmentCustomerHomeBinding;
import com.example.pokecenter.databinding.FragmentProductByPokemonBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductByPokemonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductByPokemonFragment extends Fragment {

    private FragmentProductByPokemonBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductByPokemonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductByPokemonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductByPokemonFragment newInstance(String param1, String param2) {
        ProductByPokemonFragment fragment = new ProductByPokemonFragment();
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
        binding = FragmentProductByPokemonBinding.inflate(inflater, container, false);

        Pokemon receivedPokemon = ProductByPokemonFragmentArgs.fromBundle(getArguments()).getPokemon();
        String sender = ProductByPokemonFragmentArgs.fromBundle(getArguments()).getSender();

        binding.pokeName.setText(receivedPokemon.getName());

        binding.backButton.setOnClickListener(view -> {
            switch (sender) {
                case "CustomerHomeFragment":
                    NavHostFragment.findNavController(this)
                            .navigateUp();
                    break;
                case "PokedexFragment":
                    NavHostFragment.findNavController(this)
                            .navigateUp();
                    break;
            }
        });

        return binding.getRoot();
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}