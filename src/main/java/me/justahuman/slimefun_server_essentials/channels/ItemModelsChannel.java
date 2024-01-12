package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ItemModelsChannel extends AbstractChannel {
    @Override
    public String getChannel() {
        return "slimefun_server_essentials:item_models";
    }

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            final String id = slimefunItem.getId();
            final int model = Slimefun.getItemTextureService().getModelData(id);
            if (model == 0) {
                continue;
            }

            final ByteArrayDataOutput packet = ByteStreams.newDataOutput();
            packet.writeInt(model);
            packet.writeUTF(id);
            sendMessage(player, packet);
        }
    }
}
