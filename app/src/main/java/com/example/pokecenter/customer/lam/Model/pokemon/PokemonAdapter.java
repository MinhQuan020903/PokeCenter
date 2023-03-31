package com.example.pokecenter.customer.lam.Model.pokemon;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonAdapter extends  RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>{

    private Context mContext;
    private ArrayList<Pokemon> mPokemons;

    public PokemonAdapter(Context context) {
        this.mContext  = context;
    }

    public void setData(ArrayList<Pokemon> list) {
        this.mPokemons = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_poke_card_view, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = mPokemons.get(position);
        if (pokemon == null) {
            return;
        }
        Picasso.get().load(pokemon.getImageUrl()).into(holder.pokeImage);
        holder.pokeName.setText(pokemon.getName());
        holder.pokeLayoutCard.setBackgroundColor(Color.parseColor(BackgroundColor.of(pokemon.getType())));
    }

    @Override
    public int getItemCount() {
        if (mPokemons != null) {
            return mPokemons.size();
         }
        return 0;
    }

    public class PokemonViewHolder  extends RecyclerView.ViewHolder {

        private ImageView pokeImage;
        private TextView pokeName;
        private LinearLayout pokeLayoutCard;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);

            pokeImage = itemView.findViewById(R.id.poke_image);
            pokeName = itemView.findViewById(R.id.poke_name );
            pokeLayoutCard = itemView.findViewById(R.id.poke_layout_card);
        }
    }

    public static class BackgroundColor {
        public static String of(String type) {
            Map<String, String> colors = new HashMap<>();

            colors.put("fire", "#FDDFDF");
            colors.put("grass", "#DEFDE0");
            colors.put("electric", "#FCF7DE");
            colors.put("water", "#DEF3FD");
            colors.put("ground", "#F4E7DA");
            colors.put("rock", "#D5D5D4");
            colors.put("fairy", "#FCEAFF");
            colors.put("poison", "#BF80B2");
            colors.put("bug", "#F8D5A3");
            colors.put("dragon", "#97B3E6");
            colors.put("psychic", "#EAEDA1");
            colors.put("flying", "#F5F5F5");
            colors.put("fighting", "#E6E0D4");
            colors.put("steel", "#DCDCE3");
            colors.put("ice", "#BFEAFF");
            colors.put("normal", "#F5F5F5");
            colors.put("dark", "#966A54");
            colors.put("ghost", "#9898D1");
            return colors.get(type);
        }
    }
}

