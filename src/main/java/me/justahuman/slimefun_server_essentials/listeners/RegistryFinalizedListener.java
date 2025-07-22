package me.justahuman.slimefun_server_essentials.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.api.event.SlimefunEssentialsRegisterEvent;
import me.justahuman.slimefun_server_essentials.implementation.DisplayComponentTypes;
import me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegistryFinalizedListener implements Listener {
    @EventHandler
    public void onRegistryFinalized(SlimefunItemRegistryFinalizedEvent event) {
        Utils.load();

        JsonUtils.deleteGenerated();
        DisplayComponentTypes.clear();
        RecipeCategoryExporters.clear();
        RecipeDisplays.clear();
        Bukkit.getPluginManager().callEvent(new SlimefunEssentialsRegisterEvent());

        SlimefunServerEssentials.getComponentTypesChannel().load();
        SlimefunServerEssentials.getItemsChannel().load();
        SlimefunServerEssentials.getRecipeCategoriesChannel().load();
        SlimefunServerEssentials.getRecipeDisplaysChannel().load();
    }
}
