package me.justahuman.slimefun_server_essentials.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.justahuman.slimefun_server_essentials.SlimefunServerEssentials;
import me.justahuman.slimefun_server_essentials.implementation.core.DefaultDisplays;
import me.justahuman.slimefun_server_essentials.util.DataUtils;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategoryBuilder {
    @Getter private final String id;
    private String display = null;
    private ItemStack item = null;
    private int speed = -1;
    private int energy = -1;
    private final List<RecipeBuilder> recipes = new ArrayList<>();
    private String copy = null;

    public RecipeCategoryBuilder(String id) {
        this.id = id;
    }

    public void item(ItemStack item) {
        this.item = item;
    }

    public void speed(int speed) {
        this.speed = speed;
    }

    public void energy(int energy) {
        this.energy = energy;
    }

    public void display(DefaultDisplays display) {
        this.display = display.id();
    }

    public void display(String display) {
        this.display = display;
    }

    public void copy(String copy) {
        this.copy = copy;
    }

    public void recipe(RecipeBuilder recipe) {
        JsonUtils.addRecipeWithOptimize(this.recipes, recipe);
    }

    public void sort() {
        recipes.sort((r1, r2) -> {
            final int sfTicksCompare = Integer.compare(r1.getSfTicks() == null ? 1 : 0, r2.getSfTicks() == null ? 1 : 0);
            if (sfTicksCompare != 0) {
                return sfTicksCompare;
            }
            final int ticksCompare = Integer.compare(r1.getTicks() == null ? 1 : 0, r2.getTicks() == null ? 1 : 0);
            if (ticksCompare != 0) {
                return ticksCompare;
            }
            final int inputsCompare = Integer.compare(r1.getInputs().size(), r2.getInputs().size());
            if (inputsCompare != 0) {
                return inputsCompare;
            }
            return Integer.compare(r1.getOutputs().size(), r2.getOutputs().size());
        });
    }

    public void tryOptimize(Iterable<RecipeCategoryBuilder> categories) {
        if (isEmpty()) {
            return;
        }

        for (RecipeCategoryBuilder category : categories) {
            if (category == this || this.recipes.size() != category.recipes.size()) {
                continue;
            } else if ((category.speed == -1 || this.speed == -1) && category.speed != this.speed) {
                continue;
            } else if (category.speed < this.speed) {
                continue;
            }

            boolean canCopy = true;
            for (int i = 0; i < this.recipes.size(); i++) {
                RecipeBuilder theirRecipe = category.recipes.get(i);
                RecipeBuilder ourRecipe = this.recipes.get(i);
                if (!theirRecipe.equals(ourRecipe) && !theirRecipe.sped(category.speed).equals(ourRecipe.sped(category.speed))) {
                    canCopy = false;
                    break;
                }
            }

            if (canCopy) {
                SlimefunServerEssentials.getInstance().getLogger().info("Optimizing recipe category " + category.id + " by copying from " + this.id);
                category.copy = this.id;
                category.recipes.clear();
            }
        }
    }

    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    public void toBytes(ByteArrayDataOutput output) {
        output.writeUTF(this.id);
        DataUtils.add(output, this.display, null);
        output.writeBoolean(this.item != null);
        if (this.item != null) {
            output.write(DataUtils.bytes(this.item));
        }
        DataUtils.add(output, this.speed, -1);
        DataUtils.add(output, this.energy, -1);
        output.writeInt(this.recipes.size());
        for (RecipeBuilder recipe : this.recipes) {
            recipe.toBytes(output);
        }
        DataUtils.add(output, this.copy, null);
    }

    public JsonObject toJson() {
        JsonObject category = new JsonObject();
        if (display != null) {
            category.addProperty("display", this.display);
        }
        if (item != null) {
            category.add("item", JsonUtils.serializeItem(item));
        }
        if (speed != -1) {
            category.addProperty("speed", this.speed);
        }
        if (energy != -1) {
            category.addProperty("energy", this.energy);
        }
        JsonArray recipes = new JsonArray();
        for (RecipeBuilder recipe : this.recipes) {
            recipes.add(recipe.toJson());
        }
        JsonUtils.sortJsonArray(recipes);
        category.add("recipes", recipes.size() == 1 ? recipes.get(0) : recipes);
        if (copy != null) {
            category.addProperty("copy", this.copy);
        }
        return category;
    }

    public static void optimize(Iterable<RecipeCategoryBuilder> categories) {
        for (RecipeCategoryBuilder category : categories) {
            category.sort();
        }
        for (RecipeCategoryBuilder category : categories) {
            category.tryOptimize(categories);
        }
    }
}
