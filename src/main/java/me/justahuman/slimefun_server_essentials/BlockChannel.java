package me.justahuman.slimefun_server_essentials;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.ChunkPosition;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlockChannel implements PluginMessageListener, Listener {
    private static final Map<ChunkPosition, Set<BlockPosition>> cachedSlimefunBlocks = new HashMap<>();
    private static final Set<UUID> players = new HashSet<>();
    public static final String channel = "slimefun_server_essentials:block";

    public BlockChannel(@Nonnull SlimefunServerEssentials slimefunServerEssentials) {
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

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] message) {
        Bukkit.getScheduler().runTaskAsynchronously(SlimefunServerEssentials.getInstance(), () -> {
            if (!channel.equals(BlockChannel.channel) || BlockStorage.getStorage(player.getWorld()) == null) {
                return;
            }

            final World world = player.getWorld();
            final BlockStorage blockStorage = BlockStorage.getStorage(world);
            final ByteArrayDataInput packet = ByteStreams.newDataInput(message);
            final int chunkX = packet.readInt();
            final int chunkZ = packet.readInt();
            final ChunkPosition chunkPosition = new ChunkPosition(world, chunkX, chunkZ);

            if (cachedSlimefunBlocks.containsKey(chunkPosition)) {
                for (BlockPosition blockPosition : cachedSlimefunBlocks.get(chunkPosition)) {
                    sendSlimefunBlock(player, blockPosition, BlockStorage.getLocationInfo(blockPosition.toLocation(), "id"));
                }
                return;
            }

            final Map<Location, Config> rawStorage = blockStorage.getRawStorage();
            final Set<BlockPosition> blockPositions = new HashSet<>();
            final Map<BlockPosition, String> ids = new HashMap<>();

            for (Map.Entry<Location, Config> entry : rawStorage.entrySet()) {
                final Location location = entry.getKey();
                final Config config = entry.getValue();
                if (location.getBlockX() >> 4 != chunkX || location.getBlockZ() >> 4 != chunkZ || !config.contains("id")) {
                    continue;
                }

                final BlockPosition blockPosition = new BlockPosition(location);
                blockPositions.add(blockPosition);
                ids.put(blockPosition, config.getString("id"));
            }

            for (Map.Entry<BlockPosition, String> entry : ids.entrySet()) {
                sendSlimefunBlock(player, entry.getKey(), entry.getValue());
            }

            cachedSlimefunBlocks.put(chunkPosition, blockPositions);
        });
    }
//
//    // Slimefun Block Place Event
//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    private void onSlimefunBlockPlace(SlimefunBlockPlaceEvent event) {
//        final String id = event.getSlimefunItem().getId();
//        for (UUID uuid : players) {
//            final Player player = Bukkit.getPlayer(uuid);
//            if (player != null) {
//                final BlockPosition blockPosition = new BlockPosition(event.getBlockPlaced());
//                cachedSlimefunBlocks.merge(new ChunkPosition(player.getLocation()), new HashSet<>(), (s1, s2) -> {
//                    s1.addAll(s2);
//                    return s1;
//                });
//
//                sendSlimefunBlock(player, blockPosition, id);
//            }
//        }
//    }
//
//    // Slimefun Block Break Event
//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    private void onSlimefunBlockBreak(SlimefunBlockBreakEvent event) {
//        final Block block = event.getBlock();
//        for (UUID uuid : players) {
//            final Player player = Bukkit.getPlayer(uuid);
//            if (player != null && block.getWorld() == player.getWorld() && player.getLocation().distanceSquared(block.getLocation()) <= 64) {
//                final BlockPosition blockPosition = new BlockPosition(event.getBlockBroken());
//                cachedSlimefunBlocks.merge(new ChunkPosition(player.getLocation()), new HashSet<>(), (s1, s2) -> {
//                    s1.addAll(s2);
//                    return s1;
//                });
//
//                sendSlimefunBlock(player, blockPosition, " ");
//            }
//        }
//    }

    @ParametersAreNonnullByDefault
    public static void sendSlimefunBlock(Player player, BlockPosition blockPosition, String id) {
        final ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeInt(blockPosition.getX());
        packet.writeInt(blockPosition.getY());
        packet.writeInt(blockPosition.getZ());
        packet.writeUTF(id);
        player.sendPluginMessage(SlimefunServerEssentials.getInstance(), channel, packet.toByteArray());
    }
}
