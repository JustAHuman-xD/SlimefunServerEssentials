package me.justahuman.recipe_exporter;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeExporter extends JavaPlugin {

    @Getter
    private static RecipeExporter instance;

    @Override
    public void onEnable() {
        instance = this;
        final PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        final CommandCompletions<BukkitCommandCompletionContext> commandCompletions = paperCommandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("addons", c -> Utils.getSlimefunAddonNames());
        commandCompletions.registerAsyncCompletion("item_groups", c -> Utils.getSlimefunAddonNames());
        paperCommandManager.registerCommand(new CommandManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
