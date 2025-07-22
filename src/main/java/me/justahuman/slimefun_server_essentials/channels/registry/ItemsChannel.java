package me.justahuman.slimefun_server_essentials.channels.registry;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.justahuman.slimefun_server_essentials.channels.AbstractChannel;
import me.justahuman.slimefun_server_essentials.util.DataUtils;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemsChannel extends AbstractChannel {
    @Override
    public void load() {
        if (!messages.isEmpty()) {
            return;
        }


        for (Map.Entry<SlimefunAddon, List<SlimefunItem>> entry : Utils.getSortedAddonRegistry().entrySet()) {
            String addonId = entry.getKey().getName().toLowerCase(Locale.ROOT).replace(" ", "_");
            JsonObject items = new JsonObject();
            for (SlimefunItem item : entry.getValue()) {
                items.add(item.getId(), JsonUtils.serializeItem(item));
            }
            JsonUtils.generated("items/" + addonId, items);
        }

        for (Map.Entry<SlimefunAddon, List<SlimefunItem>> entry : Utils.getSortedAddonRegistry().entrySet()) {
            List<SlimefunItem> items = entry.getValue();
            ByteArrayDataOutput itemsPacket = ByteStreams.newDataOutput();
            itemsPacket.writeInt(items.size());
            for (SlimefunItem item : items) {
                itemsPacket.writeUTF(item.getId());
                itemsPacket.write(DataUtils.bytes(item.getItem()));
            }
            messages.addAll(splitMessage(itemsPacket.toByteArray()));
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:items";
    }
}
