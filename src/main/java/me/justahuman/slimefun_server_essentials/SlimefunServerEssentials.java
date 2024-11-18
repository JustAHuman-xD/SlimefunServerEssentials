package me.justahuman.slimefun_server_essentials;

import de.tr7zw.nbtapi.NBT;
import lombok.Getter;
import me.justahuman.slimefun_server_essentials.channels.RecipeDisplaysChannel;
import me.justahuman.slimefun_server_essentials.implementation.core.DefaultCategories;
import me.justahuman.slimefun_server_essentials.implementation.core.DefaultDisplays;
import me.justahuman.slimefun_server_essentials.listeners.RegistryFinalizedListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlimefunServerEssentials extends JavaPlugin {
    private static @Getter SlimefunServerEssentials instance;
    private static @Getter RecipeDisplaysChannel recipeDisplaysChannel;

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        instance = this;
        recipeDisplaysChannel = new RecipeDisplaysChannel();
        new Metrics(instance, 18206);
        DefaultCategories.register();
        DefaultDisplays.register();
        getServer().getPluginManager().registerEvents(new RegistryFinalizedListener(), this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
