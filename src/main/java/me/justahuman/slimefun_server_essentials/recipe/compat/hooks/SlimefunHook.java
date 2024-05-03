package me.justahuman.slimefun_server_essentials.recipe.compat.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ProgrammableAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoAnvil;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoBrewer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ChargingBench;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricDustWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.BookBinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.OilPump;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner.IndustrialMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GoldPan;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import me.justahuman.slimefun_server_essentials.recipe.RecipeBuilder;
import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import me.justahuman.slimefun_server_essentials.util.ReflectionUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SlimefunHook extends PluginHook {
    @Override
    public List<String> getSpecialCases() {
        return List.of("CLIMBING_PICK", "ORE_WASHER", "AUTOMATED_PANNING_MACHINE");
    }

    @Override
    public boolean handles(SlimefunItem slimefunItem) {
        return slimefunItem instanceof GoldPan
                || slimefunItem instanceof ElectricGoldPan
                || slimefunItem instanceof ElectricDustWasher
                || slimefunItem instanceof ProgrammableAndroid
                || slimefunItem instanceof GEOMiner
                || slimefunItem instanceof OilPump
                || slimefunItem instanceof IndustrialMiner
                || slimefunItem instanceof AutoAnvil
                || slimefunItem instanceof AutoEnchanter
                || slimefunItem instanceof AutoDisenchanter
                || slimefunItem instanceof BookBinder
                || slimefunItem instanceof ChargingBench
                || slimefunItem instanceof AutoBrewer;
    }

    @Override
    public boolean handlesParent(RecipeType recipeType) {
        return recipeType == RecipeType.MOB_DROP
                || recipeType == RecipeType.BARTER_DROP;
    }

    @Override
    public void handle(JsonObject category, JsonArray recipes, SlimefunItem slimefunItem) {
        if (slimefunItem instanceof GoldPan pan) {
            for (Material input : pan.getInputMaterials()) {
                final RandomizedSet<ItemStack> outputs = ReflectionUtils.getField(pan, "randomizer", new RandomizedSet<>());
                for (Map.Entry<ItemStack, Float> output : outputs.toMap().entrySet()) {
                    add(recipes, new RecipeBuilder().input(new ItemStack(input)).output(output.getKey(), output.getValue()));
                }
            }
        } else if (slimefunItem instanceof ElectricGoldPan) {
            final GoldPan goldPan = ReflectionUtils.getField(slimefunItem, "goldPan", null);
            final GoldPan netherGoldPan = ReflectionUtils.getField(slimefunItem, "netherGoldPan", null);
            if (goldPan == null || netherGoldPan == null) {
                return;
            }

            for (Material input : goldPan.getInputMaterials()) {
                final RandomizedSet<ItemStack> outputs = ReflectionUtils.getField(goldPan, "randomizer", new RandomizedSet<>());
                for (Map.Entry<ItemStack, Float> output : outputs.toMap().entrySet()) {
                    add(recipes, new RecipeBuilder().input(new ItemStack(input)).output(output.getKey(), output.getValue()).sfTicks(6));
                }
            }
            for (Material input : netherGoldPan.getInputMaterials()) {
                final RandomizedSet<ItemStack> outputs = ReflectionUtils.getField(netherGoldPan, "randomizer", new RandomizedSet<>());
                for (Map.Entry<ItemStack, Float> output : outputs.toMap().entrySet()) {
                    add(recipes, new RecipeBuilder().input(new ItemStack(input)).output(output.getKey(), output.getValue()).sfTicks(8));
                }
            }
        } else if (slimefunItem instanceof OreWasher) {

        } else if (slimefunItem instanceof ElectricDustWasher) {

        } else if (slimefunItem instanceof GEOMiner) {

        } else if (slimefunItem instanceof OilPump) {

        } else if (slimefunItem instanceof AutoAnvil) {

        } else if (slimefunItem instanceof AutoEnchanter) {

        } else if (slimefunItem instanceof AutoDisenchanter) {

        } else if (slimefunItem instanceof BookBinder) {

        } else if (slimefunItem instanceof ChargingBench) {

        } else if (slimefunItem instanceof AutoBrewer) {
            
        }

        if (slimefunItem instanceof AContainer container) {
            category.addProperty("speed", container.getSpeed());
            category.addProperty("energy", -container.getEnergyConsumption());
        }
    }

    @Override
    public void handleParent(JsonObject category, JsonArray recipes, SlimefunItem slimefunItem) {

    }

    @Override
    public String getHookName() {
        return "Slimefun";
    }
}
