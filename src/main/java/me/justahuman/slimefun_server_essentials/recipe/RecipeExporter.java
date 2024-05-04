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
        final JsonArray recipes1 = JsonUtils.getArray(category1, "recipes", null);
        if (recipes1 == null) {
            return null;
        }

        for (String key : categories.keySet()) {
            final JsonObject category2 = JsonUtils.getObjectOrDefault(categories, key, null);
            if (category2 == null) {
                continue;
            }

            final JsonArray recipes2 = JsonUtils.getArray(category2, "recipes", null);
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

                if (!Objects.equals(JsonUtils.getArray(recipe1, "inputs", null), JsonUtils.getArray(recipe2, "inputs", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getArray(recipe1, "outputs", null), JsonUtils.getArray(recipe2, "outputs", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getArray(recipe1, "labels", null), JsonUtils.getArray(recipe2, "labels", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getInt(recipe1, "energy", null), JsonUtils.getInt(recipe2, "energy", null))) {
                    continue;
                }

                if (!Objects.equals(JsonUtils.getInt(recipe1, "time", 0) / 10, (JsonUtils.getInt(recipe2, "time", 0) / 10) / JsonUtils.getInt(category1, "speed", 1))) {
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

    public static void addParentCategory(JsonObject category, SlimefunItem slimefunItem) {
        final JsonArray recipes = JsonUtils.getArray(category, "recipes", new JsonArray());
        category.add("recipes", recipes);

        if (!category.has("item")) {
            category.add("item", JsonUtils.serializeItem(slimefunItem.getRecipeType().toItem()));
        }

        for (PluginHook hook : Hooks.HOOKS) {
            if (hook.handlesParent(slimefunItem)) {
                hook.handleParent(category, recipes, slimefunItem);
                return;
            }
        }

        category.addProperty("type", "grid3");
        addRecipeWithOptimize(recipes, new RecipeBuilder().inputs(slimefunItem.getRecipe()).output(slimefunItem.getRecipeOutput()));
    }

    public static JsonObject getCategory(SlimefunItem slimefunItem) {
        final JsonObject category = new JsonObject();
        final JsonArray recipes = new JsonArray();

        PluginHook matchedHook = null;
        for (PluginHook hook : Hooks.HOOKS) {
            if (hook.handles(slimefunItem) || hook.getSpecialCases().contains(slimefunItem.getId())) {
                matchedHook = hook;
                break;
            }
        }

        if (matchedHook != null) {
            matchedHook.handle(category, recipes, slimefunItem);
        } else if (slimefunItem instanceof CraftingBlock || slimefunItem instanceof MachineBlock) {
            final InfinityLibBlock wrappedBlock = InfinityLibBlock.wrap(slimefunItem);
            for (InfinityLibBlock.Recipe recipe : wrappedBlock.recipes()) {
                addRecipeWithOptimize(recipes, new RecipeBuilder()
                        .inputs(recipe.inputs())
                        .outputs(recipe.outputs())
                        .sfTicks(recipe.time()));
            }

            if (wrappedBlock.energy() != null) {
                category.addProperty("energy", wrappedBlock.energy());
            }

            final String type = fromLayout(slimefunItem);
            if (type != null) {
                category.addProperty("type", type);
            }
        } else if (slimefunItem instanceof MultiBlockMachine multiBlock) {
            // Slimefun saves it as Input, Output, Input, Output, So we skip on the Outputs
            ItemStack[] inputs = null;
            final List<ItemStack[]> multiBlockRecipes = multiBlock.getRecipes();
            for (ItemStack[] outputs : multiBlockRecipes) {
                if (inputs == null) {
                    inputs = outputs;
                    continue;
                }
                addRecipeWithOptimize(recipes, new RecipeBuilder().inputs(inputs).outputs(outputs));
                inputs = null;
            }
            category.addProperty("type", "grid3");
        } else if (slimefunItem instanceof AncientAltar altar) {
            for (AltarRecipe altarRecipe : altar.getRecipes()) {
                final List<ItemStack> i = altarRecipe.getInput();
                // Slimefun Ancient Altar Recipes are put in this Strange Order, don't ask me Why
                final ItemStack[] inputs = new ItemStack[] { i.get(0), i.get(1), i.get(2), i.get(7), altarRecipe.getCatalyst(), i.get(3), i.get(6), i.get(5), i.get(4) };
                // An Ancient Altar Task goes through 36 "stages" before completion, the delay between each is 8 ticks.
                addRecipeWithOptimize(recipes, new RecipeBuilder().ticks(36 * 8).inputs(inputs).output(altarRecipe.getOutput()));
            }
            category.addProperty("type", "ancient_altar");
        } else if (slimefunItem instanceof AbstractEnergyProvider provider) {
            final Set<MachineFuel> fuelTypes = provider.getFuelTypes();
            for (MachineFuel machineFuel : fuelTypes) {
                final List<ItemStack> inputs = new ArrayList<>(List.of(machineFuel.getInput()));
                if (slimefunItem instanceof Reactor reactor && reactor.getCoolant() != null) {
                    for (int count = (int) Math.ceil(machineFuel.getTicks() / 50D); count != 0; count -= Math.min(count, 64)) {
                        inputs.add(new CustomItemStack(reactor.getCoolant(), Math.min(count, 64)));
                    }
                }
                addRecipeWithOptimize(recipes, new RecipeBuilder().sfTicks(machineFuel.getTicks()).inputs(inputs).output(machineFuel.getOutput()));
            }

            if (slimefunItem instanceof Reactor) {
                category.addProperty("type", "reactor");
            }
            category.addProperty("energy", provider.getEnergyProduction());
        } else if (slimefunItem instanceof SolarGenerator generator) {
            addRecipeWithOptimize(recipes, new RecipeBuilder().sfTicks(1).energy(generator.getDayEnergy()).label("day"));
            addRecipeWithOptimize(recipes, new RecipeBuilder().sfTicks(1).energy(generator.getNightEnergy()).label("night"));
        } else if (slimefunItem instanceof AContainer container) {
            for (MachineRecipe machineRecipe : container.getMachineRecipes()) {
                final ItemStack[] inputs = machineRecipe.getInput();
                final ItemStack[] outputs = machineRecipe.getOutput();
                addRecipeWithOptimize(recipes, new RecipeBuilder().sfTicks(machineRecipe.getTicks()).inputs(inputs).outputs(outputs));
            }

            if (slimefunItem instanceof ElectricSmeltery) {
                category.addProperty("type", "smeltery");
            }
            category.addProperty("speed", container.getSpeed());
            category.addProperty("energy", -container.getEnergyConsumption());
        } else if (slimefunItem instanceof RecipeDisplayItem item) {
            exportDisplayRecipes(item, recipes);
        }

        if (!recipes.isEmpty()) {
            JsonUtils.sortJsonArray(recipes);
            category.add("recipes", recipes.size() == 1
                    ? recipes.get(0)
                    : recipes);
        }

        return category;
    }

    public static void exportDisplayRecipes(RecipeDisplayItem item, JsonArray recipes) {
        ItemStack input = null;
        final List<ItemStack> displayRecipes = item.getDisplayRecipes();
        for (ItemStack output : displayRecipes) {
            if (input == null) {
                input = output;
                continue;
            }

            addRecipeWithOptimize(recipes, new RecipeBuilder().input(input).output(output));
            input = null;
        }
    }

    public static void addRecipeWithOptimize(JsonArray recipes, RecipeBuilder builder) {
        final JsonObject recipeObject = builder.build();
        JsonUtils.removeWhitespace(recipeObject);
        final Pair<Integer, JsonObject> optimizedRecipe = optimizeRecipe(recipes, recipeObject);
        if (optimizedRecipe != null) {
            recipes.set(optimizedRecipe.getFirstValue(), optimizedRecipe.getSecondValue());
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
    public static Pair<Integer, JsonObject> optimizeRecipe(JsonArray recipes, JsonObject recipe1) {
        final JsonArray complex1 = JsonUtils.getArray(recipe1, "complex", new JsonArray());
        final JsonArray inputs1 = JsonUtils.getArray(recipe1, "inputs", new JsonArray());
        final JsonArray outputs1 = JsonUtils.getArray(recipe1, "outputs", new JsonArray());
        final JsonArray labels1 = JsonUtils.getArray(recipe1, "labels", new JsonArray());
        final Integer time1 = JsonUtils.getInt(recipe1, "time", null);
        final Integer energy1 = JsonUtils.getInt(recipe1, "energy", null);

        for (int index = 0; index < recipes.size(); index++) {
            // This should always evaluate to false this is just an instanceof cast
            if (!(recipes.get(index) instanceof JsonObject recipe2)) {
                continue;
            }

            final JsonArray complex2 = JsonUtils.getArray(recipe2, "complex", new JsonArray());
            final JsonArray inputs2 = JsonUtils.getArray(recipe2, "inputs", new JsonArray());
            final JsonArray outputs2 = JsonUtils.getArray(recipe2, "outputs", new JsonArray());
            final JsonArray labels2 = JsonUtils.getArray(recipe2, "labels", new JsonArray());
            final Integer time2 = JsonUtils.getInt(recipe2, "time", null);
            final Integer energy2 = JsonUtils.getInt(recipe1, "energy", null);

            if (!Objects.equals(time1, time2) || !Objects.equals(energy1, energy2)) {
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
                if (!canMerge && !singleDifference && JsonUtils.equalAmount(input1, input2)) {
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

            if (time1 != null) {
                recipe3.addProperty("time", time1);
            }

            JsonUtils.addArray(recipe3, "complex", complex3);
            JsonUtils.addArray(recipe3, "inputs", inputs3);
            JsonUtils.addArray(recipe3, "outputs", outputs3);
            JsonUtils.addArray(recipe3, "labels", labels3);
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
