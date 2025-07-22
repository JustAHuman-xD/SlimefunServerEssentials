package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockPlaceEvent;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlockChannel extends AbstractChannel {
    private static final Map<ChunkPosition, Set<Location>> CHUNK_CACHE = new HashMap<>();

    public static void cacheBlockStorage() {
        for (World world : Bukkit.getWorlds()) {
            BlockStorage blockStorage = BlockStorage.getStorage(world);
            if (blockStorage == null) {
                continue;
            }

            Map<Location, Config> rawStorage = blockStorage.getRawStorage();
            for (Map.Entry<Location, Config> entry : rawStorage.entrySet()) {
                Location location = entry.getKey();
                Config config = entry.getValue();
                if (!config.contains("id")) {
                    continue;
                }

                ChunkPosition chunkPosition = new ChunkPosition(location);
                Set<Location> locations = CHUNK_CACHE.getOrDefault(chunkPosition, new HashSet<>());
                locations.add(location);
                CHUNK_CACHE.put(chunkPosition, locations);
            }
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:block";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Player player) {
                final Set<Location> locations = CHUNK_CACHE.get(new ChunkPosition(chunk));
                if (locations == null) {
                    continue;
                }

                for (Location location : locations) {
                    final String id = BlockStorage.checkID(location);
                    if (id != null) {
                        sendSlimefunBlock(player, location, id);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onSlimefunBlockPlace(SlimefunBlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        final Location location = block.getLocation();
        final ChunkPosition chunkPosition = new ChunkPosition(location);
        final Set<Location> locations = CHUNK_CACHE.getOrDefault(chunkPosition, new HashSet<>());
        locations.add(location);
        CHUNK_CACHE.put(chunkPosition, locations);

        final String id = event.getSlimefunItem().getId();
        for (UUID uuid : players) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && block.getWorld() == player.getWorld() && player.getLocation().distanceSquared(block.getLocation()) <= Math.pow(player.getClientViewDistance() * 16D, 2)) {
                sendSlimefunBlock(player, location, id);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onSlimefunBlockBreak(SlimefunBlockBreakEvent event) {
        final Block block = event.getBlockBroken();
        final Location location = block.getLocation();
        final ChunkPosition chunkPosition = new ChunkPosition(location);
        final Set<Location> locations = CHUNK_CACHE.getOrDefault(chunkPosition, new HashSet<>());
        locations.remove(location);
        CHUNK_CACHE.put(chunkPosition, locations);

        for (UUID uuid : players) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && block.getWorld() == player.getWorld() && player.getLocation().distanceSquared(block.getLocation()) <= Math.pow(player.getClientViewDistance() * 16D, 2)) {
                sendSlimefunBlock(player, location, " ");
            }
        }
    }

    @ParametersAreNonnullByDefault
    public void sendSlimefunBlock(Player player, Location location, String id) {
        final ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeInt(location.getBlockX());
        packet.writeInt(location.getBlockY());
        packet.writeInt(location.getBlockZ());
        packet.writeUTF(id);
        sendMessage(player, packet);
    }
}
