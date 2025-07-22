package me.justahuman.slimefun_server_essentials.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import lombok.Getter;
import me.justahuman.slimefun_server_essentials.api.display.ComponentType;
import me.justahuman.slimefun_server_essentials.util.DataUtils;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class RecipeBuilder {
    private Integer sfTicks = null;
    private Integer ticks = null;
    private Integer energy = null;
    private final List<ItemStack> complex = new ArrayList<>();
    private final List<String> inputs = new ArrayList<>();
    private final List<String> outputs = new ArrayList<>();
    private final List<String> labels = new ArrayList<>();

    public RecipeBuilder sfTicks(Integer ticks) {
        this.sfTicks = ticks;
        return this;
    }

    public RecipeBuilder ticks(Integer ticks) {
        this.ticks = ticks;
        return this;
    }

    public RecipeBuilder energy(Integer energy) {
        this.energy = energy;
        return this;
    }
    
    public RecipeBuilder inputs(List<ItemStack> inputs) {
        return addInputs(JsonUtils.process(this.complex, inputs));
    }
    
    public RecipeBuilder inputs(ItemStack[] inputs) {
        return addInputs(JsonUtils.process(this.complex, inputs));
    }

    public RecipeBuilder input(Material material) {
        return input(material, 1);
    }

    public RecipeBuilder input(Material material, int amount) {
        return input(new ItemStack(material, amount));
    }

    public RecipeBuilder input(SlimefunItemStack sfItemStack) {
        return input(sfItemStack, 1);
    }

    public RecipeBuilder input(SlimefunItemStack sfItemStack, int amount) {
        return input(sfItemStack, amount, 1);
    }

    public RecipeBuilder input(SlimefunItemStack sfItemStack, int amount, float consumptionChance) {
        SlimefunItem item = sfItemStack.getItem();
        if (item != null) {
            ItemStack itemStack = item.getItem().clone();
            itemStack.setAmount(amount);
            return input(itemStack, consumptionChance);
        }
        return this;
    }

    public RecipeBuilder input(ItemStack itemStack) {
        return input(itemStack, 1);
    }

    public RecipeBuilder complexInput(ItemStack itemStack) {
        return input(new ComplexItem(itemStack));
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

    private RecipeBuilder addInputs(List<String> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }
    
    public RecipeBuilder outputs(List<ItemStack> outputs) {
        return addOutputs(JsonUtils.process(this.complex, outputs));
    }
    
    public RecipeBuilder outputs(ItemStack[] outputs) {
        return addOutputs(JsonUtils.process(this.complex, outputs));
    }
    
    public RecipeBuilder addOutputs(List<String> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public RecipeBuilder output(Material material) {
        return output(material, 1);
    }

    public RecipeBuilder output(Material material, int amount) {
        return output(new ItemStack(material, amount));
    }

    public RecipeBuilder output(Material material, float productionChance) {
        return output(material, 1, productionChance);
    }

    public RecipeBuilder output(Material material, int amount, float productionChance) {
        ItemStack itemStack = new ItemStack(material, amount);
        return output(itemStack, productionChance);
    }

    public RecipeBuilder output(SlimefunItemStack sfItemStack) {
        return output(sfItemStack, 1);
    }

    public RecipeBuilder output(SlimefunItemStack sfItemStack, int amount) {
        return output(sfItemStack, amount, 1);
    }

    public RecipeBuilder output(SlimefunItemStack sfItemStack, float productionChance) {
        return output(sfItemStack, 1, productionChance);
    }

    public RecipeBuilder output(SlimefunItemStack sfItemStack, int amount, float productionChance) {
        SlimefunItem item = sfItemStack.getItem();
        if (item != null) {
            ItemStack itemStack = item.getItem().clone();
            itemStack.setAmount(amount);
            return output(itemStack, productionChance);
        }
        return this;
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

    public RecipeBuilder label(ComponentType label) {
        this.labels.add(label.id());
        return this;
    }
    
    public RecipeBuilder label(String label) {
        this.labels.add(label);
        return this;
    }

    public RecipeBuilder addLabels(List<String> labels) {
        this.labels.addAll(labels);
        return this;
    }

    public RecipeBuilder complex(List<ItemStack> complex) {
        this.complex.addAll(complex);
        return this;
    }

    public void toBytes(ByteArrayDataOutput output) {
        DataUtils.addPositive(output, this.sfTicks);
        DataUtils.addPositive(output, this.ticks);
        DataUtils.add(output, this.energy, 0);

        output.writeInt(this.complex.size());
        for (ItemStack item : this.complex) {
            output.write(DataUtils.bytes(item));
        }

        DataUtils.addArray(output, this.inputs);
        DataUtils.addArray(output, this.outputs);
        DataUtils.addArray(output, this.labels);
    }

    public JsonObject toJson() {
        final JsonObject recipe = new JsonObject();
        if (this.sfTicks != null && this.sfTicks > 0) {
            recipe.addProperty("sf_ticks", this.sfTicks);
        }

        if (this.ticks != null && this.ticks > 0) {
            recipe.addProperty("ticks", this.ticks);
        }

        if (this.energy != null && this.energy != 0) {
            recipe.addProperty("energy", this.energy);
        }

        JsonArray complexArray = new JsonArray();
        this.complex.stream().map(JsonUtils::serializeItem).forEach(complexArray::add);
        JsonUtils.addArray(recipe, "complex", complexArray);
        JsonUtils.addArray(recipe, "inputs", this.inputs);
        JsonUtils.addArray(recipe, "outputs", this.outputs);
        JsonUtils.addArray(recipe, "labels", this.labels);
        
        return recipe;
    }

    public void removeWhitespace() {
        removeWhitespace(this.inputs);
        removeWhitespace(this.outputs);
        removeWhitespace(this.labels);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RecipeBuilder other)) return false;
        return Objects.equals(this.sfTicks, other.sfTicks)
                && Objects.equals(this.ticks, other.ticks)
                && Objects.equals(this.energy, other.energy)
                && Objects.equals(this.complex, other.complex)
                && Objects.equals(this.inputs, other.inputs)
                && Objects.equals(this.outputs, other.outputs)
                && Objects.equals(this.labels, other.labels);
    }

    private static void removeWhitespace(List<String> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isBlank()) {
                list.remove(i);
            } else {
                break;
            }
        }
    }

    public RecipeBuilder sped(int speed) {
        RecipeBuilder spedRecipe = new RecipeBuilder();
        spedRecipe.sfTicks = this.sfTicks == null ? null : this.sfTicks / speed;
        spedRecipe.ticks = this.ticks == null ? null : this.ticks / speed;
        spedRecipe.energy = this.energy == null ? null : this.energy;
        spedRecipe.complex.addAll(this.complex);
        spedRecipe.inputs.addAll(this.inputs);
        spedRecipe.outputs.addAll(this.outputs);
        spedRecipe.labels.addAll(this.labels);
        return spedRecipe;
    }
}
