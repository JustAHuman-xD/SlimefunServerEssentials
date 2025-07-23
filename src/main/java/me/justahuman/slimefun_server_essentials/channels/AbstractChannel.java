package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractChannel implements PluginMessageListener, Listener {
    protected static final int MAX_MESSAGE_SIZE = 32766;
    protected static final int SPLIT_MESSAGE_SIZE = MAX_MESSAGE_SIZE - 4 - 4 - 4;
    protected static int messageId = 0;
    protected final List<byte[]> messages = new ArrayList<>();
    protected final Set<UUID> players = new HashSet<>();
    protected final Map<UUID, Runnable> onRegisterChannel = new HashMap<>();

    protected AbstractChannel() {
        final Plugin plugin = SlimefunServerEssentials.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, getChannel(), this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, getChannel());
    }

    protected String format(String string) {
        return string.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    public abstract String getChannel();
    public void load() {}
    public void scheduleMessages(@Nonnull Player player) {
        onRegisterChannel.put(player.getUniqueId(), () -> {
            for (byte[] message : messages) {
                sendMessage(player, message);
            }
        });

        if (players.contains(player.getUniqueId())) {
            onRegisterChannel.remove(player.getUniqueId()).run();
        }
    }
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
        Runnable runnable = onRegisterChannel.remove(event.getPlayer().getUniqueId());
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(getChannel())) {
            return;
        }

        onMessageReceived(player, message);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        players.remove(playerId);
        onRegisterChannel.remove(playerId);
    }

    public List<byte[]> splitMessage(byte[] data) {
        int pieces = (int) Math.ceil(data.length / (double) SPLIT_MESSAGE_SIZE);
        byte[] messageIdBytes = ByteBuffer.allocate(4).putInt(messageId++).array();
        byte[] piecesBytes = ByteBuffer.allocate(4).putInt(pieces).array();

        List<byte[]> split = new ArrayList<>();
        for (int i = 0; i < pieces; i++) {
            byte[] indexBytes = ByteBuffer.allocate(4).putInt(i).array();
            byte[] bytes = new byte[MAX_MESSAGE_SIZE];
            int start = i * SPLIT_MESSAGE_SIZE;
            int end = Math.min(data.length, (i + 1) * SPLIT_MESSAGE_SIZE);
            System.arraycopy(messageIdBytes, 0, bytes, 0, 4);
            System.arraycopy(piecesBytes, 0, bytes, 4, 4);
            System.arraycopy(indexBytes, 0, bytes, 8, 4);
            System.arraycopy(data, start, bytes, 12, end - start);
            split.add(bytes);
        }
        return split;
    }
}
