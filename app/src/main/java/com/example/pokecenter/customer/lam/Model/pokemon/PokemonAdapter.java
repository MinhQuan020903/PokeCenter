package com.example.pokecenter.customer.lam.Model.pokemon;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Interface.PokemonRecyclerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class PokemonAdapter extends  RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>{

    private Context mContext;
    private ArrayList<Pokemon> mPokemons = new ArrayList<>();

    private final PokemonRecyclerViewInterface pokemonRecyclerViewInterface;

    public PokemonAdapter(Context context, PokemonRecyclerViewInterface pokemonRecyclerViewInterface) {
        this.mContext  = context;
        this.pokemonRecyclerViewInterface = pokemonRecyclerViewInterface;
    }

    public void setData(ArrayList<Pokemon> list) {
        this.mPokemons = list;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Pokemon> list) {
        this.mPokemons.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mPokemons.clear();
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
            notifyItemChanged(position);
    }

    public Pokemon get(int position) {
        return mPokemons.get(position);
    }

    public int find(String name) {
        for (int i = 0; i < mPokemons.size(); ++i) {
            if (mPokemons.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_card_view_pokemon_api, parent, false);
        return new PokemonViewHolder(view, pokemonRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = mPokemons.get(position);
        if (pokemon == null) {
            return;
        }
        if (pokemon.getImageUrl() != "") {

            holder.progress_bar.setVisibility(View.INVISIBLE);
            Picasso.get().load(pokemon.getImageUrl()).into(holder.pokeImage);
            holder.pokeName.setText(pokemon.getName());
            holder.pokeLayoutCard.setBackgroundColor(Color.parseColor(PokemonBackgroundColor.of(pokemon.getType())));
        }
    }

    @Override
    public int getItemCount() {
        return mPokemons.size();
    }



    public class PokemonViewHolder  extends RecyclerView.ViewHolder {

        private ImageView pokeImage;
        private TextView pokeName;
        private LinearLayout pokeLayoutCard;
        private ProgressBar progress_bar;

        public PokemonViewHolder(@NonNull View itemView, PokemonRecyclerViewInterface pokemonRecyclerViewInterface) {
            super(itemView);

            pokeImage = itemView.findViewById(R.id.poke_image);
            pokeName = itemView.findViewById(R.id.poke_name );
            pokeLayoutCard = itemView.findViewById(R.id.poke_layout_card);
            progress_bar = itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(view -> {
                if (pokemonRecyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                         pokemonRecyclerViewInterface.onPokemonCardClick(mPokemons.get(pos));
                    }
                }
            });
        }
    }


}

