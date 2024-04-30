package me.justahuman.slimefun_server_essentials.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.mooy1.infinitylib.machines.CraftingBlock;
import io.github.mooy1.infinitylib.machines.MachineBlock;
import io.github.mooy1.infinitylib.machines.MachineLayout;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricSmeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.justahuman.slimefun_server_essentials.recipe.compat.PluginHook;
import me.justahuman.slimefun_server_essentials.recipe.compat.misc.InfinityLibBlock;
import me.justahuman.slimefun_server_essentials.util.Hooks;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import me.justahuman.slimefun_server_essentials.util.ReflectionUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RecipeExporter {
    public static String fromLayout(Object objectWithLayout) {
        final MachineLayout layout = ReflectionUtils.getField(objectWithLayout, "layout", MachineLayout.MACHINE_DEFAULT);
        if (layout == MachineLayout.CRAFTING_DEFAULT) {
            return "grid3";
        } else if (layout == MachineLayout.MACHINE_DEFAULT) {
            return null;
        } else {
            final int[] slots = layout.inputSlots();
            final int sqrt = (int) Math.sqrt(slots.length);
            return sqrt * sqrt == slots.length ? "grid" + sqrt : null;
        }
    }

    public static void addCategoryWithOptimize(String key, JsonObject categoryObject, JsonObject rootObject) {
        final JsonObject optimizedCategory = optimizeCategory(rootObject.deepCopy(), categoryObject.deepCopy());
        rootObject.add(key, optimizedCategory == null ? categoryObject : optimizedCategory);
    }

    public static JsonObject optimizeCategory(JsonObject categories, JsonObject category1) {
        final JsonArray recipes1 = JsonUtils.getArrayOrDefault(category1, "recipes", null);
        if (recipes1 == null) {
            return null;
        }

        for (String key : categories.keySet()) {
            final JsonObject category2 = JsonUtils.getObjectOrDefault(categories, key, null);
            if (category2 == null) {
                continue;
            }

            final JsonArray recipes2 = JsonUtils.getArrayOrDefault(category2, "recipes", null);
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

                if (!Objects.equals(JsonUtils.getArrayOrDefault(recipe1, "inputs", null), JsonUtils.getArrayOrDefault(recipe2, "inputs", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getArrayOrDefault(recipe1, "outputs", null), JsonUtils.getArrayOrDefault(recipe2, "outputs", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getArrayOrDefault(recipe1, "labels", null), JsonUtils.getArrayOrDefault(recipe2, "labels", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getIntegerOrDefault(recipe1, "energy", null), JsonUtils.getIntegerOrDefault(recipe2, "energy", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getIntegerOrDefault(recipe1, "time", 0) / 10, (JsonUtils.getIntegerOrDefault(recipe2, "time", 0) / 10) / JsonUtils.getIntegerOrDefault(category1, "speed", 1))) {
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

    public static void exportParentCategory(SlimefunItem slimefunItem, JsonObject categoryObject) {
        if (!categoryObject.has("item")) {
            categoryObject.add("item", JsonUtils.serializeItem(slimefunItem.getRecipeType().toItem()));
        }

        final JsonArray recipesArray = JsonUtils.getArrayOrDefault(categoryObject, "recipes", new JsonArray());
        addRecipeWithOptimize(recipesArray, new RecipeBuilder().inputs(slimefunItem.getRecipe()).output(slimefunItem.getRecipeOutput()));
        categoryObject.add("recipes", recipesArray);
    }

    public static JsonObject getCategory(SlimefunItem slimefunItem) {
        final JsonObject categoryObject = new JsonObject();
        final JsonArray recipesArray = new JsonArray();

        PluginHook matchedHook = null;
        for (PluginHook hook : Hooks.HOOKS) {
            if (hook.handles(slimefunItem) || hook.getSpecialCases().contains(slimefunItem.getId())) {
                matchedHook = hook;
                break;
            }
        }

        if (matchedHook != null) {
            matchedHook.handle(categoryObject, recipesArray, slimefunItem);
        } else if (slimefunItem instanceof CraftingBlock || slimefunItem instanceof MachineBlock) {
            final InfinityLibBlock wrappedBlock = InfinityLibBlock.wrap(slimefunItem);
            for (InfinityLibBlock.Recipe recipe : wrappedBlock.recipes()) {
                addRecipeWithOptimize(recipesArray, new RecipeBuilder()
                        .inputs(recipe.inputs())
                        .outputs(recipe.outputs())
                        .sfTicks(recipe.time()));
            }

            if (wrappedBlock.energy() != null) {
                categoryObject.addProperty("energy", wrappedBlock.energy());
            }

            final String type = fromLayout(slimefunItem);
            if (type != null) {
                categoryObject.addProperty("type", type);
            }
        } else if (slimefunItem instanceof MultiBlockMachine multiBlockMachine) {
            final List<ItemStack[]> recipes = multiBlockMachine.getRecipes();
            for (ItemStack[] inputs : recipes) {
                // Slimefun saves it as Input, Output, Input, Output, So we skip on the Outputs
                final int index = recipes.indexOf(inputs);
                if (index % 2 != 0 || recipes.size() - 1 < index + 1) {
                    continue;
                }

                addRecipeWithOptimize(recipesArray, new RecipeBuilder().inputs(inputs).outputs(recipes.get(index + 1)));
            }
            categoryObject.addProperty("type", "grid3");
        } else if (slimefunItem instanceof AncientAltar ancientAltar) {
            for (AltarRecipe altarRecipe : ancientAltar.getRecipes()) {
                final List<ItemStack> i = altarRecipe.getInput();
                // Slimefun Ancient Altar Recipes are put in this Strange Order, don't ask me Why
                final ItemStack[] inputs = new ItemStack[] { i.get(0), i.get(1), i.get(2), i.get(7), altarRecipe.getCatalyst(), i.get(3), i.get(6), i.get(5), i.get(4) };
                final ItemStack[] outputs = new ItemStack[] { altarRecipe.getOutput() };
                // An Ancient Altar Task goes through 36 "stages" before completion, the delay between each is 8 ticks.
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().ticks(36 * 8).inputs(inputs).outputs(outputs));
            }

            categoryObject.addProperty("type", "ancient_altar");
        } else if (slimefunItem instanceof AbstractEnergyProvider abstractEnergyProvider) {
            final Set<MachineFuel> abstractEnergyProviderFuelTypes = abstractEnergyProvider.getFuelTypes();
            for (MachineFuel machineFuel : abstractEnergyProviderFuelTypes) {
                final List<ItemStack> inputs = new ArrayList<>(List.of(machineFuel.getInput()));
                if (slimefunItem instanceof Reactor reactor && reactor.getCoolant() != null) {
                    for (int count = (int) Math.ceil(machineFuel.getTicks() / 50D); count != 0; count -= Math.min(count, 64)) {
                        inputs.add(new CustomItemStack(reactor.getCoolant(), Math.min(count, 64)));
                    }
                }

                addRecipeWithOptimize(recipesArray, new RecipeBuilder().sfTicks(machineFuel.getTicks()).inputs(inputs).output(machineFuel.getOutput()));
            }

            categoryObject.addProperty("energy", abstractEnergyProvider.getEnergyProduction());

            if (slimefunItem instanceof Reactor) {
                categoryObject.addProperty("type", "reactor");
            }
        } else if (slimefunItem instanceof SolarGenerator solarGenerator) {
            addRecipeWithOptimize(recipesArray, new RecipeBuilder().sfTicks(1).energy(solarGenerator.getDayEnergy()).label("day"));
            addRecipeWithOptimize(recipesArray, new RecipeBuilder().sfTicks(1).energy(solarGenerator.getNightEnergy()).label("night"));
        } else if (slimefunItem instanceof AContainer aContainer) {
            for (MachineRecipe machineRecipe : aContainer.getMachineRecipes()) {
                final ItemStack[] inputs = machineRecipe.getInput();
                final ItemStack[] outputs = machineRecipe.getOutput();
                addRecipeWithOptimize(recipesArray, new RecipeBuilder().sfTicks(machineRecipe.getTicks()).inputs(inputs).outputs(outputs));
            }

            categoryObject.addProperty("speed", aContainer.getSpeed());
            categoryObject.addProperty("energy", -aContainer.getEnergyConsumption());

            if (slimefunItem instanceof ElectricSmeltery) {
                categoryObject.addProperty("type", "smeltery");
            }
        } else if (slimefunItem instanceof RecipeDisplayItem recipeDisplayItem) {
            exportDisplayRecipes(recipeDisplayItem, recipesArray);
        }

        if (!recipesArray.isEmpty()) {
            JsonUtils.sortJsonArray(recipesArray);
            categoryObject.add("recipes", recipesArray);
        }

        return categoryObject;
    }

    private static void exportDisplayRecipes(RecipeDisplayItem recipeDisplayItem, JsonArray recipesArray) {
        final List<ItemStack> recipes = recipeDisplayItem.getDisplayRecipes();
        for (ItemStack input : recipes) {
            // Slimefun saves it as Input, Output, Input, Output, So we skip on the Outputs
            final int index = recipes.indexOf(input);
            if (index % 2 != 0 || recipes.size() - 1 < index + 1) {
                continue;
            }

            final ItemStack output = recipes.get(index + 1);

            addRecipeWithOptimize(recipesArray, new RecipeBuilder().input(input).output(output));
        }
    }

    public static void addRecipeWithOptimize(JsonArray recipesArray, RecipeBuilder builder) {
        final JsonObject recipeObject = builder.build();
        JsonUtils.removeWhitespace(recipeObject);
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
        final JsonArray inputs1 = JsonUtils.getArrayOrDefault(recipe1, "inputs", new JsonArray());
        final JsonArray outputs1 = JsonUtils.getArrayOrDefault(recipe1, "outputs", new JsonArray());
        final JsonArray labels1 = JsonUtils.getArrayOrDefault(recipe1, "labels", new JsonArray());
        final Integer time1 = JsonUtils.getIntegerOrDefault(recipe1, "time", null);
        final Integer energy1 = JsonUtils.getIntegerOrDefault(recipe1, "energy", null);

        for (int index = 0; index < recipes.size(); index++) {
            // This should always evaluate to false this is just an instanceof cast
            if (!(recipes.get(index) instanceof JsonObject recipe2)) {
                continue;
            }

            final JsonArray inputs2 = JsonUtils.getArrayOrDefault(recipe2, "inputs", new JsonArray());
            final JsonArray outputs2 = JsonUtils.getArrayOrDefault(recipe2, "outputs", new JsonArray());
            final JsonArray labels2 = JsonUtils.getArrayOrDefault(recipe2, "labels", new JsonArray());
            final Integer time2 = JsonUtils.getIntegerOrDefault(recipe2, "time", null);
            final Integer energy2 = JsonUtils.getIntegerOrDefault(recipe1, "energy", null);

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
                if (!canMerge && !singleDifference && JsonUtils.equalAmount(inputElement1, inputElement2)) {
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
                    JsonUtils.addElementToArray(inputArray3, inputElement1);
                    JsonUtils.addElementToArray(inputArray3, inputElement2);
                    JsonUtils.sortJsonArray(inputArray3);
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

    public static String getLabelName(World.Environment environment) {
        return switch (environment) {
            case NORMAL, CUSTOM -> "overworld";
            case NETHER -> "the_nether";
            case THE_END -> "the_end";
        };
    }

    public static String getLabelName(Biome biome) {
        return "biome:" + biome.getKey().getKey();
    }
}
