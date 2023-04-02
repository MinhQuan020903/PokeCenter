package com.example.pokecenter.customer.lam.Model.pokemon;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Interface.RecyclerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonAdapter extends  RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>{

    private Context mContext;
    private ArrayList<Pokemon> mPokemons = new ArrayList<>();

    private final RecyclerViewInterface recyclerViewInterface;

    public PokemonAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.mContext  = context;
        this.recyclerViewInterface = recyclerViewInterface;
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

    public Pokemon getItem(int position) {
        return mPokemons.get(position);
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_poke_card_view, parent, false);
        return new PokemonViewHolder(view, recyclerViewInterface);
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



    public static class PokemonViewHolder  extends RecyclerView.ViewHolder {

        private ImageView pokeImage;
        private TextView pokeName;
        private LinearLayout pokeLayoutCard;
        private ProgressBar progress_bar;

        public PokemonViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface ) {
            super(itemView);

            pokeImage = itemView.findViewById(R.id.poke_image);
            pokeName = itemView.findViewById(R.id.poke_name );
            pokeLayoutCard = itemView.findViewById(R.id.poke_layout_card);
            progress_bar = itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                         recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }


}

