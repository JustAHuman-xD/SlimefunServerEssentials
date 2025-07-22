package me.justahuman.slimefun_server_essentials.api.event;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;
import me.justahuman.slimefun_server_essentials.api.display.AbstractDisplayBuilder;
import me.justahuman.slimefun_server_essentials.api.display.ComponentType;
import me.justahuman.slimefun_server_essentials.implementation.DisplayComponentTypes;
import me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class SlimefunEssentialsRegisterEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public void registerComponentTypes(ComponentType... componentTypes) {
        for (ComponentType componentType : componentTypes) {
            DisplayComponentTypes.register(componentType.id(), componentType);
        }
    }

    public void registerRecipeDisplay(String id, AbstractDisplayBuilder<?> displayBuilder) {
        RecipeDisplays.register(id, displayBuilder);
    }

    public void registerItemExporter(SlimefunItemStack slimefunItemStack, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        RecipeCategoryExporters.registerItemExporter(slimefunItemStack.getItem(), exporter);
    }

    public void registerItemExporter(SlimefunItem slimefunItem, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        RecipeCategoryExporters.registerItemExporter(slimefunItem, exporter);
    }

    public <C extends SlimefunItem> void registerItemExporter(Class<C> clazz, BiConsumer<C, RecipeCategoryBuilder> exporter) {
        RecipeCategoryExporters.registerItemExporter(clazz, exporter);
    }

    public <C extends RecipeType> void registerTypeExporter(C type, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        RecipeCategoryExporters.registerTypeExporter(type, exporter);
    }

    public <C extends RecipeType> void registerTypeExporter(C type, ItemStack item, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        RecipeCategoryExporters.registerTypeExporter(type, item, exporter);
    }

    @Override
    public @Nonnull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @Nonnull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
