package me.justahuman.slimefun_server_essentials.recipe.compat.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;

public class FnAmpHook extends PluginHook {
    @Override
    public boolean handles(SlimefunItem slimefunItem) {
        return false;
    }

    @Override
    public void handle(JsonObject categoryObject, JsonArray recipesArray, SlimefunItem slimefunItem) {

    }

    @Override
    public String getHookName() {
        return null;
    }
}
