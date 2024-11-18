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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractChannel implements PluginMessageListener, Listener {
    protected static final int MAX_MESSAGE_SIZE = 32766;
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
        sendMessage(player, dataOutput.toByteArray());
    }

    public void sendMessage(@Nonnull Player player, @Nonnull ByteArrayDataOutput message) {
        sendMessage(player, message.toByteArray());
    }

    public void sendMessage(@Nonnull Player player, @Nonnull byte[] message) {
        player.sendPluginMessage(SlimefunServerEssentials.getInstance(), getChannel(), message);
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

    public List<byte[]> splitMessage(byte[] data) {
        byte[] newData = new byte[data.length + 4];
        System.arraycopy(data, 0, newData, 4, data.length);

        int pieces = (int) Math.ceil(newData.length / (double) MAX_MESSAGE_SIZE);
        newData[0] = (byte) (pieces >> 24);
        newData[1] = (byte) (pieces >> 16);
        newData[2] = (byte) (pieces >> 8);
        newData[3] = (byte) pieces;

        List<byte[]> split = new ArrayList<>();
        for (int i = 0; i < pieces; i++) {
            int start = i * MAX_MESSAGE_SIZE;
            int end = Math.min(newData.length, (i + 1) * MAX_MESSAGE_SIZE);
            split.add(Arrays.copyOfRange(newData, start, end));
        }
        return split;
    }
}
