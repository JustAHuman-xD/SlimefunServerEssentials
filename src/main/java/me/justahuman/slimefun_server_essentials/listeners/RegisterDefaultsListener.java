package me.justahuman.slimefun_server_essentials.listeners;

import me.justahuman.slimefun_server_essentials.api.event.SlimefunEssentialsRegisterEvent;
import me.justahuman.slimefun_server_essentials.implementation.core.DefaultCategories;
import me.justahuman.slimefun_server_essentials.implementation.core.DefaultDisplays;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static me.justahuman.slimefun_server_essentials.implementation.core.DefaultComponentTypes.*;

public class RegisterDefaultsListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegister(SlimefunEssentialsRegisterEvent event) {
        event.registerComponentTypes(ENERGY, SLOT, LARGE_SLOT, ARROW_RIGHT, ARROW_LEFT, FILLING_ARROW_RIGHT, FILLING_ARROW_LEFT, REQUIRES_DAY, REQUIRES_NIGHT);
        for (DefaultDisplays display : DefaultDisplays.values()) {
            event.registerRecipeDisplay(display.id(), display.display());
        }
        DefaultCategories.register(event);
    }
}
