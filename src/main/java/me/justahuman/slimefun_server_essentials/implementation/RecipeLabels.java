package me.justahuman.slimefun_server_essentials.implementation;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class RecipeLabels {
    private static final Map<String, JsonObject> RECIPE_LABELS = new HashMap<>();

    public static void register(String id, JsonObject display) {
        // TODO: How to handle ids with existing labels?
        RECIPE_LABELS.put(id, display);
    }

    public static Map<String, JsonObject> getRecipeLabels() {
        return RECIPE_LABELS;
    }
}
