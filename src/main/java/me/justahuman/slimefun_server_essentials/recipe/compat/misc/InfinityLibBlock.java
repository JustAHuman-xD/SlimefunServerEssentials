package me.justahuman.slimefun_server_essentials.recipe.compat.misc;

import io.github.mooy1.infinitylib.common.StackUtils;
import io.github.mooy1.infinitylib.machines.CraftingBlock;
import io.github.mooy1.infinitylib.machines.CraftingBlockRecipe;
import io.github.mooy1.infinitylib.machines.MachineBlock;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import me.justahuman.slimefun_server_essentials.util.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record InfinityLibBlock(List<Recipe> recipes, Integer energy) {
    public static InfinityLibBlock wrap(SlimefunItem slimefunItem) {
        if (slimefunItem instanceof CraftingBlock craftingBlock) {
            return wrap(craftingBlock);
        } else if (slimefunItem instanceof MachineBlock machineBlock) {
            return wrap(machineBlock);
        }
        return null;
    }

    public static InfinityLibBlock wrap(CraftingBlock craftingBlock) {
        Integer energy = null;
        final List<CraftingBlockRecipe> craftingRecipes = ReflectionUtils.getField(craftingBlock, "recipes", new ArrayList<>());
        final List<Recipe> recipes = new ArrayList<>(craftingRecipes.stream().map(Recipe::wrap).toList());

        if (craftingBlock instanceof EnergyNetComponent component) {
            final int energyDirect = ReflectionUtils.getField(craftingBlock, "energy", 0);
            final int energyPerTick = ReflectionUtils.getField(craftingBlock, "energyPerTick", 0);
            energy = Math.max(energyDirect, energyPerTick);
            if (component.getEnergyComponentType() == EnergyNetComponentType.CONSUMER) {
                energy *= -1;
            }
        }

        return new InfinityLibBlock(recipes, energy);
    }

    public static InfinityLibBlock wrap(MachineBlock machineBlock) {
        final int time = ReflectionUtils.getField(machineBlock, "ticksPerOutput", 1);
        final List<?> machineRecipes = ReflectionUtils.getField(machineBlock, "recipes", new ArrayList<>());
        final List<Recipe> recipes = new ArrayList<>(machineRecipes.stream().map(recipe -> Recipe.wrap(recipe, time)).toList());
        return new InfinityLibBlock(recipes, ReflectionUtils.getField(machineBlock, "energyPerTick", 0) * -1);
    }

    public record Recipe(List<ItemStack> inputs, List<ItemStack> outputs, Integer time) {
        public static Recipe wrap(CraftingBlockRecipe recipe) {
            return new Recipe(Arrays.asList(recipe.recipe()), new ArrayList<>(List.of(recipe.output())), null);
        }

        public static Recipe wrap(Object recipe, int time) {
            final String[] strings = ReflectionUtils.getField(recipe, "strings", new String[0]);
            final int[] amounts = ReflectionUtils.getField(recipe, "amounts", new int[0]);
            final ItemStack output = ReflectionUtils.getField(recipe, "output", new ItemStack(Material.AIR));
            final List<ItemStack> inputs = new ArrayList<>();
            for (int i = 0; i < strings.length; i++) {
                int amount = amounts[i];
                final int stacks = (int) Math.ceil(amount / 64D);
                for (int s = 1; s <= stacks; s++) {
                    final ItemStack input = StackUtils.itemByIdOrType(strings[i]);
                    input.setAmount(Math.min(amount, input.getMaxStackSize()));
                    amount -= input.getMaxStackSize();
                    inputs.add(input);
                }
            }
            return new Recipe(inputs, new ArrayList<>(List.of(output)), time);
        }
    }
}
