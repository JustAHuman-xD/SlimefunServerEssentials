package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;
import me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeCategoriesChannel extends AbstractChannel {
    private static final List<byte[]> MESSAGES = new ArrayList<>();

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        if (MESSAGES.isEmpty()) {
            for (Map.Entry<SlimefunAddon, List<SlimefunItem>> registryEntry : Utils.getSortedAddonRegistry().entrySet()) {
                List<SlimefunItem> items = registryEntry.getValue();
                for (SlimefunItem item : items) {
                    JsonObject category = RecipeCategoryExporters.exportItemsRecipes(item);
                    if (category != null) {
                        ByteArrayDataOutput itemCategoryPacket = ByteStreams.newDataOutput();
                        itemCategoryPacket.writeUTF(item.getId());
                        itemCategoryPacket.writeUTF(category.toString());
                        MESSAGES.addAll(splitMessage(itemCategoryPacket.toByteArray()));
                    }
                }

                Map<RecipeType, RecipeCategoryBuilder> builderMap = new HashMap<>();
                for (SlimefunItem item : items) {
                    RecipeType type = item.getRecipeType();
                    RecipeCategoryBuilder builder = builderMap.getOrDefault(type, new RecipeCategoryBuilder());
                    RecipeCategoryExporters.exportTypeRecipes(item, builder);
                    if (!builder.isEmpty()) {
                        builderMap.put(type, builder);
                    }
                }

                for (Map.Entry<RecipeType, RecipeCategoryBuilder> entry : builderMap.entrySet()) {
                    ByteArrayDataOutput typeCategoryPacket = ByteStreams.newDataOutput();
                    typeCategoryPacket.writeUTF(entry.getKey().getKey().getKey());
                    typeCategoryPacket.writeUTF(entry.getValue().build().toString());
                    MESSAGES.addAll(splitMessage(typeCategoryPacket.toByteArray()));
                }
            }
        }

        for (byte[] message : MESSAGES) {
            sendMessage(player, message);
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:recipe_categories";
    }
}
