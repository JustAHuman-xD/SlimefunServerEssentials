package me.justahuman.recipe_exporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class RecipeBuilder {
    private Integer time = null;
    private JsonArray inputs = null;
    private JsonArray outputs = null;
    
    public RecipeBuilder time(Integer time) {
        this.time = time;
        return this;
    }
    
    public RecipeBuilder inputs(ItemStack[] inputs) {
        return inputs(Utils.processList(inputs));
    }
    
    private RecipeBuilder inputs(JsonArray inputs) {
        this.inputs = inputs;
        return this;
    }
    
    public RecipeBuilder outputs(ItemStack[] outputs) {
        return outputs(Utils.processList(outputs));
    }
    
    private RecipeBuilder outputs(JsonArray outputs) {
        this.outputs = outputs;
        return this;
    }
    
    public JsonObject build() {
        final JsonObject recipe = new JsonObject();
        if (this.time != null) {
            recipe.addProperty("time", this.time);
        }
        
        if (this.inputs != null) {
            recipe.add("inputs", this.inputs);
        }
    
        if (this.outputs != null) {
            recipe.add("outputs", this.outputs);
        }
        
        return recipe;
    }
}
