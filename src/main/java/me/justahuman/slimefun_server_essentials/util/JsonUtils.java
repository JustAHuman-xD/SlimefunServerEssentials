package me.justahuman.slimefun_server_essentials.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.tr7zw.nbtapi.NBTItem;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import me.justahuman.slimefun_server_essentials.recipe.compat.misc.ComplexItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Optional;

public class JsonUtils {
    public static JsonObject getObjectOrDefault(JsonObject parent, String key, JsonObject def) {
        return parent.get(key) instanceof JsonObject json ? json : def;
    }

    public static JsonArray getArray(JsonObject parent, String key, JsonArray def) {
        final JsonElement element = parent.get(key);
        if (element instanceof JsonArray array) {
            return array;
        } else if (element == null) {
            return def;
        }

        final JsonArray array = new JsonArray();
        array.add(element);
        return array;
    }

    public static String getString(JsonObject parent, String key, String def) {
        return getPrimitive(parent, key).filter(JsonPrimitive::isString).map(JsonPrimitive::getAsString).orElse(def);
    }

    public static Boolean getBool(JsonObject parent, String key, Boolean def) {
        return getPrimitive(parent, key).filter(JsonPrimitive::isBoolean).map(JsonPrimitive::getAsBoolean).orElse(def);
    }

    public static Long getLong(JsonObject parent, String key, Long def) {
        return getNumber(parent, key).map(Number::longValue).orElse(def);
    }

    public static Integer getInt(JsonObject parent, String key, Integer def) {
        return getNumber(parent, key).map(Number::intValue).orElse(def);
    }

    public static Optional<Number> getNumber(JsonObject parent, String key) {
        return getPrimitive(parent, key).filter(JsonPrimitive::isNumber).map(JsonPrimitive::getAsNumber);
    }

    public static Optional<JsonPrimitive> getPrimitive(JsonObject parent, String key) {
        return parent.get(key) instanceof JsonPrimitive primitive ? Optional.of(primitive) : Optional.empty();
    }

    public static boolean equalAmount(JsonElement element1, JsonElement element2) {
        final Integer amount1 = getAmount(element1);
        final Integer amount2 = getAmount(element2);
        return amount1.equals(amount2);
    }

    // In Some Special Cases there is no Amount Provided, For Ex, if it is an entity it would be entity:entity_type, or if it is a fluid it would be fluid:fluid_type, in these cases we return 1
    public static Integer getAmount(JsonElement element) {
        if (element instanceof JsonPrimitive primitive && primitive.isString() && primitive.getAsString().contains(":")) {
            final String sub = primitive.getAsString().substring(primitive.getAsString().indexOf(":"));
            try {
                return Integer.parseInt(sub);
            } catch (NumberFormatException ignored) {}
        } else if (element instanceof JsonArray array) {
            return getAmount(array.get(0));
        }
        return 1;
    }

    public static JsonArray process(JsonArray complex, List<ItemStack> process) {
        final JsonArray processed = new JsonArray();
        for (ItemStack itemStack : process) {
            processed.add(process(complex, itemStack));
        }
        return processed;
    }

    public static JsonArray process(JsonArray complex, ItemStack[] process) {
        final JsonArray processed = new JsonArray();
        for (ItemStack itemStack : process) {
            processed.add(process(complex, itemStack));
        }
        return processed;
    }

    public static String process(JsonArray complexStacks, ItemStack itemStack) {
        return process(complexStacks, itemStack, 1);
    }

    public static String process(JsonArray complexStacks, ItemStack itemStack, float chance) {
        if (itemStack == null) {
            return "";
        }

        boolean complex = false;
        final StringBuilder processed = new StringBuilder();
        final String slimefunId = Slimefun.getItemDataService().getItemData(itemStack).orElse("");
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (!slimefunId.isBlank() && !(slimefunItem instanceof VanillaItem) && !(itemStack instanceof ComplexItem)) {
            processed.append(slimefunId);
        } else if (itemStack instanceof ComplexItem || (slimefunItem == null && itemStack.hasItemMeta() && (itemStack.getItemMeta().hasDisplayName() || itemStack.getItemMeta().hasLore()))) {
            processed.append('?').append(complexStacks.size());
            complexStacks.add(serializeItem(itemStack));
            complex = true;
        } else {
            processed.append(itemStack.getType().name().toLowerCase());
        }
        processed.append(':').append(itemStack.getAmount());

        if (chance != 1) {
            processed.append('%').append(chance);
        }

        if (!complex && itemStack.getItemMeta() instanceof Damageable damageable && damageable.hasDamage()) {
            processed.append('^').append(damageable.getDamage());
        }

        return processed.toString();
    }

    public static JsonObject serializeItem(SlimefunItem slimefunItem) {
        return serializeItem(slimefunItem.getItem());
    }

    public static void sortJsonArray(JsonArray array) {
        final List<JsonElement> jsonElements = JsonArrayList.of(array);
        jsonElements.sort((e1, e2) -> {
            if (e1 instanceof JsonObject json1 && e2 instanceof JsonObject json2) {
                final int timeCompare = Integer.compare(getInt(json1, "time", 1), getInt(json2, "time", 1));
                if (timeCompare != 0) {
                    return timeCompare;
                }
                final int inputsCompare = Integer.compare(getArray(json1, "inputs", new JsonArray()).size(), getArray(json2, "inputs", new JsonArray()).size());
                if (inputsCompare != 0) {
                    return timeCompare;
                }
                return Integer.compare(getArray(json1, "outputs", new JsonArray()).size(), getArray(json2, "outputs", new JsonArray()).size());
            } else if (e1 instanceof JsonPrimitive primitive1 && e2 instanceof JsonPrimitive primitive2) {
                if (primitive1.isString() && primitive2.isString()) {
                    return primitive1.getAsString().compareTo(primitive2.getAsString());
                } else if (primitive1.isNumber() && primitive2.isNumber()) {
                    return Float.compare(primitive1.getAsFloat(), primitive2.getAsFloat());
                }
            }
            return 0;
        });
    }

    public static void removeWhitespace(JsonArray array) {
        for (int i = array.size() - 1; i >= 0; i--) {
            if (array.get(i) instanceof JsonPrimitive primitive && primitive.isString() && primitive.getAsString().isBlank()) {
                array.remove(i);
            } else {
                break;
            }
        }
    }

    public static void removeWhitespace(JsonObject recipeObject) {
        final JsonArray inputs = getArray(recipeObject, "inputs", new JsonArray());
        final JsonArray outputs = getArray(recipeObject, "outputs", new JsonArray());
        removeWhitespace(inputs);
        removeWhitespace(outputs);
    }

    public static JsonObject serializeItem(ItemStack itemStack) {
        final JsonObject itemObject = new JsonObject();
        itemObject.add("item", new JsonPrimitive("minecraft:" + itemStack.getType().name().toLowerCase()));
        final String nbtString = new NBTItem(itemStack).getCompound().toString();
        itemObject.add("nbt", new JsonPrimitive(nbtString));
        return itemObject;
    }

    public static void addElementToArray(JsonArray parent, JsonElement element) {
        if (element instanceof JsonArray array) {
            parent.addAll(array);
        } else if (element instanceof JsonPrimitive primitive) {
            parent.add(primitive);
        }
    }

    public static void addArray(JsonObject jsonObject, String key, JsonArray array) {
        removeWhitespace(array);
        if (array.isEmpty()) {
            return;
        }

        jsonObject.add(key, array.size() == 1
                ? array.get(0)
                : array);
    }

    public static JsonObject serializeItemGroup(Player player, ItemGroup itemGroup) {
        final JsonObject groupObject = new JsonObject();
        final JsonArray items = new JsonArray();
        groupObject.add("item", serializeItem(itemGroup.getItem(player)));
        for (SlimefunItem slimefunItem : itemGroup.getItems()) {
            items.add(slimefunItem.getId());
        }

        if (!items.isEmpty()) {
            groupObject.add("items", items);
        }

        if (itemGroup instanceof NestedItemGroup nestedGroup) {
            final JsonArray children = new JsonArray();
            for (ItemGroup child : Utils.getSubItemGroups(nestedGroup)) {
                children.add(child.getKey().toString());
            }

            if (!children.isEmpty()) {
                groupObject.add("nested", children);
            }
        }

        if (itemGroup instanceof LockedItemGroup lockedGroup) {
            final JsonArray parents = new JsonArray();
            for (ItemGroup parent : lockedGroup.getParents()) {
                parents.add(parent.getKey().toString());
            }

            if (!parents.isEmpty()) {
                groupObject.add("locked", parents);
            }
        }

        if (itemGroup instanceof SeasonalItemGroup seasonalGroup) {
            groupObject.addProperty("seasonal", seasonalGroup.getMonth().getValue());
        }

        if (itemGroup instanceof SubItemGroup) {
            groupObject.addProperty("sub", true);
        }

        return groupObject;
    }
}
