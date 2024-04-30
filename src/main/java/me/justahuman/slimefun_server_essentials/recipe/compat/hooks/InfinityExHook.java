package me.justahuman.slimefun_server_essentials.recipe.compat.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.mooy1.infinityexpansion.items.abstracts.AbstractEnergyCrafter;
import io.github.mooy1.infinityexpansion.items.blocks.StrainerBase;
import io.github.mooy1.infinityexpansion.items.generators.EnergyGenerator;
import io.github.mooy1.infinityexpansion.items.generators.GenerationType;
import io.github.mooy1.infinityexpansion.items.generators.InfinityReactor;
import io.github.mooy1.infinityexpansion.items.machines.GeoQuarry;
import io.github.mooy1.infinityexpansion.items.machines.GrowingMachine;
import io.github.mooy1.infinityexpansion.items.machines.MaterialGenerator;
import io.github.mooy1.infinityexpansion.items.machines.ResourceSynthesizer;
import io.github.mooy1.infinityexpansion.items.machines.SingularityConstructor;
import io.github.mooy1.infinityexpansion.items.machines.StoneworksFactory;
import io.github.mooy1.infinityexpansion.items.machines.VoidHarvester;
import io.github.mooy1.infinityexpansion.items.materials.Materials;
import io.github.mooy1.infinityexpansion.items.materials.Strainer;
import io.github.mooy1.infinityexpansion.items.mobdata.MobSimulationChamber;
import io.github.mooy1.infinityexpansion.items.quarries.Quarry;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.justahuman.slimefun_server_essentials.recipe.RecipeBuilder;
import me.justahuman.slimefun_server_essentials.recipe.RecipeExporter;
import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import me.justahuman.slimefun_server_essentials.recipe.compat.misc.ComplexItem;
import me.justahuman.slimefun_server_essentials.util.ReflectionUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfinityExHook extends PluginHook {
    private static final ItemStack INFINITY_INGOT_USAGE = new ComplexItem(new CustomItemStack(Materials.INFINITE_INGOT, meta -> {
       final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
       lore.add(0, " ");
       lore.add(0, ChatColor.WHITE + "Lasts: 27h 13m 20s");
       meta.setLore(lore);
    }));
    private static final ItemStack VOID_INGOT_USAGE = new ComplexItem(new CustomItemStack(Materials.VOID_INGOT, meta -> {
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add(0, " ");
        lore.add(0, ChatColor.WHITE + "Lasts: 4h 26m 40s");
        meta.setLore(lore);
    }));

    @Override
    public List<String> getSpecialCases() {
        return List.of("INFINITY_REACTOR", "GEO_QUARRY", "STRAINER_BASE", "RESOURCE_SYNTHESIZER", "SINGULARITY_CONSTRUCTOR",
                "STONE_WORKS_FACTORY", "VOID_HARVESTER", "MOB_SIMULATION_CHAMBER");
    }

    @Override
    public boolean handles(SlimefunItem slimefunItem) {
        return slimefunItem instanceof EnergyGenerator
                || slimefunItem instanceof AbstractEnergyCrafter
                || slimefunItem instanceof GrowingMachine
                || slimefunItem instanceof MaterialGenerator
                || slimefunItem instanceof Quarry;
    }

    @Override
    public void handle(JsonObject categoryObject, JsonArray recipesArray, SlimefunItem slimefunItem) {
        int energy = 0;
        if (slimefunItem instanceof InfinityReactor reactor) {
            recipesArray.add(new RecipeBuilder().input(INFINITY_INGOT_USAGE).input(VOID_INGOT_USAGE).build());
            energy = ReflectionUtils.getField(reactor, "gen", 0);
        } else if (slimefunItem instanceof GeoQuarry quarry) {
            final int time = ReflectionUtils.getField(quarry, "ticksPerOuptut", 0);
            final var recipes = fillGeoQuarryRecipes(quarry);
            for (var entry : recipes.entrySet()) {
                final World.Environment environment = entry.getKey().getSecondValue();
                final Biome biome = entry.getKey().getFirstValue();
                for (Map.Entry<ItemStack, Float> output : entry.getValue().toMap().entrySet()) {
                    add(recipesArray, new RecipeBuilder()
                            .label(RecipeExporter.getLabelName(environment))
                            .label(RecipeExporter.getLabelName(biome))
                            .output(output.getKey(), output.getValue())
                            .sfTicks(time));
                }
            }
            energy = ReflectionUtils.getField(quarry, "energyPerTick", 0);
        } else if (slimefunItem instanceof StrainerBase base) {
            final int time = ReflectionUtils.getField(base, "time", 0);
            final ItemStack potatoFish = new CustomItemStack(Material.POTATO, "&7:&6Potatofish&7:", "&eLucky");
            final ItemStack[] outputs = ReflectionUtils.getField(base, "OUTPUTS", new ItemStack[0]);
            for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                if (item instanceof Strainer strainer) {
                    add(recipesArray, new RecipeBuilder()
                            .input(strainer.getItem())
                            .output(Utils.damage(strainer.getItem()))
                            .output(potatoFish, 0.0001F)
                            .sfTicks(time / Strainer.getStrainer(strainer.getItem())));

                    for (ItemStack output : outputs) {
                        add(recipesArray, new RecipeBuilder()
                                .input(strainer.getItem())
                                .output(Utils.damage(strainer.getItem()))
                                .output(output, 1F / outputs.length)
                                .sfTicks(time / Strainer.getStrainer(strainer.getItem())));
                    }
                }
            }
        } else if (slimefunItem instanceof ResourceSynthesizer synthesizer) {
            final SlimefunItemStack[] recipes = ReflectionUtils.getField(synthesizer, "recipes", new SlimefunItemStack[0]);
            for (int i = 0; i < recipes.length; i += 3) {
                add(recipesArray, new RecipeBuilder()
                        .input(recipes[i])
                        .input(recipes[i + 1])
                        .output(recipes[i + 2])
                        .sfTicks(1));
            }
            energy = ReflectionUtils.getField(synthesizer, "energyPerTick", 0);
        } else if (slimefunItem instanceof SingularityConstructor constructor) {
            int sideLength = 0;
            final int speed = ReflectionUtils.getField(constructor, "speed", 1);
            final List<?> recipes = ReflectionUtils.getField(constructor, "RECIPE_LIST", new ArrayList<>());
            for (Object object : recipes) {
                final ItemStack input = ReflectionUtils.getField(object, "input", new ItemStack(Material.AIR));
                final ItemStack output = ReflectionUtils.getField(object, "output", new ItemStack(Material.AIR));
                final int amount = ReflectionUtils.getField(object, "amount", 0);
                final int stacks = amount / input.getMaxStackSize();
                final int extra = amount - stacks * input.getMaxStackSize();
                final int stackCount = stacks + (extra > 0 ? 1 : 0);
                final RecipeBuilder recipeBuilder = new RecipeBuilder().sfTicks(amount / speed).output(output);

                for (int i = 0; i < stacks; i++) {
                    recipeBuilder.input(new CustomItemStack(input, input.getMaxStackSize()));
                }

                if (extra > 0) {
                    recipeBuilder.input(new CustomItemStack(input, extra));
                }

                add(recipesArray, recipeBuilder);
                sideLength = Math.max(sideLength, (int) Math.ceil(Math.sqrt(stackCount)));
            }
            energy = ReflectionUtils.getField(constructor, "energyPerTick", 0);
        } else if (slimefunItem instanceof StoneworksFactory factory) {
            final Class<?>[] nestClasses = StoneworksFactory.class.getNestMembers();
            if (nestClasses.length == 0) {
                return;
            }

            final Class<?> choiceClass = nestClasses[0];
            if (!choiceClass.isEnum()) {
                return;
            }

            for (Object choice : choiceClass.getEnumConstants()) {
                final ItemStack choiceStack = ReflectionUtils.getField(choice, "item", new ItemStack(Material.AIR));
                final Material[] inputs = ReflectionUtils.getField(choice, "inputs", new Material[0]);
                final Material[] outputs = ReflectionUtils.getField(choice, "outputs", new Material[0]);
                for (int i = 0; i < inputs.length; i++) {
                    if (outputs.length > i) {
                        add(recipesArray, new RecipeBuilder()
                                .input(choiceStack)
                                .input(new ItemStack(inputs[i]))
                                .output(new ItemStack(outputs[i]))
                                .sfTicks(1));
                    }
                }
            }
            energy = ReflectionUtils.getField(factory, "energyPerTick", 0);
        } else if (slimefunItem instanceof VoidHarvester harvester) {

        } else if (slimefunItem instanceof MobSimulationChamber chamber) {

        } else if (slimefunItem instanceof EnergyGenerator generator) {
            energy = ReflectionUtils.getField(generator, "generation", 0);
            switch(ReflectionUtils.getField(generator, "type", GenerationType.SOLAR)) {
                case SOLAR -> add(recipesArray, new RecipeBuilder().sfTicks(1).label("day"));
                case LUNAR -> {
                    add(recipesArray, new RecipeBuilder().sfTicks(1).label("overworld").label("night"));
                    add(recipesArray, new RecipeBuilder().sfTicks(1).label("the_nether"));
                    add(recipesArray, new RecipeBuilder().sfTicks(1).label("the_end"));
                }
                case GEOTHERMAL -> {
                    add(recipesArray, new RecipeBuilder().sfTicks(1).label("overworld"));
                    add(recipesArray, new RecipeBuilder().sfTicks(1).energy(energy * 2).label("the_nether"));
                }
                case HYDROELECTRIC -> add(recipesArray, new RecipeBuilder().sfTicks(1).label("waterlogged"));
                case INFINITY -> add(recipesArray, new RecipeBuilder().sfTicks(1));
            }
        } else if (slimefunItem instanceof AbstractEnergyCrafter energyCrafter) {

        } else if (slimefunItem instanceof GrowingMachine growingMachine) {

        } else if (slimefunItem instanceof MaterialGenerator materialGenerator) {

        } else if (slimefunItem instanceof Quarry quarry) {

        }

        if (energy != 0) {
            categoryObject.addProperty("energy", energy);
        }
    }

    private Map<Pair<Biome, World.Environment>, RandomizedSet<ItemStack>> fillGeoQuarryRecipes(GeoQuarry geoQuarry) {
        final Map<Pair<Biome, World.Environment>, RandomizedSet<ItemStack>> recipes = ReflectionUtils.getField(geoQuarry, "recipes", new HashMap<>());
        for (Biome biome : Biome.values()) {
            for (World.Environment environment : World.Environment.values()) {
                recipes.computeIfAbsent(new Pair<>(biome, environment), ignored -> {
                    final RandomizedSet<ItemStack> set = new RandomizedSet<>();
                    for (GEOResource geoResource : Slimefun.getRegistry().getGEOResources().values()) {
                        final int supply = geoResource.getDefaultSupply(environment, biome);
                        if (supply > 0) {
                            set.add(geoResource.getItem(), supply);
                        }
                    }
                    return set;
                });
            }
        }
        return recipes;
    }

    @Override
    public String getHookName() {
        return "InfinityExpansion";
    }
}
