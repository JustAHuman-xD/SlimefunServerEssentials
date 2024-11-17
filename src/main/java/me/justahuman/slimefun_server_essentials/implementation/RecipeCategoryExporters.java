package me.justahuman.slimefun_server_essentials.implementation;

import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class RecipeCategoryExporters {
    private static final Map<SlimefunItem, BiConsumer<SlimefunItem, RecipeCategoryBuilder>> ITEM_EXPORTERS = new HashMap<>();
    private static final List<ItemRecipeCategoryExporter<? extends SlimefunItem>> ITEM_CLASS_EXPORTERS = new ArrayList<>();
    private static final Map<RecipeType, BiConsumer<SlimefunItem, RecipeCategoryBuilder>> TYPE_EXPORTERS = new HashMap<>();

    public static JsonObject exportItemsRecipes(SlimefunItem slimefunItem) {
        BiConsumer<SlimefunItem, RecipeCategoryBuilder> itemExporter = ITEM_EXPORTERS.get(slimefunItem);
        if (itemExporter != null) {
            RecipeCategoryBuilder builder = new RecipeCategoryBuilder();
            itemExporter.accept(slimefunItem, builder);
            return builder.build();
        }

        for (ItemRecipeCategoryExporter<? extends SlimefunItem> classExporter : ITEM_CLASS_EXPORTERS) {
            RecipeCategoryBuilder exported = classExporter.tryExport(slimefunItem);
            if (exported != null) {
                return exported.build();
            }
        }
        return null;
    }


    public static void exportTypeRecipes(SlimefunItem slimefunItem, RecipeCategoryBuilder builder) {
        BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter = TYPE_EXPORTERS.get(slimefunItem.getRecipeType());
        if (exporter != null) {
            exporter.accept(slimefunItem, builder);
        }
    }

    public static void registerItemExporter(SlimefunItemStack slimefunItemStack, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        registerItemExporter(slimefunItemStack.getItem(), exporter);
    }

    public static void registerItemExporter(SlimefunItem slimefunItem, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        ITEM_EXPORTERS.put(slimefunItem, exporter);
    }

    public static <C extends SlimefunItem> void registerItemExporter(Class<C> clazz, BiConsumer<C, RecipeCategoryBuilder> exporter) {
        ITEM_CLASS_EXPORTERS.add(new ItemRecipeCategoryExporter<>(clazz, exporter));
        ITEM_CLASS_EXPORTERS.sort((e1, e2) -> {
            if (e1.clazz.isAssignableFrom(e2.clazz)) {
                return -1;
            } else if (e2.clazz.isAssignableFrom(e1.clazz)) {
                return 1;
            }
            return 0;
        });
    }

    public static <C extends RecipeType> void registerTypeExporter(C type, BiConsumer<SlimefunItem, RecipeCategoryBuilder> exporter) {
        TYPE_EXPORTERS.put(type, exporter);
    }

    private record ItemRecipeCategoryExporter<I extends SlimefunItem>(Class<I> clazz, BiConsumer<I, RecipeCategoryBuilder> exporter) {
        public RecipeCategoryBuilder tryExport(SlimefunItem slimefunItem) {
            if (clazz.isInstance(slimefunItem)) {
                RecipeCategoryBuilder builder = new RecipeCategoryBuilder();
                exporter.accept(clazz.cast(slimefunItem), builder);
                return builder;
            }
            return null;
        }
    }
}
