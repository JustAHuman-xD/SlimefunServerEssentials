package me.justahuman.slimefun_server_essentials;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlimefunServerEssentials extends JavaPlugin {

    @Getter
    private static SlimefunServerEssentials instance;

    @Override
    public void onEnable() {
        instance = this;

        new Metrics(instance, 18206);

        final PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        final CommandCompletions<BukkitCommandCompletionContext> commandCompletions = paperCommandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("addons", c -> Utils.getSlimefunAddonNames());
        paperCommandManager.registerCommand(new CommandManager());

        if (getConfig().getBoolean("automatic-addons", true)) {
            new AddonChannel(instance, getConfig().getStringList("addon-blacklist"));
        }

        if (getConfig().getBoolean("custom-block-textures", true)) {
            new BlockChannel(instance);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
