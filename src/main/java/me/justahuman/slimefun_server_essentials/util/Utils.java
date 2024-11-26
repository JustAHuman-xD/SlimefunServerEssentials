package me.justahuman.slimefun_server_essentials.util;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.api.ComplexItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Utils {
    private static final Map<SlimefunAddon, List<SlimefunItem>> SORTED_ADDON_REGISTRY = new LinkedHashMap<>();
    private static final Map<RecipeType, List<SlimefunItem>> SORTED_RECIPE_REGISTRY = new LinkedHashMap<>();
    private static final ItemStack WATER_BOTTLE = new CustomItemStack(Material.POTION, meta -> ((PotionMeta) meta).setBasePotionType(PotionType.WATER));

    public static void load() {
        SORTED_ADDON_REGISTRY.clear();
        List<SlimefunAddon> addons = new ArrayList<>();
        List<RecipeType> recipeTypes = new ArrayList<>();
        Map<SlimefunAddon, List<SlimefunItem>> addonItems = new HashMap<>();
        Map<RecipeType, List<SlimefunItem>> recipeItems = new HashMap<>();
        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            addonItems.compute(item.getAddon(), (addon, items) -> {
                if (items == null) {
                    items = new ArrayList<>();
                }
                items.add(item);
                return items;
            });

            RecipeType recipeType = item.getRecipeType();
            recipeItems.compute(recipeType, (type, items) -> {
                if (items == null) {
                    items = new ArrayList<>();
                }
                items.add(item);
                return items;
            });

            if (!addons.contains(item.getAddon())) {
                addons.add(item.getAddon());
            }

            if (!recipeTypes.contains(recipeType)) {
                recipeTypes.add(recipeType);
            }
        }

        addons.remove(Slimefun.instance());
        addons.sort(byItemIndexInGroup(addonItems::get));
        addons.sort(byItemGroupIndex(addonItems::get));
        addons.addFirst(Slimefun.instance());

        for (SlimefunAddon addon : addons) {
            List<SlimefunItem> items = addonItems.get(addon);
            items.sort(byItemIndexInGroup(i -> items));
            items.sort(byItemGroupIndex(i -> items));
            SORTED_ADDON_REGISTRY.put(addon, items);
        }

        recipeTypes.sort(byItemIndexInGroup(recipeItems::get));
        recipeTypes.sort(byItemGroupIndex(recipeItems::get));

        for (RecipeType recipeType : recipeTypes) {
            List<SlimefunItem> items = recipeItems.get(recipeType);
            items.sort(byItemIndexInGroup(i -> items));
            items.sort(byItemGroupIndex(i -> items));
            SORTED_RECIPE_REGISTRY.put(recipeType, items);
        }
    }

    public static <T> Comparator<T> byItemIndexInGroup(Function<T, Collection<SlimefunItem>> items) {
        return Comparator.comparingInt(t -> items.apply(t).stream().mapToInt(item -> item.getItemGroup().getItems().indexOf(item)).min().orElse(0));
    }

    public static <T> Comparator<T> byItemGroupIndex(Function<T, Collection<SlimefunItem>> items) {
        return Comparator.comparingInt(t -> items.apply(t).stream().mapToInt(item -> Slimefun.getRegistry().getAllItemGroups().indexOf(item.getItemGroup())).min().orElse(0));
    }

    public static ItemStack waterBottle() {
        return WATER_BOTTLE.clone();
    }

    public static ItemStack damage(ItemStack itemStack) {
        return damage(itemStack, 1);
    }

    public static ItemStack maxDamage(ItemStack itemStack) {
        itemStack = itemStack.clone();
        if (itemStack.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(itemStack.getType().getMaxDurability() - 1);
            itemStack.setItemMeta(damageable);
        }
        return itemStack;
    }

    public static ItemStack damage(ItemStack itemStack, int damage) {
        itemStack = itemStack.clone();
        if (itemStack.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + damage);
            itemStack.setItemMeta(damageable);
        }
        return itemStack;
    }

    public static ItemStack enchant(ItemStack itemStack, Enchantment... enchantments) {
        itemStack = new ComplexItem(itemStack);
        for (Enchantment enchantment : enchantments) {
            itemStack.addUnsafeEnchantment(enchantment, enchantment.getStartLevel());
        }
        return itemStack;
    }

    @SafeVarargs
    public static <T> Set<T> merge(Collection<T> collection, T... values) {
        final Set<T> set = new HashSet<>(collection);
        set.addAll(Arrays.asList(values));
        return set;
    }

    @SafeVarargs
    public static <N extends Number> N max(N... numbers) {
        N max = null;
        for (N number : numbers) {
            if (max == null || number.floatValue() > max.floatValue()) {
                max = number;
            }
        }
        return max;
    }

    public static void log(String log) {
        SlimefunServerEssentials.getInstance().getLogger().info(log);
    }

    public static Map<SlimefunAddon, List<SlimefunItem>> getSortedAddonRegistry() {
        return SORTED_ADDON_REGISTRY;
    }

    public static Map<RecipeType, List<SlimefunItem>> getSortedRecipeRegistry() {
        return SORTED_RECIPE_REGISTRY;
    }
}
