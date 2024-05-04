package me.justahuman.slimefun_server_essentials.util;

import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import me.justahuman.slimefun_server_essentials.recipe.compat.hooks.InfinityExHook;
import me.justahuman.slimefun_server_essentials.recipe.compat.hooks.SlimefunHook;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

public class Hooks {
    public static final Set<PluginHook> HOOKS = new HashSet<>();

    public static void init(PluginManager manager) {
        new SlimefunHook();
        if (manager.isPluginEnabled("InfinityExpansion")) {
            new InfinityExHook();
        }
    }
}
