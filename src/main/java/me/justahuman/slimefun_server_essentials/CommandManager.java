package me.justahuman.slimefun_server_essentials;

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
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@CommandAlias("slimefun_server_essentials")
public class CommandManager extends BaseCommand {
    private static final Gson gson = new Gson().newBuilder().create();
    private static final String path = "plugins/SlimefunServerEssentials/exported/";

    @Subcommand("block")
    @CommandPermission("slimefun_server_essentials.block")
    @Description("Sends the Slimefun Block Packet to tell a Client a Block is a Slimefun Block")
    public void block(Player player, String[] args) {
        final Block block = player.getTargetBlock(8);
        if (block == null || BlockStorage.check(block) == null) {
            player.sendMessage(ChatColors.color("&cYou must be looking at a Slimefun Block"));
            return;
        }

        BlockChannel.sendSlimefunBlock(player, new BlockPosition(block), BlockStorage.check(block).getId());
    }
    
    @Subcommand("export items")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.items")
    @Description("Exports the items for a given Slimefun Addon to a Json File")
    public void exportItems(CommandSender sender, String[] args) {
        if (args.length < 1 || Utils.invalidSlimefunAddon(args[0])) {
            sender.sendMessage(ChatColors.color("&cInvalid Slimefun Addon!"));
            return;
        }
        
        final String addon = args[0];
        final JsonObject root = new JsonObject();
        final String filePath = path + "items/" + addon.toLowerCase() + ".json";
        final List<SlimefunItem> slimefunItems = Utils.getSortedSlimefunItems(addon);
        
        for (SlimefunItem slimefunItem : slimefunItems) {
            root.add(slimefunItem.getId(), Utils.serializeItem(slimefunItem));
        }
        
        exportToFile(sender, root, filePath);
    }
    
    @Subcommand("export all_items")
    @CommandPermission("slimefun_server_essentials.export.items")
    @Description("Exports all items per Slimefun Addon")
    public void exportAllItems(CommandSender sender, String[] args) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportItems(sender, new String[] {addon});
        }
    }
    
    @Subcommand("export item_groups")
    @CommandCompletion("@item_groups")
    @CommandPermission("slimefun_server_essentials.export.item_groups")
    @Description("Exports the item groups for a given Slimefun Addon to a Json File")
    public void exportItemGroup(Player player, String[] args) {
        if (args.length < 1 || Utils.invalidSlimefunAddon(args[0])) {
            player.sendMessage(ChatColors.color("&cInvalid Slimefun Addon!"));
            return;
        }
    
        final String addon = args[0];
        final JsonObject root = new JsonObject();
        final String filePath = path + "item_groups/" + addon.toLowerCase() + ".json";
        final Set<ItemGroup> itemGroups = Utils.getItemGroups().getOrDefault(addon, new HashSet<>());
        
        for (ItemGroup itemGroup : itemGroups) {
            root.add(itemGroup.getKey().getKey(), Utils.serializeItemGroup(player, itemGroup));
        }
        
        exportToFile(player, root, filePath);
    }
    
    @Subcommand("export all_item_groups")
    @CommandPermission("slimefun_server_essentials.export.items")
    @Description("Exports all item groups per Slimefun Addon")
    public void exportAllItemGroups(Player player, String[] args) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportItemGroup(player, new String[] {addon});
        }
    }
    
    @Subcommand("export categories")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.categories")
    @Description("Exports the categories for a given Slimefun Addon to a Json File")
    public void exportCategories(CommandSender sender, String[] args) {
        if (args.length < 1 || Utils.invalidSlimefunAddon(args[0])) {
            sender.sendMessage(ChatColors.color("&cInvalid Slimefun Addon!"));
            return;
        }
        
        final String addon = args[0];
        final JsonObject root = new JsonObject();
        final String filePath = path + "categories/" + addon.toLowerCase() + ".json";
        final List<SlimefunItem> slimefunItems = Utils.getSortedSlimefunItems(addon);
    
        for (SlimefunItem slimefunItem : slimefunItems) {
            final JsonObject categoryObject = Utils.getCategory(slimefunItem);
            if (!categoryObject.keySet().isEmpty()) {
                Utils.addCategoryWithOptimize(slimefunItem.getId(), categoryObject, root);
            }
        }
    
        exportToFile(sender, root, filePath);
    }
    
    @Subcommand("export all_categories")
    @CommandPermission("slimefun_server_essentials.export.categories")
    @Description("Exports all categories per Slimefun Addon")
    public void exportAllCategories(CommandSender sender, String[] args) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportCategories(sender, new String[] {addon});
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
            gson.toJson(root, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        
            sender.sendMessage(ChatColors.color("&aSuccessfully exported to " + filePath + "!"));
        } catch (IOException | SecurityException e) {
            sender.sendMessage(ChatColors.color("&cAn error occurred while exporting! (Check the Console)"));
            e.printStackTrace();
        }
    }
}
