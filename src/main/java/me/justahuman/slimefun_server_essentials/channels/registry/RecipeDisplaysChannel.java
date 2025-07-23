package me.justahuman.slimefun_server_essentials.channels.registry;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_server_essentials.api.display.AbstractDisplayBuilder;
import me.justahuman.slimefun_server_essentials.channels.AbstractChannel;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;
import me.justahuman.slimefun_server_essentials.util.DataUtils;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;

import java.util.Map;

public class RecipeDisplaysChannel extends AbstractChannel {
    @Override
    public void load() {
        if (!messages.isEmpty()) {
            return;
        }

        for (Map.Entry<String, AbstractDisplayBuilder<?>> entry : RecipeDisplays.getRecipeDisplays().entrySet()) {
            JsonUtils.generated("recipe_display/" + format(entry.getKey()), entry.getValue().toJson());
        }

        for (Map.Entry<String, AbstractDisplayBuilder<?>> entry : RecipeDisplays.getRecipeDisplays().entrySet()) {
            ByteArrayDataOutput displayPacket = ByteStreams.newDataOutput();
            DataUtils.writeVersion(displayPacket);
            displayPacket.writeUTF(entry.getKey());
            entry.getValue().toBytes(displayPacket);
            messages.addAll(splitMessage(displayPacket.toByteArray()));
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:recipe_displays";
    }
}
