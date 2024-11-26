package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class RecipeDisplaysChannel extends AbstractChannel {
    @Override
    public void load() {
        if (messages.isEmpty()) {
            for (Map.Entry<String, JsonObject> entry : RecipeDisplays.getRecipeDisplays().entrySet()) {
                ByteArrayDataOutput displayPacket = ByteStreams.newDataOutput();
                displayPacket.writeUTF(entry.getKey());
                displayPacket.writeUTF(entry.getValue().toString());
                messages.addAll(splitMessage(displayPacket.toByteArray()));
            }
        }
    }

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        for (byte[] message : messages) {
            sendMessage(player, message);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:recipe_displays";
    }
}
