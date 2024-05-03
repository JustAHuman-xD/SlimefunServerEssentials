package me.justahuman.slimefun_server_essentials.recipe.compat.hooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ProgrammableAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoAnvil;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoBrewer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ChargingBench;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricDustWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.BookBinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ExpCollector;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.OilPump;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner.IndustrialMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GoldPan;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import me.justahuman.slimefun_server_essentials.recipe.RecipeBuilder;
import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import me.justahuman.slimefun_server_essentials.util.ReflectionUtils;
import me.justahuman.slimefun_server_essentials.util.Utils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SlimefunHook extends PluginHook {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

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
            final ItemStack[] dusts = ReflectionUtils.getField(slimefunItem, "dusts", new ItemStack[0]);
            for (ItemStack dust : dusts) {
                add(recipes, new RecipeBuilder().input(SlimefunItems.SIFTED_ORE).output(dust, 1F / dusts.length));
            }
            add(recipes, new RecipeBuilder().input(new ItemStack(Material.SAND, 2)).output(SlimefunItems.SALT));
            add(recipes, new RecipeBuilder().input(SlimefunItems.PULVERIZED_ORE).output(SlimefunItems.PURE_ORE_CLUSTER));
        } else if (slimefunItem instanceof ElectricDustWasher) {
            final OreWasher oreWasher = ReflectionUtils.getField(slimefunItem, "oreWasher", null);
            if (oreWasher == null) {
                return;
            }

            final ItemStack[] dusts = ReflectionUtils.getField(oreWasher, "dusts", new ItemStack[0]);
            for (ItemStack dust : dusts) {
                add(recipes, new RecipeBuilder().input(SlimefunItems.SIFTED_ORE).output(dust, 1F / dusts.length).sfTicks(8));
            }
            add(recipes, new RecipeBuilder().input(new ItemStack(Material.SAND, 2)).output(SlimefunItems.SALT).sfTicks(8));
            add(recipes, new RecipeBuilder().input(SlimefunItems.PULVERIZED_ORE).output(SlimefunItems.PURE_ORE_CLUSTER).sfTicks(8));
        } else if (slimefunItem instanceof GEOMiner geoMiner) {
            for (ItemStack output : geoMiner.getDisplayRecipes()) {
                add(recipes, new RecipeBuilder().output(output).sfTicks(14));
            }
            category.addProperty("speed", geoMiner.getSpeed());
            category.addProperty("energy", -geoMiner.getEnergyConsumption());
        } else if (slimefunItem instanceof OilPump) {
            add(recipes, new RecipeBuilder().input(new ItemStack(Material.BUCKET)).output(SlimefunItems.OIL_BUCKET).sfTicks(52));
        } else if (slimefunItem instanceof AutoAnvil anvil) {
            autoAnvilRecipe(anvil, recipes, Material.WOODEN_HOE);
            autoAnvilRecipe(anvil, recipes, Material.STONE_SHOVEL);
            autoAnvilRecipe(anvil, recipes, Material.IRON_PICKAXE);
            autoAnvilRecipe(anvil, recipes, Material.DIAMOND_AXE);
            autoAnvilRecipe(anvil, recipes, Material.NETHERITE_SWORD);
            autoAnvilRecipe(anvil, recipes, Material.LEATHER_HELMET);
            autoAnvilRecipe(anvil, recipes, Material.DIAMOND_CHESTPLATE);
            autoAnvilRecipe(anvil, recipes, Material.IRON_LEGGINGS);
            autoAnvilRecipe(anvil, recipes, Material.GOLDEN_BOOTS);
            autoAnvilRecipe(anvil, recipes, Material.SHIELD);
        } else if (slimefunItem instanceof AutoEnchanter || slimefunItem instanceof AutoDisenchanter) {
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.WOODEN_HOE, 1);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.STONE_SHOVEL, 2);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.IRON_PICKAXE, 3);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.DIAMOND_AXE, 4);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.NETHERITE_SWORD, 5);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.LEATHER_HELMET, 1);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.DIAMOND_CHESTPLATE, 2);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.IRON_LEGGINGS, 3);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.GOLDEN_BOOTS, 4);
            autoAbstractEnchantRecipe(slimefunItem, recipes, Material.SHIELD, 2);
        } else if (slimefunItem instanceof BookBinder) {
            add(recipes, new RecipeBuilder()
                    .input(Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), Enchantment.DAMAGE_ALL))
                    .input(Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), Enchantment.DURABILITY))
                    .output(Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), Enchantment.DAMAGE_ALL, Enchantment.DURABILITY))
                    .sfTicks(100));
        } else if (slimefunItem instanceof ExpCollector) {
            add(recipes, new RecipeBuilder().input("$:1").output(SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE).sfTicks(1));
            category.addProperty("energy", -10);
        } else if (slimefunItem instanceof ChargingBench) {

        } else if (slimefunItem instanceof AutoBrewer) {
            final Map<Material, PotionType> potionRecipes = ReflectionUtils.getField(slimefunItem, "potionRecipes", new HashMap<>());
            final Map<PotionType, PotionType> fermentations = ReflectionUtils.getField(slimefunItem, "fermentations", new HashMap<>());

        }

        if (slimefunItem instanceof AContainer container) {
            category.addProperty("speed", container.getSpeed());
            category.addProperty("energy", -container.getEnergyConsumption());
        } else if (slimefunItem instanceof MultiBlockMachine) {
            category.addProperty("type", "grid3");
        }
    }

    @Override
    public void handleParent(JsonObject category, JsonArray recipes, SlimefunItem slimefunItem) {

    }

    public void autoAnvilRecipe(AutoAnvil anvil, JsonArray recipes, Material material) {
        final ItemStack defaultItem = new ItemStack(material);
        final ItemStack damagedItem = Utils.maxDamage(defaultItem);
        final ItemStack repairedItem = ReflectionUtils.callMethod(anvil, "repair", defaultItem, damagedItem);
        add(recipes, new RecipeBuilder().input(damagedItem).input(SlimefunItems.DUCT_TAPE).output(repairedItem).sfTicks(60));
    }

    public void autoAbstractEnchantRecipe(SlimefunItem slimefunItem, JsonArray recipes, Material material, int enchants) {
        if (slimefunItem instanceof AutoEnchanter) {
            autoEnchantRecipe(recipes, material, enchants);
        } else {
            autoDisenchantRecipe(recipes, material, enchants);
        }
    }

    public void autoEnchantRecipe(JsonArray recipes, Material material, int enchants) {
        final List<Enchantment> applicable = getApplicableEnchantments(material, enchants);
        if (applicable.isEmpty()) {
            return;
        }

        final ItemStack normal = new ItemStack(material);
        final ItemStack book = Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), applicable.toArray(Enchantment[]::new));
        final ItemStack enchanted = Utils.enchant(normal, applicable.toArray(Enchantment[]::new));
        add(recipes, new RecipeBuilder().input(normal).input(book).output(enchanted).sfTicks(150 * enchants));
    }

    public void autoDisenchantRecipe(JsonArray recipes, Material material, int enchants) {
        final List<Enchantment> applicable = getApplicableEnchantments(material, enchants);
        if (applicable.isEmpty()) {
            return;
        }

        final ItemStack normal = Utils.enchant(new ItemStack(material), applicable.toArray(Enchantment[]::new));
        final ItemStack disenchanted = new ItemStack(material);
        final ItemStack book = Utils.enchant(new ItemStack(Material.ENCHANTED_BOOK), applicable.toArray(Enchantment[]::new));
        add(recipes, new RecipeBuilder().input(normal).output(disenchanted).output(book).sfTicks(180 * enchants));
    }

    public List<Enchantment> getApplicableEnchantments(Material material, int enchants) {
        final List<Enchantment> applicable = new ArrayList<>();
        final Enchantment[] enchantments = Enchantment.values();
        final ItemStack itemStack = new ItemStack(material);
        for (int i = 0; i < enchantments.length; i++) {
            final Enchantment enchantment = enchantments[RANDOM.nextInt(enchantments.length)];
            if (enchantment.canEnchantItem(itemStack)) {
                applicable.add(enchantment);
            }

            if (applicable.size() == enchants) {
                break;
            }
        }
        return applicable;
    }

    public void chargingBenchRecipe(JsonArray recipes, SlimefunItem slimefunItem) {

    }

    public void autoBrewerRecipe(JsonArray recipes) {
        add(recipes, new RecipeBuilder().sfTicks(60));
    }

    @Override
    public String getHookName() {
        return "Slimefun";
    }
}
