package me.justahuman.slimefun_server_essentials.recipe.compat.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.mooy1.infinityexpansion.items.abstracts.EnergyConsumer;
import io.github.mooy1.infinityexpansion.items.abstracts.EnergyProducer;
import io.github.mooy1.infinityexpansion.items.blocks.StrainerBase;
import io.github.mooy1.infinityexpansion.items.generators.EnergyGenerator;
import io.github.mooy1.infinityexpansion.items.generators.InfinityReactor;
import io.github.mooy1.infinityexpansion.items.machines.GearTransformer;
import io.github.mooy1.infinityexpansion.items.machines.GeoQuarry;
import io.github.mooy1.infinityexpansion.items.machines.GrowingMachine;
import io.github.mooy1.infinityexpansion.items.machines.MaterialGenerator;
import io.github.mooy1.infinityexpansion.items.machines.ResourceSynthesizer;
import io.github.mooy1.infinityexpansion.items.machines.SingularityConstructor;
import io.github.mooy1.infinityexpansion.items.machines.StoneworksFactory;
import io.github.mooy1.infinityexpansion.items.machines.VoidHarvester;
import io.github.mooy1.infinityexpansion.items.materials.Materials;
import io.github.mooy1.infinityexpansion.items.materials.Strainer;
import io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard;
import io.github.mooy1.infinityexpansion.items.mobdata.MobDataTier;
import io.github.mooy1.infinityexpansion.items.mobdata.MobSimulationChamber;
import io.github.mooy1.infinityexpansion.items.quarries.DimensionOscillator;
import io.github.mooy1.infinityexpansion.items.quarries.Oscillator;
import io.github.mooy1.infinityexpansion.items.quarries.Quarry;
import io.github.mooy1.infinityexpansion.items.quarries.QuarryPool;
import io.github.mooy1.infinitylib.machines.AbstractMachineBlock;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfinityExHook extends PluginHook {
    private static final ItemStack INFINITY_INGOT_USAGE = new ComplexItem(new CustomItemStack(Materials.INFINITE_INGOT, meta -> {
       final List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
       lore.add(0, " ");
       lore.add(0, ChatColor.WHITE + "Lasts: 27h 13m 20s");
       meta.setLore(lore);
    }));
    private static final ItemStack VOID_INGOT_USAGE = new ComplexItem(new CustomItemStack(Materials.VOID_INGOT, meta -> {
        final List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        lore.add(0, " ");
        lore.add(0, ChatColor.WHITE + "Lasts: 4h 26m 40s");
        meta.setLore(lore);
    }));

    @Override
    public List<String> getSpecialCases() {
        return List.of("INFINITY_REACTOR", "GEO_QUARRY", "STRAINER_BASE", "RESOURCE_SYNTHESIZER", "SINGULARITY_CONSTRUCTOR",
                "STONE_WORKS_FACTORY", "VOID_HARVESTER", "MOB_SIMULATION_CHAMBER", "GEAR_TRANSFORMER");
    }

    @Override
    public boolean handles(SlimefunItem slimefunItem) {
        return slimefunItem instanceof EnergyGenerator
                || slimefunItem instanceof GrowingMachine
                || slimefunItem instanceof MaterialGenerator
                || slimefunItem instanceof Quarry
                || slimefunItem instanceof MobDataCard
                || slimefunItem instanceof Oscillator;
    }

    @Override
    public void handle(JsonObject category, JsonArray recipes, SlimefunItem slimefunItem) {
        int energy = 0;
        if (slimefunItem instanceof EnergyProducer producer) {
            energy = producer.getEnergyGenerated();
        } else if (slimefunItem instanceof EnergyConsumer consumer) {
            energy = consumer.getEnergyConsumption();
        } else if (slimefunItem instanceof AbstractMachineBlock machineBlock) {
            energy = ReflectionUtils.getField(AbstractMachineBlock.class, machineBlock, "energyPerTick", 0);
        }

        if (slimefunItem instanceof InfinityReactor) {
            recipes.add(new RecipeBuilder().input(INFINITY_INGOT_USAGE).input(VOID_INGOT_USAGE).build());
        } else if (slimefunItem instanceof GeoQuarry quarry) {
            for (var entry : fillGeoQuarryRecipes(quarry).entrySet()) {
                final World.Environment dimension = entry.getKey().getSecondValue();
                final Biome biome = entry.getKey().getFirstValue();
                for (Map.Entry<ItemStack, Float> output : entry.getValue().toMap().entrySet()) {
                    add(recipes, new RecipeBuilder()
                            .label(RecipeExporter.getLabelName(dimension))
                            .label(RecipeExporter.getLabelName(biome))
                            .output(output.getKey(), output.getValue())
                            .sfTicks(quarry.getSfTicks()));
                }
            }
        } else if (slimefunItem instanceof StrainerBase base) {
            final int time = base.getSfTicks();
            final ItemStack[] outputs = StrainerBase.OUTPUTS;
            for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                if (item instanceof Strainer strainer) {
                    add(recipes, new RecipeBuilder()
                            .input(strainer.getItem())
                            .output(Utils.damage(strainer.getItem()))
                            .output(StrainerBase.POTATO, 0.0001F)
                            .sfTicks(time / Strainer.getStrainer(strainer.getItem())));

                    for (ItemStack output : outputs) {
                        add(recipes, new RecipeBuilder()
                                .input(strainer.getItem())
                                .output(Utils.damage(strainer.getItem()))
                                .output(output, 1F / outputs.length)
                                .sfTicks(time / Strainer.getStrainer(strainer.getItem())));
                    }
                }
            }
        } else if (slimefunItem instanceof ResourceSynthesizer synthesizer) {
            final SlimefunItemStack[] synthesizerRecipes = synthesizer.recipes();
            for (int i = 0; i < synthesizerRecipes.length; i += 3) {
                add(recipes, new RecipeBuilder()
                        .input(synthesizerRecipes[i])
                        .input(synthesizerRecipes[i + 1])
                        .output(synthesizerRecipes[i + 2])
                        .sfTicks(1));
            }
        } else if (slimefunItem instanceof SingularityConstructor constructor) {
            int sideLength = 0;
            final int speed = constructor.getSpeed();
            for (SingularityConstructor.Recipe recipe : SingularityConstructor.RECIPE_LIST()) {
                final ItemStack input = recipe.input();
                final ItemStack output = recipe.output();
                final int amount = recipe.amount();
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

                add(recipes, recipeBuilder);
                sideLength = Math.max(sideLength, (int) Math.ceil(Math.sqrt(stackCount)));
            }
            category.addProperty("type", "grid" + sideLength);
            category.addProperty("speed", speed);
        } else if (slimefunItem instanceof StoneworksFactory) {
            for (StoneworksFactory.Choice choice : StoneworksFactory.Choice.values()) {
                final ItemStack choiceStack = choice.item();
                final Material[] inputs = choice.inputs();
                final Material[] outputs = choice.outputs();
                for (int i = 0; i < inputs.length; i++) {
                    if (outputs.length > i) {
                        add(recipes, new RecipeBuilder()
                                .input(choiceStack)
                                .input(new ItemStack(inputs[i]))
                                .output(new ItemStack(outputs[i]))
                                .sfTicks(1));
                    }
                }
            }
        } else if (slimefunItem instanceof VoidHarvester harvester) {
            add(recipes, new RecipeBuilder().output(Materials.VOID_BIT).sfTicks(harvester.getSfTicks()));
            category.addProperty("speed", harvester.getSpeed());
        } else if (slimefunItem instanceof MobSimulationChamber chamber) {
            for (SlimefunItem item : MobDataCard.CARDS.values()) {
                if (item instanceof MobDataCard card) {
                    final RandomizedSet<ItemStack> drops = card.drops();
                    final MobDataTier tier = card.tier();
                    final RecipeBuilder builder = new RecipeBuilder()
                            .input(card.getItem(), 0)
                            .energy(chamber.getEnergyConsumption() + tier.energy())
                            .sfTicks(chamber.getSfTicks());

                    for (Map.Entry<ItemStack, Float> entry : drops.toMap().entrySet()) {
                        builder.output(entry.getKey(), entry.getValue());
                    }
                    add(recipes, builder.output("$:" + tier.xp()));
                }
            }
        } else if (slimefunItem instanceof EnergyGenerator generator) {
            switch(generator.type()) {
                case SOLAR -> add(recipes, new RecipeBuilder().sfTicks(1).label("day"));
                case LUNAR -> {
                    add(recipes, new RecipeBuilder().sfTicks(1).label("overworld").label("night"));
                    add(recipes, new RecipeBuilder().sfTicks(1).label("the_nether"));
                    add(recipes, new RecipeBuilder().sfTicks(1).label("the_end"));
                }
                case GEOTHERMAL -> {
                    add(recipes, new RecipeBuilder().sfTicks(1).label("overworld"));
                    add(recipes, new RecipeBuilder().sfTicks(1).energy(energy * 2).label("the_nether"));
                }
                case HYDROELECTRIC -> add(recipes, new RecipeBuilder().sfTicks(1).label("waterlogged"));
                case INFINITY -> add(recipes, new RecipeBuilder().sfTicks(1));
            }
        } else if (slimefunItem instanceof GearTransformer) {
            final String[] toolTypes = GearTransformer.TOOL_TYPES;
            final String[] armorTypes = GearTransformer.ARMOR_TYPES;
            final String[] toolMaterials = GearTransformer.TOOL_MATERIALS;
            final String[] armorMaterials = GearTransformer.ARMOR_MATERIALS;
            final ItemStack[] toolRecipe = GearTransformer.TOOL_RECIPE;
            final ItemStack[] armorRecipe = GearTransformer.ARMOR_RECIPE;

            addUpgradeRecipes(recipes, toolTypes, toolMaterials, toolRecipe);
            addUpgradeRecipes(recipes, armorTypes, armorMaterials, armorRecipe);
        } else if (slimefunItem instanceof GrowingMachine machine) {
            for (Map.Entry<Material, ItemStack[]> recipe : machine.recipes().entrySet()) {
                add(recipes, new RecipeBuilder()
                        .input(new ItemStack(recipe.getKey()), 0)
                        .outputs(recipe.getValue())
                        .sfTicks(machine.getSfTicks()));
            }
        } else if (slimefunItem instanceof MaterialGenerator generator) {
            add(recipes, new RecipeBuilder().output(new ItemStack(generator.material(), generator.speed())).sfTicks(1));
        } else if (slimefunItem instanceof Quarry quarry) {
            final int speed = quarry.speed();
            final int chance = quarry.chance();
            final int interval = ReflectionUtils.getField(quarry, "INTERVAL", 10);
            final Map<World.Environment, QuarryPool> pools = quarry.getPools();
            for (World.Environment dimension : pools.keySet()) {
                final QuarryPool pool = pools.get(dimension);
                final int baseChance = pool.chanceOverride(chance);
                add(recipes, new RecipeBuilder()
                        .output(new ItemStack(pool.commonDrop(), speed), (baseChance - 1F) / baseChance)
                        .label(RecipeExporter.getLabelName(dimension))
                        .sfTicks(interval));

                final var entries = new ArrayList<>(pool.drops().toMap().entrySet());
                entries.sort(Comparator.comparingDouble(Map.Entry::getValue));
                Collections.reverse(entries);
                for (Map.Entry<Material, Float> drop : entries) {
                    add(recipes, new RecipeBuilder()
                            .output(new ItemStack(drop.getKey(), speed), (1F / baseChance) * drop.getValue())
                            .label(RecipeExporter.getLabelName(dimension))
                            .sfTicks(interval));
                }

                for (Oscillator oscillator : Oscillator.getOscillators()) {
                    if (oscillator instanceof DimensionOscillator) {
                        continue;
                    }

                    add(recipes, new RecipeBuilder()
                            .input(oscillator.getItem(), 0)
                            .output(new ItemStack(oscillator.getItem().getType(), speed),
                                    (float) (1.0 / (double) quarry.chance() * oscillator.chance))
                            .label(RecipeExporter.getLabelName(dimension))
                            .sfTicks(interval));
                }
            }

            for (Oscillator oscillator : Oscillator.getOscillators()) {
                if (!(oscillator instanceof DimensionOscillator dimensionOscillator)) {
                    continue;
                }

                final float baseChance = (float) ((1F / quarry.chance()) * dimensionOscillator.chance);
                final World.Environment dimension = ReflectionUtils.getField(dimensionOscillator, "dimension", World.Environment.NORMAL);
                final QuarryPool pool = quarry.getPools().get(dimension);
                ArrayList<Map.Entry<Material, Float>> entries = new ArrayList<>(pool.drops().toMap().entrySet());
                entries.sort(Comparator.comparingDouble(Map.Entry::getValue));
                Collections.reverse(entries);

                add(recipes, new RecipeBuilder()
                        .input(oscillator.getItem(), 0)
                        .output(new ItemStack(pool.commonDrop(), quarry.speed()), baseChance * (1F/10F))
                        .sfTicks(interval));
                for (Map.Entry<Material, Float> drop : entries) {
                    add(recipes, new RecipeBuilder()
                            .input(oscillator.getItem(), 0)
                            .output(new ItemStack(drop.getKey(), quarry.speed()),
                                    baseChance * (9F/10F) * drop.getValue())
                            .sfTicks(interval));
                }
            }
        }

        if (energy != 0) {
            category.addProperty("energy", energy);
        }
    }

    private void addUpgradeRecipes(JsonArray recipesArray, String[] types, String[] materials, ItemStack[] recipes) {
        for (String armorType : types) {
            for (int i = 0; i < materials.length; i++) {
                Material armorInput = Material.getMaterial(materials[i] + armorType);
                Material armorOutput = Material.getMaterial(materials[Math.min(materials.length - 1, i + 1)] + armorType);
                if (armorInput == null || armorOutput == null || armorInput == armorOutput) {
                    continue;
                }

                ItemStack input = recipes[i];
                add(recipesArray, new RecipeBuilder()
                        .input(input)
                        .input(new ItemStack(armorInput))
                        .output(new ItemStack(armorOutput))
                        .sfTicks(1));
            }
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
