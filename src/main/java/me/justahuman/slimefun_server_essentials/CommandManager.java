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

@SuppressWarnings("unused")
@CommandAlias("slimefun_server_essentials")
public class CommandManager extends BaseCommand {
    private static final Gson gson = new Gson().newBuilder().create();
    private static final String path = "plugins/SlimefunServerEssentials/exported/";

    @Subcommand("block")
    @CommandPermission("slimefun_server_essentials.block")
    @Description("向客户端发包，说明该方块为粘液科技方块（仅用于测试）")
    public void block(Player player, String[] args) {
        final Block block = player.getTargetBlock(null, 8);
        if (BlockStorage.check(block) == null) {
            player.sendMessage(ChatColors.color("&c你必须看向一个粘液科技方块"));
            return;
        }

        BlockChannel.sendSlimefunBlock(player, new BlockPosition(block), BlockStorage.check(block).getId());
    }
    
    @Subcommand("export items")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.items")
    @Description("导出指定附属的物品")
    public void exportItems(CommandSender sender, String[] args) {
        if (args.length < 1 || Utils.invalidSlimefunAddon(args[0])) {
            sender.sendMessage(ChatColors.color("&c无效的附属名称!"));
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
    @Description("导出所有附属的物品")
    public void exportAllItems(CommandSender sender, String[] args) {
        for (String addon : Utils.getSlimefunAddonNames()) {
            exportItems(sender, new String[] {addon});
        }
    }
    
    @Subcommand("export categories")
    @CommandCompletion("@addons")
    @CommandPermission("slimefun_server_essentials.export.categories")
    @Description("导出指定附属的分类")
    public void exportCategories(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColors.color("&a你现在可以在wiki上查看全部教程了!"));
        if (args.length < 1 || Utils.invalidSlimefunAddon(args[0])) {
            sender.sendMessage(ChatColors.color("&c无效的附属名称!"));
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
    @Description("导出所有附属的分类")
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
                sender.sendMessage(ChatColors.color("&c导出时发生错误! (检查控制台)"));
                e.printStackTrace();
                return;
            }
        }
    
        try {
            final BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8));
            gson.toJson(root, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        
            sender.sendMessage(ChatColors.color("&a成功导出至 " + filePath + "!"));
        } catch (IOException | SecurityException e) {
            sender.sendMessage(ChatColors.color("&c导出时发生错误! (检查控制台)"));
            e.printStackTrace();
        }
    }
}
