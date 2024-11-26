package me.justahuman.slimefun_server_essentials.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.justahuman.slimefun_server_essentials.api.ComplexItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Objects;
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

    public static boolean equalAmount(String id1, String id2) {
        final Integer amount1 = getAmount(id1);
        final Integer amount2 = getAmount(id2);
        return amount1.equals(amount2);
    }

    public static Integer getAmount(String id) {
        final int start = id.lastIndexOf(':');
        if (start == -1) {
            return 1;
        }

        if (id.contains("%")) {
            id = id.substring(0, id.indexOf("%"));
        }

        if (id.contains("^")) {
            id = id.substring(0, id.indexOf("^"));
        }

        if (id.contains("*")) {
            id = id.substring(0, id.lastIndexOf('*'));
        }

        try {
            return Integer.parseInt(id.substring(start));
        } catch (NumberFormatException ignored) {}
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
        } else if (itemStack instanceof ComplexItem || (slimefunItem == null && !itemStack.equals(new ItemStack(itemStack.getType())))) {
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

        if (!complex && itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof Damageable damageable && damageable.hasDamage()) {
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
                final int ticksCompare = Integer.compare(getInt(json1, "sf_ticks", 1), getInt(json2, "sf_ticks", 1));
                if (ticksCompare != 0) {
                    return ticksCompare;
                }
                final int inputsCompare = Integer.compare(getArray(json1, "inputs", new JsonArray()).size(), getArray(json2, "inputs", new JsonArray()).size());
                if (inputsCompare != 0) {
                    return inputsCompare;
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
        if (itemStack == null) {
            itemStack = new ItemStack(Material.AIR);
        }

        final JsonObject itemObject = new JsonObject();
        final ReadWriteNBT itemNBT = NBT.itemStackToNBT(itemStack);
        final ReadWriteNBT components = itemNBT.getCompound("components");

        itemObject.addProperty("id", itemNBT.getString("id"));
        itemObject.addProperty("amount", itemStack.getAmount());
        if (components != null) {
            itemObject.add("components", new JsonPrimitive(components.toString()));
        }

        return itemObject;
    }

    public static void addArray(JsonObject jsonObject, String key, JsonArray array) {
        removeWhitespace(array);
        if (!array.isEmpty()) {
            jsonObject.add(key, array.size() == 1 ? array.get(0) : array);
        }
    }

    public static void addRecipeWithOptimize(JsonArray recipes, JsonObject recipeObject) {
        removeWhitespace(recipeObject);
        final OptimizedRecipe optimizedRecipe = optimizeRecipe(recipes, recipeObject);
        if (optimizedRecipe != null) {
            recipes.set(optimizedRecipe.index, optimizedRecipe.recipe);
        } else {
            recipes.add(recipeObject);
        }
    }

    /**
     * Attempts to optimize a Recipe {@link JsonObject} by merging it with another Recipe {@link JsonObject}
     * @param recipe1 The Recipe {@link JsonObject} to optimize
     * @param recipes The Recipes {@link JsonArray} to search for a match
     * @return {@link Pair} with the {@link Pair#getFirstValue()} being the index and the {@link Pair#getSecondValue()} being the optimized Recipe {@link JsonObject}
     */
    private static OptimizedRecipe optimizeRecipe(JsonArray recipes, JsonObject recipe1) {
        final JsonArray complex1 = getArray(recipe1, "complex", new JsonArray());
        final JsonArray inputs1 = getArray(recipe1, "inputs", new JsonArray());
        final JsonArray outputs1 = getArray(recipe1, "outputs", new JsonArray());
        final JsonArray labels1 = getArray(recipe1, "labels", new JsonArray());
        final Integer sfTicks1 = getInt(recipe1, "sf_ticks", null);
        final Integer energy1 = getInt(recipe1, "energy", null);

        for (int index = 0; index < recipes.size(); index++) {
            // This should always evaluate to false this is just an instanceof cast
            if (!(recipes.get(index) instanceof JsonObject recipe2)) {
                continue;
            }

            final JsonArray complex2 = getArray(recipe2, "complex", new JsonArray());
            final JsonArray inputs2 = getArray(recipe2, "inputs", new JsonArray());
            final JsonArray outputs2 = getArray(recipe2, "outputs", new JsonArray());
            final JsonArray labels2 = getArray(recipe2, "labels", new JsonArray());
            final Integer sfTicks2 = getInt(recipe2, "sf_ticks", null);
            final Integer energy2 = getInt(recipe1, "energy", null);

            if (!Objects.equals(sfTicks1, sfTicks2) || !Objects.equals(energy1, energy2)) {
                continue;
            }

            boolean canMerge = complex1.equals(complex2)
                    && labels1.equals(labels2)
                    && inputs1.size() == inputs2.size()
                    && outputs1.equals(outputs2);

            if (!canMerge) {
                continue;
            }

            boolean singleDifference = false;
            // Then we go through inputs, we can allow for a single difference as those are what will be merged
            for (int inputIndex = 0; inputIndex < inputs1.size(); inputIndex++) {
                if (!canMerge) {
                    break;
                }

                final String input1 = inputs1.get(inputIndex).getAsString();
                final String input2 = inputs2.get(inputIndex).getAsString();

                canMerge = input1.equals(input2) || input1.contains(input2) || input2.contains(input1);
                // We can allow for a single difference in the Inputs as that is the Point of Merging
                if (!canMerge && !singleDifference && equalAmount(input1, input2)) {
                    canMerge = true;
                    singleDifference = true;
                }
            }

            if (!canMerge) {
                continue;
            }

            // At this point we have confirmed that these 2 recipes can be merged, so let's do that.
            final JsonObject recipe3 = new JsonObject();
            final JsonArray inputs3 = new JsonArray();
            final JsonArray complex3 = complex1.deepCopy();
            final JsonArray labels3 = labels1.deepCopy();
            final JsonArray outputs3 = outputs1.deepCopy();

            for (int inputIndex = 0; inputIndex < inputs1.size(); inputIndex++) {
                final JsonElement inputElement1 = inputs1.get(inputIndex);
                final JsonElement inputElement2 = inputs2.size() - 1 < inputIndex ? new JsonPrimitive("") : inputs2.get(inputIndex);
                if (inputElement1.equals(inputElement2)) {
                    inputs3.add(inputElement1);
                } else {
                    inputs3.add(inputElement1.getAsString() + "," + inputElement2.getAsString());
                }
            }

            if (sfTicks1 != null) {
                recipe3.addProperty("sf_ticks", sfTicks1);
            }

            addArray(recipe3, "complex", complex3);
            addArray(recipe3, "inputs", inputs3);
            addArray(recipe3, "outputs", outputs3);
            addArray(recipe3, "labels", labels3);
            return new OptimizedRecipe(index, recipe3);
        }
        return null;
    }

    private record OptimizedRecipe(int index, JsonObject recipe) {}
}
