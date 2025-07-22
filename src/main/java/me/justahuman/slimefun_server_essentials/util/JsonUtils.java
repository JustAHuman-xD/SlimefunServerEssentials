package me.justahuman.slimefun_server_essentials.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.api.ComplexItem;
import me.justahuman.slimefun_server_essentials.api.RecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JsonUtils {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void deleteGenerated() {
        File generated = new File(SlimefunServerEssentials.getInstance().getDataFolder(), "generated");
        try {
            if (generated.exists()) {
                generated.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generated(String path, JsonElement element) {
        if (!path.endsWith(".json")) {
            path += ".json";
        }

        File generated = new File(SlimefunServerEssentials.getInstance().getDataFolder(), "generated");
        File file = new File(generated, path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Could not create file: " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try(FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            GSON.toJson(element, bufferedWriter);
            SlimefunServerEssentials.getInstance().getLogger().info("Generated: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        if (id.lastIndexOf(':') == -1) {
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
            return Integer.parseInt(id.substring(id.lastIndexOf(':')));
        } catch (NumberFormatException ignored) {}
        return 1;
    }

    public static List<String> process(List<ItemStack> complexStacks, List<ItemStack> process) {
        final List<String> processed = new ArrayList<>();
        for (ItemStack itemStack : process) {
            processed.add(process(complexStacks, itemStack));
        }
        return processed;
    }

    public static List<String> process(List<ItemStack> complexStacks, ItemStack[] process) {
        final List<String> processed = new ArrayList<>();
        for (ItemStack itemStack : process) {
            processed.add(process(complexStacks, itemStack));
        }
        return processed;
    }

    public static String process(List<ItemStack> complexStacks, ItemStack itemStack) {
        return process(complexStacks, itemStack, 1);
    }

    public static String process(List<ItemStack> complexStacks, ItemStack itemStack, float chance) {
        if (itemStack == null) {
            return "";
        }

        boolean complex = false;
        final StringBuilder processed = new StringBuilder();
        final String slimefunId = Slimefun.getItemDataService().getItemData(itemStack).orElse("");
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (!slimefunId.isBlank() && !(slimefunItem instanceof VanillaItem) && !(itemStack instanceof ComplexItem)) {
            processed.append(slimefunId);
        } else if (itemStack instanceof ComplexItem || (slimefunItem == null && !itemStack.isSimilar(new ItemStack(itemStack.getType())))) {
            int index = complexStacks.contains(itemStack) ? complexStacks.indexOf(itemStack) : complexStacks.size();
            processed.append('?').append(index);
            if (!complexStacks.contains(itemStack)) {
                complexStacks.add(itemStack);
            }
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
                final int sfTicksCompare = Integer.compare(getInt(json1, "sf_ticks", 1), getInt(json2, "sf_ticks", 1));
                if (sfTicksCompare != 0) {
                    return sfTicksCompare;
                }
                final int ticksCompare = Integer.compare(getInt(json1, "ticks", 1), getInt(json2, "ticks", 1));
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

    public static void addArray(JsonObject jsonObject, String key, List<String> array) {
        final JsonArray jsonArray = new JsonArray();
        for (String element : array) {
            jsonArray.add(element);
        }
        addArray(jsonObject, key, jsonArray);
    }

    public static void addArray(JsonObject jsonObject, String key, JsonArray array) {
        removeWhitespace(array);
        if (!array.isEmpty()) {
            jsonObject.add(key, array.size() == 1 ? array.get(0) : array);
        }
    }

    public static void addRecipeWithOptimize(List<RecipeBuilder> recipes, RecipeBuilder recipe) {
        recipe.removeWhitespace();
        final OptimizedRecipe optimizedRecipe = optimizeRecipeInputs(recipes, recipe);
        if (optimizedRecipe != null) {
            recipes.set(optimizedRecipe.index, optimizedRecipe.recipe);
        } else {
            recipes.add(recipe);
        }
    }

    private static OptimizedRecipe optimizeRecipeInputs(List<RecipeBuilder> recipes, RecipeBuilder recipe1) {
        for (int index = 0; index < recipes.size(); index++) {
            RecipeBuilder recipe2 = recipes.get(index);
            boolean canMerge = recipe1.getComplex().equals(recipe2.getComplex())
                    && recipe1.getInputs().size() == recipe2.getInputs().size()
                    && recipe1.getOutputs().equals(recipe2.getOutputs())
                    && recipe1.getLabels().equals(recipe2.getLabels())
                    && Objects.equals(recipe1.getSfTicks(), recipe2.getSfTicks())
                    && Objects.equals(recipe1.getTicks(), recipe2.getTicks())
                    && Objects.equals(recipe1.getEnergy(), recipe2.getEnergy());

            if (!canMerge) {
                continue;
            }

            boolean singleDifference = false;
            // Then we go through inputs, we can allow for a single difference as those are what will be merged
            for (int inputIndex = 0; inputIndex < recipe1.getInputs().size(); inputIndex++) {
                if (!canMerge) {
                    break;
                }

                final String input1 = recipe1.getInputs().get(inputIndex);
                final String input2 = recipe2.getInputs().get(inputIndex);

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
            final RecipeBuilder recipe3 = new RecipeBuilder()
                    .complex(recipe1.getComplex())
                    .addOutputs(recipe1.getOutputs())
                    .addLabels(recipe1.getLabels())
                    .sfTicks(recipe1.getSfTicks())
                    .ticks(recipe1.getTicks())
                    .energy(recipe1.getEnergy());

            for (int inputIndex = 0; inputIndex < recipe1.getInputs().size(); inputIndex++) {
                final String inputElement1 = recipe1.getInputs().get(inputIndex);
                final String inputElement2 = recipe2.getInputs().size() - 1 < inputIndex ? "" : recipe2.getInputs().get(inputIndex);
                if (inputElement1.equals(inputElement2)) {
                    recipe3.input(inputElement1);
                } else {
                    recipe3.input(inputElement1 + "," + inputElement2);
                }
            }
            return new OptimizedRecipe(index, recipe3);
        }
        return null;
    }

    private record OptimizedRecipe(int index, RecipeBuilder recipe) {}
}
