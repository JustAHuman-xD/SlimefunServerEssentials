package me.justahuman.slimefun_server_essentials;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
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
@CommandAlias("slimefun_server_essentials")
public class CommandManager extends BaseCommand {
    private static final Gson GSON = new Gson().newBuilder().create();
    private static final String PATH = "plugins/SlimefunServerEssentials/exported/";

    @Subcommand("block")
    @CommandPermission("slimefun_server_essentials.block")
    @Description("Sends the Slimefun Block Packet to tell a Client a Block is a Slimefun Block, Used in Testing")
    public void block(Player player, String[] args) {
        final Block block = player.getTargetBlock(null, 8);
        final SlimefunItem slimefunItem = BlockStorage.check(block);
        if (slimefunItem == null) {
            player.sendMessage(ChatColors.color("&cYou must be looking at a Slimefun Block"));
            return;
        }

        Optional.ofNullable(SlimefunServerEssentials.getBlockChannel())
                .ifPresent(blockChannel -> blockChannel.sendSlimefunBlock(player, new BlockPosition(block), slimefunItem.getId()));
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
        final String filePath = PATH + "items/" + addon.toLowerCase() + ".json";
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

    @Subcommand("export categories")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.categories")
    @Description("Exports the categories for a given Slimefun Addon to a Json File")
    public void exportCategories(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColors.color("&aFull tutorial available on the Wiki!"));
        if (args.length < 1 || Utils.invalidSlimefunAddon(args[0])) {
            sender.sendMessage(ChatColors.color("&cInvalid Slimefun Addon!"));
            return;
        }
        
        final String addon = args[0];
        final JsonObject root = new JsonObject();
        final String filePath = PATH + "categories/" + addon.toLowerCase() + ".json";
        final List<SlimefunItem> slimefunItems = Utils.getSortedSlimefunItems(addon);
    
        for (SlimefunItem slimefunItem : slimefunItems) {
            final JsonObject categoryObject = Utils.getCategory(slimefunItem);
            if (!categoryObject.keySet().isEmpty()) {
                Utils.addCategoryWithOptimize(slimefunItem.getId(), categoryObject, root);
            }
        }
        root.add("MULTIBLOCK", Utils.getMultiblockRecipes().deepCopy());
        Utils.clearMultiblockRecipes();
    
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
