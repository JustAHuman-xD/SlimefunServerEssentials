package me.justahuman.slimefun_server_essentials.features;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.recipe.RecipeExporter;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@CommandAlias("slimefun_server_essentials|sse")
public class CommandManager extends BaseCommand {
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final String PATH = "plugins/SlimefunServerEssentials/exported/";

    @Subcommand("block")
    @CommandPermission("slimefun_server_essentials.block")
    @Description("Sends the Slimefun Block Packet to tell a Client a Block is a Slimefun Block, Used in Testing")
    public void block(Player player) {
        final Block block = player.getTargetBlock(null, 8);
        final SlimefunItem slimefunItem = BlockStorage.check(block);
        if (slimefunItem == null) {
            player.sendMessage(ChatColors.color("&cYou must be looking at a Slimefun Block"));
            return;
        }

        Optional.ofNullable(SlimefunServerEssentials.getBlockChannel())
                .ifPresent(blockChannel -> blockChannel.sendSlimefunBlock(player, new BlockPosition(block), slimefunItem.getId()));
    }

    @Subcommand("export_all")
    @CommandPermission("slimefun_server_essentials.export_all")
    @Description("Exports everything from a server")
    public void exportAll(CommandSender sender) {
        exportAllItems(sender);
        exportAllRecipes(sender);
        exportAllItemGroups(sender);
    }

    @Subcommand("export item_groups")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.item_groups")
    @Description("Exports the item groups for a given Slimefun Addon to a Json File")
    public void exportItemGroups(CommandSender sender, String addon) {
        final JsonObject root = new JsonObject();
        final String filePath = PATH + "item_groups/" + addon.toLowerCase() + ".json";
        final List<ItemGroup> itemGroups = Utils.getSortedItemGroups(addon);

        for (ItemGroup itemGroup : itemGroups) {
            root.add(itemGroup.getKey().getKey(), JsonUtils.serializeItemGroup(itemGroup));
        }

        exportToFile(sender, root, filePath);
    }

    @Subcommand("export all_item_groups")
    @CommandPermission("slimefun_server_essentials.export.item_groups")
    @Description("Exports all item groups for each Slimefun Addon")
    public void exportAllItemGroups(CommandSender sender) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportItemGroups(sender, addon);
        }
    }

    @Subcommand("export items")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.items")
    @Description("Exports the items for a given Slimefun Addon to a Json File")
    public void exportItems(CommandSender sender, String addon) {
        final JsonObject root = new JsonObject();
        final String filePath = PATH + "items/" + addon.toLowerCase() + ".json";
        final List<SlimefunItem> slimefunItems = Utils.getSortedSlimefunItems(addon);

        for (SlimefunItem slimefunItem : slimefunItems) {
            root.add(slimefunItem.getId(), JsonUtils.serializeItem(slimefunItem));
        }
        
        exportToFile(sender, root, filePath);
    }
    
    @Subcommand("export all_items")
    @CommandPermission("slimefun_server_essentials.export.items")
    @Description("Exports all items for each Slimefun Addon")
    public void exportAllItems(CommandSender sender) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportItems(sender, addon);
        }
    }

    @Subcommand("export recipes")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.recipes")
    @Description("Exports the recipes for a given Slimefun Addon to a Json File")
    public void exportRecipes(CommandSender sender, String addon) {
        final JsonObject root = new JsonObject();
        final String filePath = PATH + "recipes/" + addon.toLowerCase() + ".json";
        final List<SlimefunItem> slimefunItems = Utils.getSortedSlimefunItems(addon);

        // Add Child Recipes
        for (SlimefunItem slimefunItem : slimefunItems) {
            final JsonObject category = RecipeExporter.getCategory(slimefunItem);
            if (!category.keySet().isEmpty()) {
                RecipeExporter.addCategoryWithOptimize(slimefunItem.getId(), category, root);
            }
        }

        // Add any missing parent recipes (custom ones, i.e. multiblocks, harvest, etc)
        for (SlimefunItem slimefunItem : slimefunItems) {
            final RecipeType recipeType = slimefunItem.getRecipeType();
            if (recipeType.toItem() == null || recipeType.getMachine() != null || SlimefunItem.getByItem(recipeType.toItem()) != null) {
                continue;
            }

            final JsonObject category = JsonUtils.getObjectOrDefault(root, recipeType.getKey().getKey(), new JsonObject());
            RecipeExporter.addParentCategory(category, slimefunItem);
            root.add(recipeType.getKey().getKey(), category);
        }
    
        exportToFile(sender, root, filePath);
    }
    
    @Subcommand("export all_recipes")
    @CommandPermission("slimefun_server_essentials.export.recipes")
    @Description("Exports all recipes for each Slimefun Addon")
    public void exportAllRecipes(CommandSender sender) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportRecipes(sender, addon);
        }
    }
    
    private void exportToFile(CommandSender sender, JsonObject root, String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            try {
                boolean ignored = file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new IOException();
                }
            } catch(IOException | SecurityException e) {
                sender.sendMessage(ChatColors.color("&cAn error occurred while exporting! (Check the Console)"));
                e.printStackTrace();
                return;
            }
        }
    
        try {
            final BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8));
            GSON.toJson(root, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        
            sender.sendMessage(ChatColors.color("&aSuccessfully exported to " + filePath + "!"));
        } catch (IOException | SecurityException e) {
            sender.sendMessage(ChatColors.color("&cAn error occurred while exporting! (Check the Console)"));
            e.printStackTrace();
        }
    }
}
