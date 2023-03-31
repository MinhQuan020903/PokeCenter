package com.example.pokecenter.customer.lam.API;

import android.os.AsyncTask;

import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PokeAPIFetcher {
    private static final String TAG = "PokemonFetcher";

    public interface OnFetchCompleteListener {
        void onFetchComplete(ArrayList<Pokemon> pokemons);
    }

    public static void fetchRandomTenPokemon(int count, OnFetchCompleteListener listener) {
        new AsyncTask<Void, Void, ArrayList<Pokemon>>() {
            @Override
            protected ArrayList<Pokemon> doInBackground(Void... voids) {
                ArrayList<Pokemon> pokemons = new ArrayList<>();
                try {
                    for (int i = 1; i <= count; ++i) {

                        int randomPokemonId = (int) (Math.random() * 900) + 1;
                        URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + randomPokemonId);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.connect();

                        int responseCode = conn.getResponseCode();
                        if (responseCode != 200) {
                            throw new RuntimeException("HTTP response code: " + responseCode);
                        }

                        String inline = "";
                        Scanner scanner = new Scanner(url.openStream());
                        while (scanner.hasNext()) {
                            inline += scanner.nextLine();
                        }
                        scanner.close();

                        JSONObject data = new JSONObject(inline);
                        String name = data.getString("name");

                        String normalizeName = name.substring(0, 1).toUpperCase() + name.substring(1);

                        String imageUrl = data.getJSONObject("sprites")
                                .getJSONObject("other")
                                .getJSONObject("official-artwork")
                                .getString("front_default");

                        JSONArray types = data.getJSONArray("types");
                        String type = types.getJSONObject(0).getJSONObject("type").getString("name");

                        pokemons.add(new Pokemon(normalizeName, imageUrl, type));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return pokemons;
            }

            @Override
            protected void onPostExecute(ArrayList<Pokemon> pokemons) {
                listener.onFetchComplete(pokemons);
            }
        }.execute();
    }
}