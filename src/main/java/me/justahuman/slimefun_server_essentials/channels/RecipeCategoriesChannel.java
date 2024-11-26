package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;
import me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecipeCategoriesChannel extends AbstractChannel {
    @Override
    public void load() {
        if (messages.isEmpty()) {
            for (Map.Entry<SlimefunAddon, List<SlimefunItem>> registryEntry : Utils.getSortedAddonRegistry().entrySet()) {
                List<Pair<SlimefunItem, JsonObject>> itemCategories = registryEntry.getValue().stream()
                        .map(item -> new Pair<>(item, RecipeCategoryExporters.exportItemsRecipes(item)))
                        .filter(pair -> pair.getSecondValue() != null).toList();

                ByteArrayDataOutput itemCategoriesPacket = ByteStreams.newDataOutput();
                itemCategoriesPacket.writeInt(itemCategories.size());
                for (Pair<SlimefunItem, JsonObject> category : itemCategories) {
                    itemCategoriesPacket.writeUTF(category.getFirstValue().getId());
                    itemCategoriesPacket.writeUTF(category.getSecondValue().toString());
                }
                messages.addAll(splitMessage(itemCategoriesPacket.toByteArray()));

                Map<RecipeType, RecipeCategoryBuilder> builderMap = new LinkedHashMap<>();
                for (SlimefunItem item : registryEntry.getValue()) {
                    RecipeType type = item.getRecipeType();
                    RecipeCategoryBuilder builder = builderMap.getOrDefault(type, new RecipeCategoryBuilder());
                    RecipeCategoryExporters.exportTypeRecipes(item, builder);
                    if (!builder.isEmpty()) {
                        builderMap.put(type, builder);
                    }
                }

                ByteArrayDataOutput typeCategoriesPacket = ByteStreams.newDataOutput();
                typeCategoriesPacket.writeInt(builderMap.size());
                for (Map.Entry<RecipeType, RecipeCategoryBuilder> entry : builderMap.entrySet()) {
                    typeCategoriesPacket.writeUTF(entry.getKey().getKey().getKey());
                    typeCategoriesPacket.writeUTF(entry.getValue().build().toString());
                }
                messages.addAll(splitMessage(typeCategoriesPacket.toByteArray()));
            }
        }
    }

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        for (byte[] message : messages) {
            sendMessage(player, message);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:recipe_categories";
    }
}
