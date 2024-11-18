package me.justahuman.slimefun_server_essentials.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RegistryFinalizedListener implements Listener {
    @EventHandler
    public void onRegistryFinalized(SlimefunItemRegistryFinalizedEvent event) {
        Utils.load();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SlimefunServerEssentials.getRecipeDisplaysChannel().onRegisterConnection(player);
    }
}
