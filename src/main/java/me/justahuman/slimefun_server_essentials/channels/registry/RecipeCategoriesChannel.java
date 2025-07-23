package me.justahuman.slimefun_server_essentials.channels.registry;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;
import me.justahuman.slimefun_server_essentials.channels.AbstractChannel;
import me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters;
import me.justahuman.slimefun_server_essentials.util.DataUtils;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeCategoriesChannel extends AbstractChannel {
    @Override
    public void load() {
        if (!messages.isEmpty()) {
            return;
        }

        for (Map.Entry<SlimefunAddon, List<SlimefunItem>> registryEntry : Utils.getSortedAddonRegistry().entrySet()) {
            String addonId = format(registryEntry.getKey().getName());
            JsonObject categories = new JsonObject();
            List<RecipeCategoryBuilder> itemCategories = registryEntry.getValue().stream()
                    .map(RecipeCategoryExporters::itemsRecipesCategory)
                    .filter(Objects::nonNull).toList();
            RecipeCategoryBuilder.optimize(itemCategories);

            ByteArrayDataOutput itemCategoriesPacket = ByteStreams.newDataOutput();
            DataUtils.writeVersion(itemCategoriesPacket);
            itemCategoriesPacket.writeInt(itemCategories.size());
            for (RecipeCategoryBuilder category : itemCategories) {
                categories.add(category.getId(), category.toJson());
                category.toBytes(itemCategoriesPacket);
            }
            messages.addAll(splitMessage(itemCategoriesPacket.toByteArray()));

            Map<RecipeType, RecipeCategoryBuilder> builderMap = new LinkedHashMap<>();
            for (SlimefunItem item : registryEntry.getValue()) {
                RecipeType type = item.getRecipeType();
                RecipeCategoryBuilder builder = builderMap.getOrDefault(type, new RecipeCategoryBuilder(type.getKey().getKey()));
                RecipeCategoryExporters.exportTypeRecipes(item, builder);
                if (!builder.isEmpty()) {
                    builderMap.put(type, builder);
                }
            }
            RecipeCategoryBuilder.optimize(builderMap.values());

            ByteArrayDataOutput typeCategoriesPacket = ByteStreams.newDataOutput();
            DataUtils.writeVersion(typeCategoriesPacket);
            typeCategoriesPacket.writeInt(builderMap.size());
            for (RecipeCategoryBuilder category : builderMap.values()) {
                categories.add(category.getId(), category.toJson());
                category.toBytes(typeCategoriesPacket);
            }
            messages.addAll(splitMessage(typeCategoriesPacket.toByteArray()));
            JsonUtils.generated("recipe_categories/" + addonId, categories);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:recipe_categories";
    }
}
