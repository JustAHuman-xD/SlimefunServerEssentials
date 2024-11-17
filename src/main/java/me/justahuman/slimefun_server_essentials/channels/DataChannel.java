package me.justahuman.slimefun_server_essentials.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;
import me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataChannel extends AbstractChannel {
    private static final List<byte[]> DATA = new ArrayList<>();

    @Override
    public void onRegisterConnection(@Nonnull Player player) {
        if (DATA.isEmpty()) {
            for (Map.Entry<String, JsonObject> entry : RecipeDisplays.getRecipeDisplays().entrySet()) {
                ByteArrayDataOutput displayPacket = ByteStreams.newDataOutput();
                displayPacket.writeUTF(entry.getKey());
                displayPacket.writeUTF(entry.getValue().toString());
                Bukkit.broadcastMessage(entry.getKey());
                Bukkit.broadcastMessage(entry.getValue().toString());
                DATA.add(displayPacket.toByteArray());
                Bukkit.broadcastMessage("bytes: " + DATA.getLast().length);
            }

            for (Map.Entry<SlimefunAddon, List<SlimefunItem>> registryEntry : Utils.getSortedAddonRegistry().entrySet()) {
                List<SlimefunItem> items = registryEntry.getValue();
                for (SlimefunItem item : items) {
                    Bukkit.broadcastMessage("=====================================");
                    ByteArrayDataOutput itemPacket = ByteStreams.newDataOutput();
                    itemPacket.writeUTF(item.getId());
                    itemPacket.writeUTF(JsonUtils.serializeItem(item).toString());
                    DATA.add(itemPacket.toByteArray());
                    Bukkit.broadcastMessage(item.getId());
                    Bukkit.broadcastMessage(JsonUtils.serializeItem(item).toString());
                    Bukkit.broadcastMessage("bytes: " + DATA.getLast().length);
                }

                for (SlimefunItem item : items) {
                    JsonObject category = RecipeCategoryExporters.exportItemsRecipes(item);
                    if (category != null) {
                        Bukkit.broadcastMessage("=====================================");
                        ByteArrayDataOutput itemCategoryPacket = ByteStreams.newDataOutput();
                        itemCategoryPacket.writeUTF(item.getId());
                        itemCategoryPacket.writeUTF(category.toString());
                        DATA.add(itemCategoryPacket.toByteArray());
                        Bukkit.broadcastMessage(item.getId());
                        Bukkit.broadcastMessage(category.toString());
                        Bukkit.broadcastMessage("bytes: " + DATA.getLast().length);
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
                    Bukkit.broadcastMessage("=====================================");
                    ByteArrayDataOutput typeCategoryPacket = ByteStreams.newDataOutput();
                    typeCategoryPacket.writeUTF(entry.getKey().getKey().getKey());
                    typeCategoryPacket.writeUTF(entry.getValue().build().toString());
                    DATA.add(typeCategoryPacket.toByteArray());
                    Bukkit.broadcastMessage(entry.getKey().getKey().getKey());
                    Bukkit.broadcastMessage(entry.getValue().build().toString());
                    Bukkit.broadcastMessage("bytes: " + DATA.getLast().length);
                }
            }
        }

        Bukkit.broadcastMessage("=====================================");
        for (byte[] data : DATA) {
            Bukkit.broadcastMessage("i: " + DATA.indexOf(data));
            sendMessage(player, data);
        }
        Bukkit.broadcastMessage("=====================================");
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:data";
    }
}
