package me.justahuman.slimefun_server_essentials.util;

import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

public class Hooks {
    public static final Set<PluginHook> HOOKS = new HashSet<>();

    public static void init(PluginManager manager) {
        for (PluginHook hook : new HashSet<>(HOOKS)) {
            if (!manager.isPluginEnabled(hook.getHookName())) {
                HOOKS.remove(hook);
            }
        }
    }
}
