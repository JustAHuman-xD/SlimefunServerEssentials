package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

public class LoadingStateChannel extends AbstractChannel {
    @EventHandler @Override
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals(getChannel())) {
            return;
        }

        Player player = event.getPlayer();
        ByteArrayDataOutput loadingStatePacket = ByteStreams.newDataOutput();
        loadingStatePacket.writeInt(SlimefunServerEssentials.getComponentTypesChannel().messages.size());
        loadingStatePacket.writeInt(SlimefunServerEssentials.getItemsChannel().messages.size());
        loadingStatePacket.writeInt(SlimefunServerEssentials.getRecipeCategoriesChannel().messages.size());
        loadingStatePacket.writeInt(SlimefunServerEssentials.getRecipeDisplaysChannel().messages.size());
        sendMessage(player, loadingStatePacket);

        SlimefunServerEssentials.getComponentTypesChannel().onRegisterConnection(player);
        SlimefunServerEssentials.getItemsChannel().onRegisterConnection(player);
        SlimefunServerEssentials.getRecipeCategoriesChannel().onRegisterConnection(player);
        SlimefunServerEssentials.getRecipeDisplaysChannel().onRegisterConnection(player);
        players.add(player.getUniqueId());
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:loading_state";
    }
}
