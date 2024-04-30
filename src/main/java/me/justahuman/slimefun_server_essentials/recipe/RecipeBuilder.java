package me.justahuman.slimefun_server_essentials.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
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
        return ticks(ticks * 10);
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
        return inputs(JsonUtils.processList(this.complex, inputs));
    }
    
    public RecipeBuilder inputs(ItemStack[] inputs) {
        return inputs(JsonUtils.processArray(this.complex, inputs));
    }

    public RecipeBuilder input(ItemStack itemStack) {
        return input(itemStack, 1);
    }

    public RecipeBuilder input(ItemStack itemStack, float chance) {
        return input(JsonUtils.process(this.complex, itemStack, chance));
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
        return outputs(JsonUtils.processList(this.complex, outputs));
    }
    
    public RecipeBuilder outputs(ItemStack[] outputs) {
        return outputs(JsonUtils.processArray(this.complex, outputs));
    }
    
    private RecipeBuilder outputs(JsonArray outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public RecipeBuilder output(ItemStack itemStack) {
        return output(itemStack, 1);
    }

    public RecipeBuilder output(ItemStack itemStack, float chance) {
        return output(JsonUtils.process(this.complex, itemStack, chance));
    }

    public RecipeBuilder output(String output) {
        this.outputs.add(output);
        return this;
    }
    
    public RecipeBuilder label(String label) {
        this.labels.add(label);
        return this;
    }
    
    public JsonObject build() {
        final JsonObject recipe = new JsonObject();
        if (this.time != null) {
            recipe.addProperty("time", this.time);
        }

        if (this.energy != null) {
            recipe.addProperty("energy", this.energy);
        }

        if (!this.complex.isEmpty()) {
            recipe.add("complex", this.complex);
        }
        
        if (!this.inputs.isEmpty()) {
            recipe.add("inputs", this.inputs);
        }
    
        if (!this.outputs.isEmpty()) {
            recipe.add("outputs", this.outputs);
        }
        
        if (!this.labels.isEmpty()) {
            recipe.add("labels", this.labels);
        }
        
        return recipe;
    }
}
