package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeDisplaysChannel extends AbstractChannel {
    private static final List<byte[]> MESSAGES = new ArrayList<>();

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        if (MESSAGES.isEmpty()) {
            for (Map.Entry<String, JsonObject> entry : RecipeDisplays.getRecipeDisplays().entrySet()) {
                ByteArrayDataOutput displayPacket = ByteStreams.newDataOutput();
                displayPacket.writeUTF(entry.getKey());
                displayPacket.writeUTF(entry.getValue().toString());
                MESSAGES.addAll(splitMessage(displayPacket.toByteArray()));
            }
        }

        for (byte[] message : MESSAGES) {
            sendMessage(player, message);
        }

        for (int i = 0; i < MESSAGES.size(); i++) {
            byte[] message = MESSAGES.get(i);
            int pieces = message[0] << 24 | message[1] << 16 | message[2] << 8 | message[3];
            byte[] newMessage = new byte[message.length - 4];
            System.arraycopy(message, 4, newMessage, 0, message.length - 4);
            message = newMessage;

            if (pieces > 1) {
                int j;
                for (j = 1; j < pieces; j++) {
                    byte[] nextMessage = MESSAGES.get(i + j);
                    newMessage = new byte[message.length + nextMessage.length];
                    System.arraycopy(message, 0, newMessage, 0, message.length);
                    System.arraycopy(nextMessage, 0, newMessage, message.length, nextMessage.length);
                    message = newMessage;
                }
                i += j - 1;
            }

            ByteArrayDataInput input = ByteStreams.newDataInput(message);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:recipe_displays";
    }
}
