package com.example.pokecenter.customer.lam.Model.pokemon;

import java.util.HashMap;
import java.util.Map;

public class PokemonBackgroundColor {
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