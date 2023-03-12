package me.justahuman.recipe_exporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.tr7zw.nbtapi.NBTItem;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Utils {
    private static final Set<String> slimefunAddons = new HashSet<>();
    private static final Map<String, Set<ItemGroup>> itemGroups = new HashMap<>();
    private static final Map<String, Set<SlimefunItem>> slimefunItems = new HashMap<>();
    static {
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            final String addonName = slimefunItem.getAddon().getName();
            final Set<SlimefunItem> itemSet = slimefunItems.getOrDefault(addonName, new HashSet<>());
            itemSet.add(slimefunItem);
            slimefunItems.put(addonName, itemSet);
        }
        
        for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
            if (itemGroup instanceof FlexItemGroup) {
                continue;
            }
            
            final String addonName = itemGroup.getAddon().getName();
            final Set<ItemGroup> groupSet = itemGroups.getOrDefault(addonName, new LinkedHashSet<>());
            groupSet.add(itemGroup);
            itemGroups.put(addonName, groupSet);
        }
        
        slimefunAddons.addAll(slimefunItems.keySet());
    }
    
    public static boolean invalidSlimefunAddon(String addon) {
        return ! slimefunAddons.contains(addon);
    }
    
    public static Set<String> getSlimefunAddonNames() {
        return Collections.unmodifiableSet(slimefunAddons);
    }
    
    public static Map<String, Set<SlimefunItem>> getSlimefunItems() {
        return Collections.unmodifiableMap(slimefunItems);
    }
    
    public static Set<SlimefunItem> getSlimefunItems(String addon) {
        return Collections.unmodifiableSet(slimefunItems.getOrDefault(addon, new HashSet<>()));
    }
    
    public static List<SlimefunItem> getSortedSlimefunItems(String addon) {
        final List<SlimefunItem> sortedSlimefunItems = new ArrayList<>(getSlimefunItems(addon));
        sortedSlimefunItems.sort(Comparator.comparing(SlimefunItem::getId));
        return sortedSlimefunItems;
    }
    
    public static Map<String, Set<ItemGroup>> getItemGroups() {
        return Collections.unmodifiableMap(itemGroups);
    }
    
    public static void addCategoryWithOptimize(String key, JsonObject categoryObject, JsonObject rootObject) {
        final JsonObject optimizedCategory = optimizeCategory(rootObject.deepCopy(), categoryObject.deepCopy());
        rootObject.add(key, optimizedCategory == null ? categoryObject : optimizedCategory);
    }
    
    public static JsonObject optimizeCategory(JsonObject categories, JsonObject category1) {
        final JsonArray recipes1 = getArrayOrDefault(category1, "recipes", null);
        if (recipes1 == null) {
            return null;
        }
        
        for (String key : categories.keySet()) {
            final JsonObject category2 = getObjectOrDefault(categories, key, null);
            if (category2 == null) {
                continue;
            }
    
            final JsonArray recipes2 = getArrayOrDefault(category2, "recipes", null);
            if (recipes2 == null) {
                continue;
            }
            
            if (recipes1.size() != recipes2.size()) {
                continue;
            }
            
            boolean canCopy = false;
            for (int i = 0; i < recipes1.size(); i++) {
                if (!(recipes1.get(i) instanceof JsonObject recipe1) || !(recipes2.get(i) instanceof JsonObject recipe2)) {
                    continue;
                }
                
                if (!Objects.equals(getArrayOrDefault(recipe1, "inputs", null), getArrayOrDefault(recipe2, "inputs", null))) {
                    continue;
                }
    
                if (!Objects.equals(getArrayOrDefault(recipe1, "outputs", null), getArrayOrDefault(recipe2, "outputs", null))) {
                    continue;
                }
    
                if (!Objects.equals(getArrayOrDefault(recipe1, "labels", null), getArrayOrDefault(recipe2, "labels", null))) {
                    continue;
                }
    
                if (!Objects.equals(getIntegerOrDefault(recipe1, "energy", null), getIntegerOrDefault(recipe2, "energy", null))) {
                    continue;
                }
                
                if (!Objects.equals(getIntegerOrDefault(recipe1, "time", 0) / 10, (getIntegerOrDefault(recipe2, "time", 0) / 10) / getIntegerOrDefault(category1, "speed", 1))) {
                    continue;
                }
                
                canCopy = true;
            }
            
            if (!canCopy) {
                continue;
            }
            
            final JsonObject optimizedCategory = category1.deepCopy();
            optimizedCategory.remove("recipes");
            optimizedCategory.addProperty("copy", key);
            return optimizedCategory;
        }
        return null;
    }
    
    public static JsonObject getCategory(SlimefunItem slimefunItem) {
        final JsonObject categoryObject = new JsonObject();
        final JsonArray recipesArray = new JsonArray();
    
        if (slimefunItem instanceof MultiBlockMachine multiBlockMachine) {
            final List<ItemStack[]> recipes = multiBlockMachine.getRecipes();
            for (ItemStack[] inputs : recipes) {
                // Slimefun saves it as Input, Output, Input, Output, So we skip on the Outputs
                final int index = recipes.indexOf(inputs);
                if (index % 2 != 0 || recipes.size() - 1 < index + 1) {
                    continue;
                }
            
                final ItemStack[] outputs = recipes.get(index + 1);
            
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().inputs(inputs).outputs(outputs).build());
            }
        } else if (slimefunItem instanceof AncientAltar ancientAltar) {
            for (AltarRecipe altarRecipe : ancientAltar.getRecipes()) {
                final List<ItemStack> altarInputs = altarRecipe.getInput();
                // Slimefun Ancient Altar Recipes are put in this Strange Order, don't ask me Why
                final ItemStack[] inputs = new ItemStack[] {
                        altarInputs.get(0),
                        altarInputs.get(1),
                        altarInputs.get(2),
                        altarInputs.get(7),
                        altarRecipe.getCatalyst(),
                        altarInputs.get(3),
                        altarInputs.get(6),
                        altarInputs.get(5),
                        altarInputs.get(4)
                };
                final ItemStack[] outputs = new ItemStack[] { altarRecipe.getOutput() };
                // An Ancient Altar Task goes through 36 "stages" before completion, the delay between each is 8 ticks.
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().time(36 * 8).inputs(inputs).outputs(outputs).build());
            }
        } else if (slimefunItem instanceof AbstractEnergyProvider abstractEnergyProvider) {
            final Set<MachineFuel> abstractEnergyProviderFuelTypes = abstractEnergyProvider.getFuelTypes();
            for (MachineFuel machineFuel : abstractEnergyProviderFuelTypes) {
                final ItemStack[] inputs = new ItemStack[] { machineFuel.getInput() };
                final ItemStack[] outputs = new ItemStack[] { machineFuel.getOutput() };
                // We have to Multiply this by 10 here as this is in Slimefun Ticks and not Minecraft Ticks
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().time(machineFuel.getTicks() * 10).inputs(inputs).outputs(outputs).build());
            }
            
            if (slimefunItem instanceof AGenerator aGenerator) {
                categoryObject.addProperty("energy", aGenerator.getEnergyProduction());
            }
        } else if (slimefunItem instanceof SolarGenerator solarGenerator) {
            addRecipeWithOptimize(recipesArray, new RecipeBuilder().time(10).energy(solarGenerator.getDayEnergy()).label("day").build());
            addRecipeWithOptimize(recipesArray, new RecipeBuilder().time(10).energy(solarGenerator.getNightEnergy()).label("night").build());
        } else if (slimefunItem instanceof AContainer aContainer) {
            for (MachineRecipe machineRecipe : aContainer.getMachineRecipes()) {
                final ItemStack[] inputs = machineRecipe.getInput();
                final ItemStack[] outputs = machineRecipe.getOutput();
                // We have to Multiply this by 10 here as this is in Slimefun Ticks and not Minecraft Ticks
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().time(machineRecipe.getTicks() * 10).inputs(inputs).outputs(outputs).build());
            }
    
            categoryObject.addProperty("speed", aContainer.getSpeed());
            categoryObject.addProperty("energy", -aContainer.getEnergyConsumption());
        } else if (slimefunItem instanceof RecipeDisplayItem recipeDisplayItem) {
            final List<ItemStack> recipes = recipeDisplayItem.getDisplayRecipes();
            for (ItemStack input : recipes) {
                // Slimefun saves it as Input, Output, Input, Output, So we skip on the Outputs
                final int index = recipes.indexOf(input);
                if (index % 2 != 0 || recipes.size() - 1 < index + 1) {
                    continue;
                }
        
                final ItemStack output = recipes.get(index + 1);
        
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().inputs(new ItemStack[] {input}).outputs(new ItemStack[] {output}).build());
            }
        }
        
        if (!recipesArray.isEmpty()) {
            sortJsonArray(recipesArray);
            categoryObject.add("recipes", recipesArray);
        }
        
        return categoryObject;
    }
    
    public static void addRecipeWithOptimize(JsonArray recipesArray, JsonObject recipeObject) {
        removeWhitespace(recipeObject);
        final Pair<Integer, JsonObject> optimizedRecipe = optimizeRecipe(recipesArray.deepCopy(), recipeObject.deepCopy());
        if (optimizedRecipe != null) {
            recipesArray.set(optimizedRecipe.getFirstValue(), optimizedRecipe.getSecondValue());
        } else {
            recipesArray.add(recipeObject);
        }
    }
    
    /**
     * Attempts to optimize a Recipe {@link JsonObject} by merging it with another Recipe {@link JsonObject}
     * @param recipe1 The Recipe {@link JsonObject} to optimize
     * @param recipes The Recipes {@link JsonArray} to search for a match
     * @return {@link Pair} with the {@link Pair#getFirstValue()} being the index and the {@link Pair#getSecondValue()} being the optimized Recipe {@link JsonObject}
     */
    public static Pair<Integer, JsonObject> optimizeRecipe(JsonArray recipes, JsonObject recipe1) {
        final JsonArray inputs1 = getArrayOrDefault(recipe1, "inputs", new JsonArray());
        final JsonArray outputs1 = getArrayOrDefault(recipe1, "outputs", new JsonArray());
        final JsonArray labels1 = getArrayOrDefault(recipe1, "labels", new JsonArray());
        final Integer time1 = getIntegerOrDefault(recipe1, "time", null);
        final Integer energy1 = getIntegerOrDefault(recipe1, "energy", null);
        
        for (int index = 0; index < recipes.size(); index++) {
            // This should always evaluate to false this is just an instanceof cast
            if (!(recipes.get(index) instanceof JsonObject recipe2)) {
                continue;
            }
            
            final JsonArray inputs2 = getArrayOrDefault(recipe2, "inputs", new JsonArray());
            final JsonArray outputs2 = getArrayOrDefault(recipe2, "outputs", new JsonArray());
            final JsonArray labels2 = getArrayOrDefault(recipe2, "labels", new JsonArray());
            final Integer time2 = getIntegerOrDefault(recipe2, "time", null);
            final Integer energy2 = getIntegerOrDefault(recipe1, "energy", null);
            
            if (!Objects.equals(time1, time2) || !Objects.equals(energy1, energy2)) {
                continue;
            }
            
            boolean canMerge = outputs1.equals(outputs2) && labels1.equals(labels2);
            
            if (!canMerge) {
                continue;
            }
            
            boolean singleDifference = false;
            // Then we go through inputs, we can allow for a single difference as those are what will be merged
            for (int inputIndex = 0; inputIndex < inputs1.size(); inputIndex++) {
                if (!canMerge) {
                    break;
                }
                
                final JsonElement inputElement1 = inputs1.get(inputIndex);
                final JsonElement inputElement2 = inputs2.size() - 1 < inputIndex ? new JsonPrimitive("") : inputs2.get(inputIndex);
                
                canMerge = inputElement1.equals(inputElement2);
                // We can allow for a single difference in the Inputs as that is the Point of Merging
                if (!canMerge && !singleDifference && equalAmount(inputElement1, inputElement2)) {
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
            final JsonArray outputs3 = outputs1.deepCopy();
            final JsonArray labels3 = labels1.deepCopy();
            
            for (int inputIndex = 0; inputIndex < inputs1.size(); inputIndex++) {
                final JsonElement inputElement1 = inputs1.get(inputIndex);
                final JsonElement inputElement2 = inputs2.size() - 1 < inputIndex ? new JsonPrimitive("") : inputs2.get(inputIndex);
                if (inputElement1.equals(inputElement2)) {
                    inputs3.add(inputElement1);
                } else {
                    final JsonArray inputArray3 = new JsonArray();
                    addElementToArray(inputArray3, inputElement1);
                    addElementToArray(inputArray3, inputElement2);
                    sortJsonArray(inputArray3);
                    inputs3.add(inputArray3);
                }
            }
            
            if (time1 != null) {
                recipe3.addProperty("time", time1);
            }
            
            if (!inputs3.isEmpty()) {
                recipe3.add("inputs", inputs3);
            }
            
            if (!outputs3.isEmpty()) {
                recipe3.add("outputs", outputs3);
            }
            
            if (!labels3.isEmpty()) {
                recipe3.add("labels", labels3);
            }
            
            return new Pair<>(index, recipe3);
        }
        return null;
    }
    
    private static JsonObject getObjectOrDefault(JsonObject jsonObject, String key, JsonObject defaultValue) {
        return jsonObject.get(key) instanceof JsonObject otherObject ? otherObject : defaultValue;
    }
    
    private static JsonArray getArrayOrDefault(JsonObject jsonObject, String key, JsonArray defaultValue) {
        return jsonObject.get(key) instanceof JsonArray jsonArray ? jsonArray : defaultValue;
    }
    
    private static String getStringOrDefault(JsonObject jsonObject, String key, String defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString() ? jsonPrimitive.getAsString() : defaultValue;
    }
    
    private static Boolean getBooleanOrDefault(JsonObject jsonObject, String key, Boolean defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isBoolean() ? jsonPrimitive.getAsBoolean() : defaultValue;
    }
    
    private static Long getLongOrDefault(JsonObject jsonObject, String key, Long defaultValue) {
        return jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isNumber() ? jsonPrimitive.getAsLong() : defaultValue;
    }
    
    private static Integer getIntegerOrDefault(JsonObject jsonObject, String key, Integer defaultValue) {
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
    
    public static JsonArray processList(ItemStack[] process) {
        final JsonArray processed = new JsonArray();
        for (ItemStack item : process) {
            processed.add(process(item));
        }
        return processed;
    }
    
    public static String process(ItemStack process) {
        final StringBuilder processed = new StringBuilder();
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(process);
        if (slimefunItem != null && !(slimefunItem instanceof VanillaItem)) {
            processed.append(slimefunItem.getId()).append(":").append(process.getAmount());
        } else if (process != null) {
            if (process.getType() == Material.EXPERIENCE_BOTTLE && process.hasItemMeta() && process.getItemMeta().hasDisplayName()) {
                final SlimefunItem flaskOfKnowledgeItem = SlimefunItems.FLASK_OF_KNOWLEDGE.getItem();
                processed.append("FILLED_").append(flaskOfKnowledgeItem.getId()).append(":").append(process.getAmount());
            } else {
                processed.append(process.getType().name().toLowerCase()).append(":").append(process.getAmount());
            }
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
            if (jsonArray.get(i) instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.isString() && jsonPrimitive.getAsString().equals("")) {
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
    
    public static JsonObject serializeItemGroup(Player player, ItemGroup itemGroup) {
        final JsonObject groupObject = new JsonObject();
        final JsonArray stacksArray = new JsonArray();
        for (SlimefunItem slimefunItem : itemGroup.getItems()) {
            stacksArray.add(slimefunItem.getId());
        }
        groupObject.add("icon", serializeItem(itemGroup.getItem(player)));
        groupObject.add("stacks", stacksArray);
        return groupObject;
    }
    
    public static void addElementToArray(JsonArray jsonArray, JsonElement jsonElement) {
        if (jsonElement instanceof JsonArray array) {
            jsonArray.addAll(array);
        } else if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
            jsonArray.add(jsonPrimitive);
        }
    }
    
    public static void log(String log) {
        RecipeExporter.getInstance().getLogger().info(log);
    }
}
