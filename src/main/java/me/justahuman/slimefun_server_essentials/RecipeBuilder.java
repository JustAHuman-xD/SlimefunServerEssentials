package me.justahuman.slimefun_server_essentials;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeBuilder {
    private Integer time = null;
    private Integer energy = null;
    private JsonArray inputs = null;
    private JsonArray outputs = null;
    private final JsonArray labels = new JsonArray();
    
    public RecipeBuilder time(Integer time) {
        this.time = time;
        return this;
    }
    
    public RecipeBuilder energy(Integer energy) {
        this.energy = energy;
        return this;
    }
    
    public RecipeBuilder inputs(List<ItemStack> inputs) {
        return inputs(Utils.processList(inputs));
    }
    
    public RecipeBuilder inputs(ItemStack[] inputs) {
        return inputs(Utils.processArray(inputs));
    }
    
    private RecipeBuilder inputs(JsonArray inputs) {
        this.inputs = inputs;
        return this;
    }
    
    public RecipeBuilder outputs(List<ItemStack> outputs) {
        return outputs(Utils.processList(outputs));
    }
    
    public RecipeBuilder outputs(ItemStack[] outputs) {
        return outputs(Utils.processArray(outputs));
    }
    
    private RecipeBuilder outputs(JsonArray outputs) {
        this.outputs = outputs;
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
        
        if (this.inputs != null) {
            recipe.add("inputs", this.inputs);
        }
    
        if (this.outputs != null) {
            recipe.add("outputs", this.outputs);
        }
        
        if (!this.labels.isEmpty()) {
            recipe.add("labels", this.labels);
        }
        
        return recipe;
    }
}
