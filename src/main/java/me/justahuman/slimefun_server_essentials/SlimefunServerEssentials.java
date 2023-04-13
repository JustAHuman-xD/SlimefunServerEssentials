package me.justahuman.slimefun_server_essentials;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class SlimefunServerEssentials extends JavaPlugin {

    @Getter
    private static SlimefunServerEssentials instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin")) {
            getLogger().log(Level.SEVERE, "本插件需要 鬼斩前置库插件(GuizhanLibPlugin) 才能运行!");
            getLogger().log(Level.SEVERE, "从此处下载: https://50l.cc/gzlib");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        final CommandCompletions<BukkitCommandCompletionContext> commandCompletions = paperCommandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("addons", c -> Utils.getSlimefunAddonNames());
        paperCommandManager.registerCommand(new CommandManager());
        new AddonChannel().init(instance);
        new BlockChannel().init(instance);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
