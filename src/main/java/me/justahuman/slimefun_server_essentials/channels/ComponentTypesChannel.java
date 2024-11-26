package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.implementation.DisplayComponentTypes;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComponentTypesChannel extends AbstractChannel {
    private static final List<byte[]> MESSAGES = new ArrayList<>();

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        if (MESSAGES.isEmpty()) {
            for (Map.Entry<String, JsonObject> entry : DisplayComponentTypes.getComponentTypes().entrySet()) {
                ByteArrayDataOutput displayPacket = ByteStreams.newDataOutput();
                displayPacket.writeUTF(entry.getKey());
                displayPacket.writeUTF(entry.getValue().toString());
                MESSAGES.addAll(splitMessage(displayPacket.toByteArray()));
            }
        }

        for (byte[] message : MESSAGES) {
            sendMessage(player, message);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:component_types";
    }
}
