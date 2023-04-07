package com.example.pokecenter.customer.lam.Interface;

import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.customer.lam.Model.product.Product;

public interface RecyclerViewInterface {
    void onPokemonCardClick(Pokemon pokemon);

    void onProductCardClick(Product product);
}
