package me.justahuman.slimefun_server_essentials.recipe.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.justahuman.slimefun_server_essentials.recipe.RecipeBuilder;
import me.justahuman.slimefun_server_essentials.recipe.RecipeExporter;
import me.justahuman.slimefun_server_essentials.util.Hooks;

import java.util.ArrayList;
import java.util.List;

public abstract class PluginHook {
    protected PluginHook() {
        Hooks.HOOKS.add(this);
    }

    public List<String> getSpecialCases() {
        return new ArrayList<>();
    }

    public abstract boolean handles(SlimefunItem slimefunItem);
    public abstract void handle(JsonObject categoryObject, JsonArray recipesArray, SlimefunItem slimefunItem);

    public void add(JsonArray recipesArray, RecipeBuilder builder) {
        RecipeExporter.addRecipeWithOptimize(recipesArray, builder);
    }

    public abstract String getHookName();
}
