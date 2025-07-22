package me.justahuman.slimefun_server_essentials.implementation;

import me.justahuman.slimefun_server_essentials.api.display.AbstractDisplayBuilder;

import java.util.HashMap;
import java.util.Map;

public class RecipeDisplays {
    private static final Map<String, AbstractDisplayBuilder<?>> RECIPE_DISPLAYS = new HashMap<>();

    public static void register(String type, AbstractDisplayBuilder<?> display) {
        // TODO: How to handle types with existing displays?
        RECIPE_DISPLAYS.put(type, display);
    }

    public static void clear() {
        RECIPE_DISPLAYS.clear();
    }

    public static Map<String, AbstractDisplayBuilder<?>> getRecipeDisplays() {
        return RECIPE_DISPLAYS;
    }
}
