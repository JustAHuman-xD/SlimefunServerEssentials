package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemsChannel extends AbstractChannel {
    private static final List<byte[]> MESSAGES = new ArrayList<>();

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        if (MESSAGES.isEmpty()) {
            for (Map.Entry<SlimefunAddon, List<SlimefunItem>> registryEntry : Utils.getSortedAddonRegistry().entrySet()) {
                List<SlimefunItem> items = registryEntry.getValue();
                ByteArrayDataOutput itemsPacket = ByteStreams.newDataOutput();
                itemsPacket.writeInt(items.size());
                for (SlimefunItem item : items) {
                    itemsPacket.writeUTF(item.getId());
                    itemsPacket.writeUTF(JsonUtils.serializeItem(item).toString());
                }
                MESSAGES.addAll(splitMessage(itemsPacket.toByteArray()));
            }
        }

        for (byte[] message : MESSAGES) {
            sendMessage(player, message);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:items";
    }
}
