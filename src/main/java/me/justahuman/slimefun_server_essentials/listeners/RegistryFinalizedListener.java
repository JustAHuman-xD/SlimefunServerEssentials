package me.justahuman.slimefun_server_essentials.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegistryFinalizedListener implements Listener {
    @EventHandler
    public void onRegistryFinalized(SlimefunItemRegistryFinalizedEvent event) {
        Utils.load();
        SlimefunServerEssentials.getComponentTypesChannel().load();
        SlimefunServerEssentials.getItemsChannel().load();
        SlimefunServerEssentials.getRecipeCategoriesChannel().load();
        SlimefunServerEssentials.getRecipeDisplaysChannel().load();
    }
}
