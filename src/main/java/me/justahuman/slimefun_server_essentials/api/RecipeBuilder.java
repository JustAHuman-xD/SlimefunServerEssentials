package me.justahuman.slimefun_server_essentials.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.implementation.core.DefaultLabels;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeBuilder {
    private Integer time = null;
    private Integer energy = null;
    private final JsonArray complex = new JsonArray();
    private final JsonArray inputs = new JsonArray();
    private final JsonArray outputs = new JsonArray();
    private final JsonArray labels = new JsonArray();

    public RecipeBuilder sfTicks(Integer ticks) {
        return ticks == null ? this : ticks(ticks * 10);
    }

    public RecipeBuilder ticks(Integer ticks) {
        this.time = ticks;
        return this;
    }

    public RecipeBuilder energy(Integer energy) {
        this.energy = energy;
        return this;
    }
    
    public RecipeBuilder inputs(List<ItemStack> inputs) {
        return inputs(JsonUtils.process(this.complex, inputs));
    }
    
    public RecipeBuilder inputs(ItemStack[] inputs) {
        return inputs(JsonUtils.process(this.complex, inputs));
    }

    public RecipeBuilder input(ItemStack itemStack) {
        return input(itemStack, 1);
    }

    public RecipeBuilder complexInput(ItemStack itemStack) {
        return input(new ComplexItem(itemStack));
    }

    public RecipeBuilder input(Material material) {
        return input(material, 1);
    }

    public RecipeBuilder input(Material material, int amount) {
        return input(new ItemStack(material, amount));
    }

    public RecipeBuilder input(ItemStack itemStack, float consumptionChance) {
        return input(JsonUtils.process(this.complex, itemStack, consumptionChance));
    }

    public RecipeBuilder input(EntityType entityType, boolean baby, int amount, float consumptionChance) {
        return input("@" + (baby ? "baby_" : "") + entityType.getKey().getKey() + ":" + amount + "%" + consumptionChance);
    }

    public RecipeBuilder input(Fluid fluid, int amount) {
        return input("~" + fluid.getKey().getKey() + ":" + amount);
    }

    public RecipeBuilder input(String input) {
        this.inputs.add(input);
        return this;
    }

    private RecipeBuilder inputs(JsonArray inputs) {
        this.inputs.addAll(inputs);
        return this;
    }
    
    public RecipeBuilder outputs(List<ItemStack> outputs) {
        return outputs(JsonUtils.process(this.complex, outputs));
    }
    
    public RecipeBuilder outputs(ItemStack[] outputs) {
        return outputs(JsonUtils.process(this.complex, outputs));
    }
    
    private RecipeBuilder outputs(JsonArray outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public RecipeBuilder output(Material material) {
        return output(material, 1);
    }

    public RecipeBuilder output(Material material, int amount) {
        return output(new ItemStack(material, amount));
    }

    public RecipeBuilder output(ItemStack itemStack) {
        return output(itemStack, 1);
    }

    public RecipeBuilder complexOutput(ItemStack itemStack) {
        return output(new ComplexItem(itemStack));
    }

    public RecipeBuilder output(ItemStack itemStack, float productionChance) {
        return output(JsonUtils.process(this.complex, itemStack, productionChance));
    }

    public RecipeBuilder output(EntityType entityType, boolean baby, int amount, float productionChance) {
        return output("@" + (baby ? "baby_" : "") + entityType.getKey().getKey() + ":" + amount + "%" + productionChance);
    }

    public RecipeBuilder output(String output) {
        this.outputs.add(output);
        return this;
    }

    public RecipeBuilder label(DefaultLabels label) {
        this.labels.add(label.id());
        return this;
    }
    
    public RecipeBuilder label(String label) {
        this.labels.add(label);
        return this;
    }
    
    public JsonObject build() {
        final JsonObject recipe = new JsonObject();
        if (this.time != null && time > 0) {
            recipe.addProperty("time", this.time);
        }

        if (this.energy != null && energy != 0) {
            recipe.addProperty("energy", this.energy);
        }

        JsonUtils.addArray(recipe, "complex", this.complex);
        JsonUtils.addArray(recipe, "inputs", this.inputs);
        JsonUtils.addArray(recipe, "outputs", this.outputs);
        JsonUtils.addArray(recipe, "labels", this.labels);
        
        return recipe;
    }
}
