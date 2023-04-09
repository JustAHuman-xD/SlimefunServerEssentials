package me.justahuman.recipe_exporter;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlockChannel implements PluginMessageListener, Listener {
    private static final Set<UUID> players = new HashSet<>();
    public static final String channel = "slimefun_server_essentials:block";

    public void init(@Nonnull SlimefunServerEssentials slimefunServerEssentials) {
        slimefunServerEssentials.getServer().getPluginManager().registerEvents(this, slimefunServerEssentials);
        slimefunServerEssentials.getServer().getMessenger().registerIncomingPluginChannel(slimefunServerEssentials, channel, this);
        slimefunServerEssentials.getServer().getMessenger().registerOutgoingPluginChannel(slimefunServerEssentials, channel);
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals(channel)) {
            return;
        }

        players.add(event.getPlayer().getUniqueId());
    }

    @ParametersAreNonnullByDefault
    public static void sendSlimefunBlock(Player player, BlockPosition blockPosition, String id) {
        final ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeInt(blockPosition.getX());
        packet.writeInt(blockPosition.getY());
        packet.writeInt(blockPosition.getZ());
        packet.writeUTF(id);
        player.sendPluginMessage(SlimefunServerEssentials.getInstance(), channel, packet.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        Bukkit.getScheduler().runTaskAsynchronously(SlimefunServerEssentials.getInstance(), () -> {
            if (!channel.equals(BlockChannel.channel) || BlockStorage.getStorage(player.getWorld()) == null) {
                return;
            }

            final World world = player.getWorld();
            final BlockStorage blockStorage = BlockStorage.getStorage(world);
            final Map<Location, Config> rawStorage = blockStorage.getRawStorage();
            final Map<Location, String> ids = new HashMap<>();
            final ByteArrayDataInput packet = ByteStreams.newDataInput(message);
            final int chunkX = packet.readInt();
            final int chunkZ = packet.readInt();

            for (Map.Entry<Location, Config> entry : rawStorage.entrySet()) {
                final Location location = entry.getKey();
                final Config config = entry.getValue();
                if (location.getBlockX() >> 4 != chunkX || location.getBlockZ() >> 4 != chunkZ || !config.contains("id")) {
                    continue;
                }

                ids.put(location, config.getString("id"));
            }

            for (Map.Entry<Location, String> entry : ids.entrySet()) {
                sendSlimefunBlock(player, new BlockPosition(entry.getKey()), entry.getValue());
            }
        });
    }

    // Slimefun Block Place Event
    private void onSlimefunBlockPlace(SlimefunItem slimefunItem, Block block) {
        final String id = slimefunItem.getId();
        for (UUID uuid : players) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                sendSlimefunBlock(player, new BlockPosition(block), id);
            }
        }
    }

    // Slimefun Block Break Event
    private void onSlimefunBlockBreak(Block block) {
        for (UUID uuid : players) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && block.getWorld() == player.getWorld() && player.getLocation().distanceSquared(block.getLocation()) <= 64) {
                sendSlimefunBlock(player, new BlockPosition(block), " ");
            }
        }
    }
}
