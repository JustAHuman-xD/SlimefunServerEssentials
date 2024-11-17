package me.justahuman.slimefun_server_essentials.implementation;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class RecipeDisplays {
    private static final Map<String, JsonObject> RECIPE_DISPLAYS = new HashMap<>();

    public static void register(String type, JsonObject display) {
        // TODO: How to handle types with existing displays?
        RECIPE_DISPLAYS.put(type, display);
    }

    public static Map<String, JsonObject> getRecipeDisplays() {
        return RECIPE_DISPLAYS;
    }
}
