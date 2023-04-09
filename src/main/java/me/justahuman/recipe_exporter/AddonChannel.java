package me.justahuman.recipe_exporter;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import javax.annotation.Nonnull;

public class AddonChannel implements Listener {
    public static final String channel = "slimefun_server_essentials:addon";

    public void init(@Nonnull SlimefunServerEssentials slimefunServerEssentials) {
        slimefunServerEssentials.getServer().getPluginManager().registerEvents(this, slimefunServerEssentials);
        slimefunServerEssentials.getServer().getMessenger().registerOutgoingPluginChannel(slimefunServerEssentials, channel);
    }

    @EventHandler
    private void onPlayerConnect(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals(channel)) {
            return;
        }

        final Player player = event.getPlayer();
        final ByteArrayDataOutput clear = ByteStreams.newDataOutput();
        clear.writeUTF("clear");
        player.sendPluginMessage(SlimefunServerEssentials.getInstance(), channel, clear.toByteArray());
        for (String slimefunAddon : Utils.getSlimefunAddonNames()) {
            final ByteArrayDataOutput addon = ByteStreams.newDataOutput();
            addon.writeUTF(slimefunAddon);
            player.sendPluginMessage(SlimefunServerEssentials.getInstance(), channel, addon.toByteArray());
        }
    }
}
