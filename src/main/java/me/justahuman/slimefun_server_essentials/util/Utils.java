package me.justahuman.slimefun_server_essentials.util;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.recipe.compat.misc.ComplexItem;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {
    public static final Map<String, Set<SlimefunItem>> slimefunItems = new HashMap<>();
    public static final Map<String, Set<ItemGroup>> itemGroups = new HashMap<>();
    public static final Map<NamespacedKey, List<SubItemGroup>> subItemGroups = new HashMap<>();

    public static void load() {
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem instanceof VanillaItem) {
                continue;
            }
            final String addonName = slimefunItem.getAddon().getName();
            final Set<SlimefunItem> items = slimefunItems.getOrDefault(addonName, new HashSet<>());
            items.add(slimefunItem);
            slimefunItems.put(addonName, items);
        }

        for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
            if (itemGroup.getAddon() == null || (itemGroup instanceof FlexItemGroup || itemGroup.getItems().isEmpty())) {
                continue;
            }
            final String addonName = itemGroup.getAddon().getName();
            final Set<ItemGroup> groups = itemGroups.getOrDefault(addonName, new HashSet<>());
            groups.add(itemGroup);
            itemGroups.put(addonName, groups);
        }
    }
    
    public static boolean invalidSlimefunAddon(String addon) {
        return !slimefunItems.containsKey(addon);
    }
    
    public static Set<String> getSlimefunAddonNames() {
        return Collections.unmodifiableSet(slimefunItems.keySet());
    }
    
    public static Map<String, Set<SlimefunItem>> getSlimefunItems() {
        return Collections.unmodifiableMap(slimefunItems);
    }
    
    public static Set<SlimefunItem> getSlimefunItems(String addon) {
        return Collections.unmodifiableSet(slimefunItems.getOrDefault(addon, new HashSet<>()));
    }

    public static Set<ItemGroup> getItemGroups(String addon) {
        return Collections.unmodifiableSet(itemGroups.getOrDefault(addon, new HashSet<>()));
    }

    public static List<SubItemGroup> getSubItemGroups(NestedItemGroup nestedItemGroup) {
        if (subItemGroups.containsKey(nestedItemGroup.getKey())) {
            return subItemGroups.get(nestedItemGroup.getKey());
        }

        final List<SubItemGroup> subGroups = ReflectionUtils.getField(nestedItemGroup, "subGroups", new ArrayList<>());
        subItemGroups.put(nestedItemGroup.getKey(), subGroups);
        return subGroups;
    }

    public static List<SlimefunItem> getSortedSlimefunItems(String addon) {
        final List<SlimefunItem> sortedSlimefunItems = new ArrayList<>(getSlimefunItems(addon));
        sortedSlimefunItems.sort(Comparator.comparing(SlimefunItem::getId));
        return sortedSlimefunItems;
    }

    public static List<ItemGroup> getSortedItemGroups(String addon) {
        final List<ItemGroup> sortedItemGroups = new ArrayList<>(getItemGroups(addon));
        sortedItemGroups.sort(Comparator.comparingInt(ItemGroup::getTier));
        return sortedItemGroups;
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

    public static <T> Set<T> merge(Collection<T> collection, T... values) {
        final Set<T> set = new HashSet<>(collection);
        set.addAll(Arrays.asList(values));
        return set;
    }

    public static void log(String log) {
        SlimefunServerEssentials.getInstance().getLogger().info(log);
    }
}
