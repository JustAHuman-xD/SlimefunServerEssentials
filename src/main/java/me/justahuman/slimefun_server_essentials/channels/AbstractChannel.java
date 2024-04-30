package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractChannel implements PluginMessageListener, Listener {
    protected final Set<UUID> players = new HashSet<>();

    protected AbstractChannel() {
        final Plugin plugin = SlimefunServerEssentials.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, getChannel(), this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, getChannel());
    }

    public abstract String getChannel();
    public void onRegisterConnection(@Nonnull Player player) {}
    public void onMessageReceived(@Nonnull Player player, @Nonnull byte[] message) {}

    public void sendMessage(@Nonnull Player player, @Nonnull String message) {
        final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF(message);
        sendMessage(player, dataOutput);
    }

    public void sendMessage(@Nonnull Player player, @Nonnull ByteArrayDataOutput message) {
        player.sendPluginMessage(SlimefunServerEssentials.getInstance(), getChannel(), message.toByteArray());
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals(getChannel())) {
            return;
        }

        players.add(event.getPlayer().getUniqueId());
        onRegisterConnection(event.getPlayer());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(getChannel())) {
            return;
        }

        onMessageReceived(player, message);
    }
}
