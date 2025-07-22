package me.justahuman.slimefun_server_essentials;

import de.tr7zw.nbtapi.NBT;
import lombok.Getter;
import me.justahuman.slimefun_server_essentials.channels.registry.ItemsChannel;
import me.justahuman.slimefun_server_essentials.channels.LoadingStateChannel;
import me.justahuman.slimefun_server_essentials.channels.registry.RecipeCategoriesChannel;
import me.justahuman.slimefun_server_essentials.channels.registry.RecipeDisplaysChannel;
import me.justahuman.slimefun_server_essentials.channels.registry.ComponentTypesChannel;
import me.justahuman.slimefun_server_essentials.listeners.RegisterDefaultsListener;
import me.justahuman.slimefun_server_essentials.listeners.RegistryFinalizedListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlimefunServerEssentials extends JavaPlugin {
    private static @Getter SlimefunServerEssentials instance;

    private static @Getter LoadingStateChannel loadingStateChannel;

    private static @Getter ItemsChannel itemsChannel;
    private static @Getter RecipeCategoriesChannel recipeCategoriesChannel;
    private static @Getter RecipeDisplaysChannel recipeDisplaysChannel;
    private static @Getter ComponentTypesChannel componentTypesChannel;

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        instance = this;

        loadingStateChannel = new LoadingStateChannel();

        itemsChannel = new ItemsChannel();
        recipeDisplaysChannel = new RecipeDisplaysChannel();
        recipeCategoriesChannel = new RecipeCategoriesChannel();
        componentTypesChannel = new ComponentTypesChannel();

        new Metrics(instance, 18206);

        getServer().getPluginManager().registerEvents(new RegistryFinalizedListener(), this);
        getServer().getPluginManager().registerEvents(new RegisterDefaultsListener(), this);

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
