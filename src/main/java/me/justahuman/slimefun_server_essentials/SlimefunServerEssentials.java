package me.justahuman.slimefun_server_essentials;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.justahuman.slimefun_server_essentials.channels.AddonChannel;
import me.justahuman.slimefun_server_essentials.channels.BlockChannel;
import me.justahuman.slimefun_server_essentials.features.CommandManager;
import me.justahuman.slimefun_server_essentials.listeners.RegistryFinalizedListener;
import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlimefunServerEssentials extends JavaPlugin {

    @Getter
    private static SlimefunServerEssentials instance;

    @Getter
    private static AddonChannel addonChannel = null;

    @Getter
    private static BlockChannel blockChannel = null;

    @Override
    public void onEnable() {
        instance = this;

        new Metrics(instance, 18206);

        getServer().getPluginManager().registerEvents(new RegistryFinalizedListener(), this);

        final PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        final CommandCompletions<BukkitCommandCompletionContext> commandCompletions = paperCommandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("addons", c -> Utils.getSlimefunAddonNames());
        paperCommandManager.registerCommand(new CommandManager());

        if (getConfig().getBoolean("automatic-addons", true)) {
            addonChannel = new AddonChannel(getConfig().getStringList("addon-blacklist"));
        }

        if (getConfig().getBoolean("custom-block-textures", true)) {
            blockChannel = new BlockChannel();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
