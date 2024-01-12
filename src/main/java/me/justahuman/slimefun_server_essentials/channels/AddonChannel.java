package me.justahuman.slimefun_server_essentials.channels;

import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class AddonChannel extends AbstractChannel {
    private final List<String> blacklist;

    public AddonChannel(@Nonnull List<String> blacklist) {
        this.blacklist = blacklist;
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:addon";
    }

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        sendMessage(player, "clear");
        for (String slimefunAddon : Utils.getSlimefunAddonNames()) {
            if (blacklist.contains(slimefunAddon)) {
                continue;
            }

            sendMessage(player, slimefunAddon);
        }
    }
}
