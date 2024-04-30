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

import java.util.List;

public class JsonUtils {
    public static JsonObject getObjectOrDefault(JsonObject jsonObject, String key, JsonObject defaultValue) {
        return jsonObject.get(key) instanceof JsonObject otherObject ? otherObject : defaultValue;
    }

    public static JsonArray getArrayOrDefault(JsonObject jsonObject, String key, JsonArray defaultValue) {
        return jsonObject.get(key) instanceof JsonArray jsonArray ? jsonArray : defaultValue;
    }

    public static String getStringOrDefault(JsonObject jsonObject, String key, String defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString() ? jsonPrimitive.getAsString() : defaultValue;
    }

    public static Boolean getBooleanOrDefault(JsonObject jsonObject, String key, Boolean defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isBoolean() ? jsonPrimitive.getAsBoolean() : defaultValue;
    }

    public static Long getLongOrDefault(JsonObject jsonObject, String key, Long defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isNumber() ? jsonPrimitive.getAsLong() : defaultValue;
    }

    public static Integer getIntegerOrDefault(JsonObject jsonObject, String key, Integer defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isNumber() ? jsonPrimitive.getAsInt() : defaultValue;
    }

    public static boolean equalAmount(JsonElement element1, JsonElement element2) {
        final Integer amount1 = getAmount(element1);
        final Integer amount2 = getAmount(element2);
        return amount1.equals(amount2);
    }

    // In Some Special Cases there is no Amount Provided, For Ex, if it is an entity it would be entity:entity_type, or if it is a fluid it would be fluid:fluid_type, in these cases we return 1
    public static Integer getAmount(JsonElement jsonElement) {
        if (jsonElement instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString() && jsonPrimitive.getAsString().contains(":")) {
            final String sub = jsonPrimitive.getAsString().substring(jsonPrimitive.getAsString().indexOf(":"));
            try {
                return Integer.parseInt(sub);
            } catch (NumberFormatException ignored) {}
        } else if (jsonElement instanceof JsonArray jsonArray) {
            return getAmount(jsonArray.get(0));
        }
        return 1;
    }

    public static JsonArray processList(JsonArray complex, List<ItemStack> process) {
        final JsonArray processed = new JsonArray();
        for (ItemStack itemStack : process) {
            processed.add(process(complex, itemStack));
        }
        return processed;
    }

    public static JsonArray processArray(JsonArray complex, ItemStack[] process) {
        final JsonArray processed = new JsonArray();
        for (ItemStack itemStack : process) {
            processed.add(process(complex, itemStack));
        }
        return processed;
    }

    public static String process(JsonArray complex, ItemStack process) {
        return process(complex, process, 1);
    }

    public static String process(JsonArray complex, ItemStack process, float chance) {
        final StringBuilder processed = new StringBuilder();
        final String slimefunId = Slimefun.getItemDataService().getItemData(process).orElse("");
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(process);
        if (!slimefunId.isBlank() && !(slimefunItem instanceof VanillaItem) && !(process instanceof ComplexItem)) {
            processed.append(slimefunId);
        } else if (process != null) {
            if (process instanceof ComplexItem || (slimefunItem == null && process.hasItemMeta() && (process.getItemMeta().hasDisplayName() || process.getItemMeta().hasLore()))) {
                processed.append('?').append(complex.size());
                complex.add(serializeItem(process));
            } else {
                processed.append(process.getType().name().toLowerCase());
            }
        }

        processed.append(':').append(process.getAmount());

        if (chance != 100) {
            processed.append('%').append(chance);
        }
        return processed.toString();
    }

    public static JsonObject serializeItem(SlimefunItem slimefunItem) {
        return serializeItem(slimefunItem.getItem());
    }

    public static void sortJsonArray(JsonArray toSort) {
        final List<JsonElement> jsonElements = JsonArrayList.of(toSort);
        jsonElements.sort((e1, e2) -> {
            if (e1 instanceof JsonObject jsonObject1 && e2 instanceof JsonObject jsonObject2) {
                final int timeCompare = Integer.compare(getIntegerOrDefault(jsonObject1, "time", 1), getIntegerOrDefault(jsonObject2, "time", 1));
                if (timeCompare != 0) {
                    return timeCompare;
                }

                final int inputsCompare = Integer.compare(getArrayOrDefault(jsonObject1, "inputs", new JsonArray()).size(), getArrayOrDefault(jsonObject2, "inputs", new JsonArray()).size());
                if (inputsCompare != 0) {
                    return timeCompare;
                }

                return Integer.compare(getArrayOrDefault(jsonObject1, "outputs", new JsonArray()).size(), getArrayOrDefault(jsonObject2, "outputs", new JsonArray()).size());
            } else if (e1 instanceof JsonPrimitive jsonPrimitive1 && e2 instanceof JsonPrimitive jsonPrimitive2) {
                if (jsonPrimitive1.isString() && jsonPrimitive2.isString()) {
                    return jsonPrimitive1.getAsString().compareTo(jsonPrimitive2.getAsString());
                } else if (jsonPrimitive1.isNumber() && jsonPrimitive2.isNumber()) {
                    return Long.compare(jsonPrimitive1.getAsLong(), jsonPrimitive2.getAsLong());
                }
            }

            return 0;
        });
    }

    public static void removeWhitespace(JsonArray jsonArray) {
        for (int i = jsonArray.size() - 1; i >= 0; i--) {
            if (jsonArray.get(i) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString() && jsonPrimitive.getAsString().isBlank()) {
                jsonArray.remove(i);
            } else {
                break;
            }
        }
    }

    public static void removeWhitespace(JsonObject recipeObject) {
        final JsonArray inputs = getArrayOrDefault(recipeObject, "inputs", new JsonArray());
        final JsonArray outputs = getArrayOrDefault(recipeObject, "outputs", new JsonArray());
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

    public static void addElementToArray(JsonArray jsonArray, JsonElement jsonElement) {
        if (jsonElement instanceof JsonArray array) {
            jsonArray.addAll(array);
        } else if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
            jsonArray.add(jsonPrimitive);
        }
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
