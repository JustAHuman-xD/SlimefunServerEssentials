package me.justahuman.slimefun_server_essentials.channels;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.entity.Player;
import java.util.List;
import javax.annotation.Nonnull;

public class ItemChannel extends AbstractChannel {
    @Override
    public String getChannel() {
        return "slimefun_server_essentials:item";
    }

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        final List<SlimefunItem> slimefunItems = Slimefun.getRegistry().getAllSlimefunItems();
        final List<SlimefunItem> enabledItems = Slimefun.getRegistry().getEnabledSlimefunItems();
        slimefunItems.removeAll(enabledItems);
        for (SlimefunItem slimefunItem : slimefunItems) {
            sendMessage(player, slimefunItem.getId());
        }
    }
}
