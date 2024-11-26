package me.justahuman.slimefun_server_essentials.implementation.core;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoAnvil;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoBrewer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ChargingBench;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricDustWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricSmeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.FluidPump;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.AnimalGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.CropGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.TreeGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.BookBinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.AutoBreeder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ExpCollector;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.IronGolemAssembler;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ProduceCollector;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.WitherAssembler;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.OilPump;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.BasicCircuitBoard;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.OrganicFertilizer;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.OrganicFood;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.AutomatedPanningMachine;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner.IndustrialMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GoldPan;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.justahuman.slimefun_server_essentials.api.RecipeBuilder;
import me.justahuman.slimefun_server_essentials.api.RecipeCategoryBuilder;
import me.justahuman.slimefun_server_essentials.api.ComplexItem;
import me.justahuman.slimefun_server_essentials.util.ReflectionUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters.registerItemExporter;
import static me.justahuman.slimefun_server_essentials.implementation.RecipeCategoryExporters.registerTypeExporter;
import static me.justahuman.slimefun_server_essentials.implementation.core.DefaultComponentTypes.*;

public class DefaultCategories {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final ItemStack TREE_INPUT = new CustomItemStack(Material.OAK_SAPLING, "&eAge 0 Sapling");
    private static final ItemStack TREE_OUTPUT = new CustomItemStack(Material.OAK_SAPLING, "&eAge 1 Sapling");
    private static final ItemStack CROP_INPUT = new CustomItemStack(Material.POTATO, "&eStage 0 Growth");
    private static final ItemStack CROP_OUTPUT = new CustomItemStack(Material.POTATO, "&eStage 1 Growth");
    private static boolean registered = false;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        registerItemExporter(MultiBlockMachine.class, (multiblock, builder) -> {
            // Slimefun saves it as Input, Output, Input, Output, So we skip on the Outputs
            ItemStack[] inputs = null;
            final List<ItemStack[]> multiBlockRecipes = multiblock.getRecipes();
            for (ItemStack[] outputs : multiBlockRecipes) {
                if (inputs == null) {
                    inputs = outputs;
                    continue;
                }
                builder.recipe(new RecipeBuilder().inputs(inputs).outputs(outputs));
                inputs = null;
            }
            builder.display(DefaultDisplays.GRID_3X3);
        });
        registerItemExporter(AbstractEnergyProvider.class, (provider, builder) -> {
            final Set<MachineFuel> fuelTypes = provider.getFuelTypes();
            for (MachineFuel machineFuel : fuelTypes) {
                final List<ItemStack> inputs = new ArrayList<>(List.of(machineFuel.getInput()));
                if (provider instanceof Reactor reactor && reactor.getCoolant() != null) {
                    for (int count = (int) Math.ceil(machineFuel.getTicks() / 50D); count != 0; count -= Math.min(count, 64)) {
                        inputs.add(new CustomItemStack(reactor.getCoolant(), Math.min(count, 64)));
                    }
                }
                builder.recipe(new RecipeBuilder().sfTicks(machineFuel.getTicks()).inputs(inputs).output(machineFuel.getOutput()));
            }

            if (provider instanceof Reactor) {
                builder.display(DefaultDisplays.REACTOR);
            }
            builder.energy(provider.getEnergyProduction());
        });
        registerItemExporter(AContainer.class, (container, builder) -> {
            for (MachineRecipe machineRecipe : container.getMachineRecipes()) {
                final ItemStack[] inputs = machineRecipe.getInput();
                final ItemStack[] outputs = machineRecipe.getOutput();
                builder.recipe(new RecipeBuilder().sfTicks(machineRecipe.getTicks()).inputs(inputs).outputs(outputs));
            }

            if (container instanceof ElectricSmeltery) {
                builder.display(DefaultDisplays.SMELTERY);
            }
            builder.speed(container.getSpeed());
            builder.energy(-container.getEnergyConsumption());
        });
        registerItemExporter(AncientAltar.class, (altar, builder) -> {
            for (AltarRecipe altarRecipe : altar.getRecipes()) {
                final List<ItemStack> i = altarRecipe.getInput();
                // Slimefun Ancient Altar Recipes are put in this Strange Order, don't ask me Why
                final ItemStack[] inputs = new ItemStack[] { i.get(0), i.get(1), i.get(2), i.get(7), altarRecipe.getCatalyst(), i.get(3), i.get(6), i.get(5), i.get(4) };
                // An Ancient Altar Task goes through 36 "stages" before completion, the delay between each is 8 ticks.
                builder.recipe(new RecipeBuilder().ticks(36 * 8).inputs(inputs).output(altarRecipe.getOutput()));
            }
        });
        registerItemExporter(SolarGenerator.class, (generator, builder) -> {
            builder.recipe(new RecipeBuilder().sfTicks(1).energy(generator.getDayEnergy()).label(REQUIRES_DAY));
            builder.recipe(new RecipeBuilder().sfTicks(1).energy(generator.getNightEnergy()).label(REQUIRES_NIGHT));
        });
        registerItemExporter(GoldPan.class, (pan, builder) -> {
            for (Material input : pan.getInputMaterials()) {
                final RandomizedSet<ItemStack> outputs = ReflectionUtils.getField(GoldPan.class, pan, "randomizer", new RandomizedSet<>());
                for (Map.Entry<ItemStack, Float> output : outputs.toMap().entrySet()) {
                    builder.recipe(new RecipeBuilder().input(new ItemStack(input)).output(output.getKey(), output.getValue()).sfTicks(60));
                }
            }
        });
        registerItemExporter(AutomatedPanningMachine.class, (machine, builder) -> {
           goldPanRecipes(builder, machine, 12, 12);
           builder.display(DefaultDisplays.GRID_3X3);
        });
        registerItemExporter(ElectricGoldPan.class, (pan, builder) -> {
           goldPanRecipes(builder, pan, 6, 8);
           builder.speed(pan.getSpeed());
           builder.energy(-pan.getEnergyConsumption());
        });
        registerItemExporter(OreWasher.class, (washer, builder) -> {
            final ItemStack[] dusts = ReflectionUtils.getField(washer, "dusts", new ItemStack[0]);
            for (ItemStack dust : dusts) {
                builder.recipe(new RecipeBuilder().input(SlimefunItems.SIFTED_ORE).output(dust, 1F / dusts.length));
            }
            builder.recipe(new RecipeBuilder().input(new ItemStack(Material.SAND, 2)).output(SlimefunItems.SALT));
            builder.recipe(new RecipeBuilder().input(SlimefunItems.PULVERIZED_ORE).output(SlimefunItems.PURE_ORE_CLUSTER));
            builder.display(DefaultDisplays.GRID_3X3);
        });
        registerItemExporter(ElectricDustWasher.class, (washer, builder) -> {
            final OreWasher oreWasher = ReflectionUtils.getField(washer, "oreWasher", null);
            if (oreWasher == null) {
                return;
            }

            final ItemStack[] dusts = ReflectionUtils.getField(oreWasher, "dusts", new ItemStack[0]);
            for (ItemStack dust : dusts) {
                builder.recipe(new RecipeBuilder().input(SlimefunItems.SIFTED_ORE).output(dust, 1F / dusts.length).sfTicks(8));
            }
            builder.recipe(new RecipeBuilder().input(new ItemStack(Material.SAND, 2)).output(SlimefunItems.SALT).sfTicks(8));
            builder.recipe(new RecipeBuilder().input(SlimefunItems.PULVERIZED_ORE).output(SlimefunItems.PURE_ORE_CLUSTER).sfTicks(8));
            builder.speed(washer.getSpeed());
            builder.energy(-washer.getEnergyConsumption());
        });
        registerItemExporter(GEOMiner.class, (miner, builder) -> {
            for (ItemStack output : miner.getDisplayRecipes()) {
                builder.recipe(new RecipeBuilder().output(output).sfTicks(14));
            }
            builder.speed(miner.getSpeed());
            builder.energy(-miner.getEnergyConsumption());
        });
        registerItemExporter(OilPump.class, (pump, builder) -> {
            builder.recipe(new RecipeBuilder().label("geo_scanned").input(new ItemStack(Material.BUCKET)).output(SlimefunItems.OIL_BUCKET).sfTicks(52));
            builder.speed(pump.getSpeed());
            builder.energy(-pump.getEnergyConsumption());
        });
        registerItemExporter(AutoAnvil.class, (anvil, builder) -> {
            autoAnvilRecipe(anvil, builder, Material.WOODEN_HOE);
            autoAnvilRecipe(anvil, builder, Material.STONE_SHOVEL);
            autoAnvilRecipe(anvil, builder, Material.IRON_PICKAXE);
            autoAnvilRecipe(anvil, builder, Material.DIAMOND_AXE);
            autoAnvilRecipe(anvil, builder, Material.NETHERITE_SWORD);
            autoAnvilRecipe(anvil, builder, Material.LEATHER_HELMET);
            autoAnvilRecipe(anvil, builder, Material.DIAMOND_CHESTPLATE);
            autoAnvilRecipe(anvil, builder, Material.IRON_LEGGINGS);
            autoAnvilRecipe(anvil, builder, Material.GOLDEN_BOOTS);
            autoAnvilRecipe(anvil, builder, Material.SHIELD);
            builder.speed(anvil.getSpeed());
            builder.energy(-anvil.getEnergyConsumption());
        });
        registerItemExporter(AutoEnchanter.class, DefaultCategories::autoAbstractEnchantRecipes);
        registerItemExporter(AutoDisenchanter.class, DefaultCategories::autoAbstractEnchantRecipes);
        registerItemExporter(BookBinder.class, (binder, builder) -> {
            builder.recipe(new RecipeBuilder()
                    .input(Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), Enchantment.SHARPNESS))
                    .input(Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), Enchantment.UNBREAKING))
                    .output(Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), Enchantment.SHARPNESS, Enchantment.UNBREAKING))
                    .sfTicks(100));
            builder.speed(binder.getSpeed());
            builder.energy(-binder.getEnergyConsumption());
        });
        registerItemExporter(ExpCollector.class, (collector, builder) -> {
            builder.recipe(new RecipeBuilder().input("$:1").output(SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE).sfTicks(1));
            builder.energy(-ReflectionUtils.getStaticField(ExpCollector.class,"ENERGY_CONSUMPTION", 10));
        });
        registerItemExporter(ChargingBench.class, (bench, builder) -> {
            chargingBenchRecipe(bench, builder, SlimefunItems.DURALUMIN_MULTI_TOOL);
            chargingBenchRecipe(bench, builder, SlimefunItems.SOLDER_MULTI_TOOL);
            chargingBenchRecipe(bench, builder, SlimefunItems.BILLON_MULTI_TOOL);
            chargingBenchRecipe(bench, builder, SlimefunItems.STEEL_MULTI_TOOL);
            chargingBenchRecipe(bench, builder, SlimefunItems.DAMASCUS_STEEL_MULTI_TOOL);
            chargingBenchRecipe(bench, builder, SlimefunItems.REINFORCED_ALLOY_MULTI_TOOL);
            chargingBenchRecipe(bench, builder, SlimefunItems.CARBONADO_MULTI_TOOL);
            builder.speed(bench.getSpeed());
            builder.energy(-bench.getEnergyConsumption());
        });
        registerItemExporter(AutoBrewer.class, (brewer, builder) -> {
            final Set<Material> basePotions = new HashSet<>(Set.of(Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION));
            final Set<Material> modifiers = new HashSet<>(Set.of(Material.FERMENTED_SPIDER_EYE, Material.NETHER_WART, Material.GUNPOWDER, Material.DRAGON_BREATH));
            final Map<Material, PotionType> potionRecipes = ReflectionUtils.getField(brewer, "potionRecipes", new HashMap<>());
            final Set<Material> ingredients = Utils.merge(potionRecipes.keySet(), Material.FERMENTED_SPIDER_EYE, Material.REDSTONE, Material.GLOWSTONE_DUST);
            for (Material potion : basePotions) {
                for (Material modifier : modifiers) {
                    final ItemStack itemStack = new ItemStack(potion);
                    final PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
                    potionMeta.setBasePotionType(PotionType.WATER);
                    itemStack.setItemMeta(potionMeta);

                    final Class<?>[] paramTypes = {Material.class, Material.class, PotionMeta.class};
                    final Object[] args = {modifier, itemStack.getType(), potionMeta};
                    final ItemStack result = ReflectionUtils.callMethod(brewer, "brew", null, paramTypes, args);
                    if (result == null) {
                        continue;
                    }

                    result.setItemMeta(potionMeta);
                    builder.recipe(new RecipeBuilder().complexInput(itemStack).input(new ItemStack(modifier)).complexOutput(result).sfTicks(60));
                    for (Material ingredient : ingredients) {
                        final PotionMeta newMeta = (PotionMeta) result.getItemMeta();
                        final Object[] newArgs = {ingredient, result.getType(), newMeta};
                        final ItemStack subResult = ReflectionUtils.callMethod(brewer, "brew", null, paramTypes, newArgs);
                        if (subResult != null) {
                            subResult.setItemMeta(newMeta);
                            builder.recipe(new RecipeBuilder().complexInput(result).input(new ItemStack(ingredient)).complexOutput(subResult).sfTicks(60));
                        }
                    }
                }
            }
        });
        registerItemExporter(IndustrialMiner.class, (miner, builder) -> {
            for (ItemStack itemStack : miner.getDisplayRecipes()) {
                builder.recipe(new RecipeBuilder().input(new ComplexItem(itemStack)));
            }
        });
        registerItemExporter(ProduceCollector.class, (collector, builder) -> {
            builder.recipe(new RecipeBuilder().input(EntityType.COW, false, 1, 0)
                    .input(Material.BUCKET).output(Material.MILK_BUCKET).sfTicks(1));
            builder.recipe(new RecipeBuilder().input(EntityType.GOAT, false, 1, 0)
                    .input(Material.BUCKET).output(Material.MILK_BUCKET).sfTicks(1));
            builder.recipe(new RecipeBuilder().input(EntityType.MOOSHROOM, false, 1, 0)
                    .input(Material.BOWL).output(Material.MUSHROOM_STEW).sfTicks(1));
            builder.speed(collector.getSpeed());
            builder.energy(-collector.getEnergyConsumption());
        });
        registerItemExporter(AutoBreeder.class, (breeder, builder) -> {
            for (OrganicFood organicFood : matching(OrganicFood.class)) {
                builder.recipe(new RecipeBuilder().input(EntityType.COW, false, 2, 0)
                        .input(organicFood.getItem())
                        .output(EntityType.COW, true, 1, 1).sfTicks(2));
            }
            builder.energy(-ReflectionUtils.getStaticField(AutoBreeder.class, "ENERGY_CONSUMPTION", 60));
        });
        registerItemExporter(AnimalGrowthAccelerator.class, (accelerator, builder) -> {
            for (OrganicFood organicFood : matching(OrganicFood.class)) {
                builder.recipe(new RecipeBuilder().input(EntityType.COW, true, 1, 1)
                        .input(organicFood.getItem())
                        .output(EntityType.COW, false, 1, 1).sfTicks(1));
            }
            builder.energy(-ReflectionUtils.getStaticField(AnimalGrowthAccelerator.class, "ENERGY_CONSUMPTION", 14));
        });
        registerItemExporter(TreeGrowthAccelerator.class, (accelerator, builder) -> {
            for (OrganicFertilizer fertilizer : matching(OrganicFertilizer.class)) {
                builder.recipe(new RecipeBuilder().input(TREE_INPUT).input(fertilizer.getItem()).output(TREE_OUTPUT).sfTicks(1));
            }
            builder.energy(-ReflectionUtils.getStaticField(TreeGrowthAccelerator.class, "ENERGY_CONSUMPTION", 24));
        });
        registerItemExporter(CropGrowthAccelerator.class, (accelerator, builder) -> {
            for (OrganicFertilizer fertilizer : matching(OrganicFertilizer.class)) {
                builder.recipe(new RecipeBuilder().input(CROP_INPUT).input(fertilizer.getItem()).output(CROP_OUTPUT).sfTicks(1));
            }
            builder.speed(accelerator.getSpeed());
            builder.energy(-accelerator.getEnergyConsumption());
        });
        registerItemExporter(FluidPump.class, (pump, builder) -> {
            builder.recipe(new RecipeBuilder().input(Fluid.WATER, 1).input(Material.BUCKET).output(Material.WATER_BUCKET).sfTicks(1));
            builder.recipe(new RecipeBuilder().input(Fluid.WATER, 1).input(Material.GLASS_BOTTLE).output(Utils.waterBottle()).sfTicks(1));
            builder.recipe(new RecipeBuilder().input(Fluid.LAVA, 1).input(Material.BUCKET).output(Material.LAVA_BUCKET).sfTicks(1));
            builder.energy(-ReflectionUtils.getStaticField(FluidPump.class, "ENERGY_CONSUMPTION", 32));
        });
        registerItemExporter(IronGolemAssembler.class, (assembler, builder) -> {
            builder.recipe(new RecipeBuilder().input(Material.PUMPKIN).input(Material.IRON_BLOCK, 4).output(EntityType.IRON_GOLEM, false, 1, 1).sfTicks(60));
            builder.energy(-assembler.getEnergyConsumption());
        });
        registerItemExporter(WitherAssembler.class, (assembler, builder) -> {
            builder.recipe(new RecipeBuilder().input(Material.WITHER_SKELETON_SKULL, 3).input(Material.SOUL_SAND, 4).output(EntityType.WITHER, false, 1, 1).sfTicks(60));
            builder.energy(-assembler.getEnergyConsumption());
        });
        registerItemExporter(SlimefunItems.CLIMBING_PICK, (item, builder) -> {});
        registerTypeExporter(RecipeType.BARTER_DROP, (item, builder) -> builder.recipe(new RecipeBuilder().input(EntityType.PIGLIN, false, 1, 0).input(Material.GOLD_INGOT).output(item.getRecipeOutput())));
        registerTypeExporter(RecipeType.MOB_DROP, (item, builder) -> {
            if (item instanceof BasicCircuitBoard board && board.isDroppedFromGolems()) {
                builder.recipe(new RecipeBuilder().input(EntityType.IRON_GOLEM, false, 1, 0).output(board.getRecipeOutput(), board.getMobDropChance() / 100F));
                return;
            }
            builder.recipe(new RecipeBuilder().inputs(item.getRecipe()).output(item.getRecipeOutput()));
            builder.display(DefaultDisplays.GRID_3X3);
        });
        registerTypeExporter(RecipeType.MULTIBLOCK, DefaultCategories::defaultTypeHandler);
        registerTypeExporter(RecipeType.INTERACT, DefaultCategories::defaultTypeHandler);
    }

    private static void defaultTypeHandler(SlimefunItem item, RecipeCategoryBuilder builder) {
        builder.recipe(new RecipeBuilder().inputs(item.getRecipe()).output(item.getRecipeOutput()));
        builder.display(DefaultDisplays.GRID_3X3);
    }

    private static void goldPanRecipes(RecipeCategoryBuilder builder, SlimefunItem slimefunItem, int normalTicks, int netherTicks) {
        final GoldPan goldPan = ReflectionUtils.getField(slimefunItem, "goldPan", null);
        final GoldPan netherGoldPan = ReflectionUtils.getField(slimefunItem, "netherGoldPan", null);
        if (goldPan == null || netherGoldPan == null) {
            return;
        }

        goldPanRecipes(builder, goldPan, normalTicks);
        goldPanRecipes(builder, netherGoldPan, netherTicks);
    }

    private static void goldPanRecipes(RecipeCategoryBuilder builder, GoldPan goldPan, int ticks) {
        for (Material input : goldPan.getInputMaterials()) {
            final RandomizedSet<ItemStack> outputs = ReflectionUtils.getField(GoldPan.class, goldPan, "randomizer", new RandomizedSet<>());
            for (Map.Entry<ItemStack, Float> output : outputs.toMap().entrySet()) {
                builder.recipe(new RecipeBuilder().input(new ItemStack(input)).output(output.getKey(), output.getValue()).sfTicks(ticks));
            }
        }
    }

    private static void autoAnvilRecipe(AutoAnvil anvil, RecipeCategoryBuilder builder, Material material) {
        final ItemStack defaultItem = new ItemStack(material);
        final ItemStack damagedItem = Utils.maxDamage(defaultItem);
        final ItemStack repairedItem = ReflectionUtils.callMethod(AutoAnvil.class, anvil, "repair", defaultItem, new Class<?>[]{ItemStack.class}, new Object[]{damagedItem});
        builder.recipe(new RecipeBuilder().input(damagedItem).input(SlimefunItems.DUCT_TAPE).output(repairedItem).sfTicks(60));
    }

    private static void autoAbstractEnchantRecipes(AContainer enchanterItem, RecipeCategoryBuilder builder) {
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.WOODEN_HOE, 1);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.STONE_SHOVEL, 2);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.IRON_PICKAXE, 3);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.DIAMOND_AXE, 4);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.NETHERITE_SWORD, 5);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.LEATHER_HELMET, 1);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.DIAMOND_CHESTPLATE, 2);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.IRON_LEGGINGS, 3);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.GOLDEN_BOOTS, 4);
        autoAbstractEnchantRecipe(enchanterItem, builder, Material.SHIELD, 2);
        builder.speed(enchanterItem.getSpeed());
        builder.energy(-enchanterItem.getEnergyConsumption());
    }

    private static void autoAbstractEnchantRecipe(SlimefunItem slimefunItem, RecipeCategoryBuilder builder, Material material, int enchants) {
        if (slimefunItem instanceof AutoEnchanter) {
            autoEnchantRecipe(builder, material, enchants);
        } else {
            autoDisenchantRecipe(builder, material, enchants);
        }
    }

    private static void autoEnchantRecipe(RecipeCategoryBuilder builder, Material material, int enchants) {
        final List<Enchantment> applicable = getApplicableEnchantments(material, enchants);
        if (applicable.isEmpty()) {
            return;
        }

        final ItemStack normal = new ItemStack(material);
        final ItemStack book = Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), applicable.toArray(Enchantment[]::new));
        final ItemStack enchanted = Utils.enchant(normal, applicable.toArray(Enchantment[]::new));
        builder.recipe(new RecipeBuilder().input(normal).input(book).output(enchanted).sfTicks(150 * enchants));
    }

    private static void autoDisenchantRecipe(RecipeCategoryBuilder builder, Material material, int enchants) {
        final List<Enchantment> applicable = getApplicableEnchantments(material, enchants);
        if (applicable.isEmpty()) {
            return;
        }

        final ItemStack normal = Utils.enchant(new ItemStack(material), applicable.toArray(Enchantment[]::new));
        final ItemStack disenchanted = new ItemStack(material);
        final ItemStack book = Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), applicable.toArray(Enchantment[]::new));
        builder.recipe(new RecipeBuilder().input(normal).output(disenchanted).output(book).sfTicks(180 * enchants));
    }

    private static List<Enchantment> getApplicableEnchantments(Material material, int enchants) {
        final List<Enchantment> applicable = new ArrayList<>();
        final Enchantment[] enchantments = Enchantment.values();
        final ItemStack itemStack = new ItemStack(material);
        for (int i = 0; i < enchantments.length; i++) {
            final Enchantment enchantment = enchantments[RANDOM.nextInt(enchantments.length)];
            if (!enchantment.getKey().getKey().contains("curse") && enchantment.canEnchantItem(itemStack)) {
                applicable.add(enchantment);
            }

            if (applicable.size() == enchants) {
                break;
            }
        }
        return applicable;
    }

    private static void chargingBenchRecipe(ChargingBench bench, RecipeCategoryBuilder builder, ItemStack itemStack) {
        if (!(SlimefunItem.getByItem(itemStack) instanceof Rechargeable rechargeable)) {
            return;
        }

        final float charge = bench.getEnergyConsumption() / 2.0F;
        final ItemStack charged = itemStack.clone();
        rechargeable.addItemCharge(charged, charge);
        builder.recipe(new RecipeBuilder().input(itemStack).output(new ComplexItem(charged)).sfTicks(1));
    }

    private static <I extends SlimefunItem> List<I> matching(Class<I> clazz) {
        final List<I> items = new ArrayList<>();
        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (clazz.isInstance(item)) {
                items.add(clazz.cast(item));
            }
        }
        return items;
    }
}
