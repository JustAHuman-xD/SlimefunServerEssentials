package me.justahuman.recipe_exporter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.justahuman.recipe_exporter.Utils.optimizeRecipe;

public class DeprecatedCommandManager implements CommandExecutor {

    private static final Gson GSON = new Gson().newBuilder().create();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return true;
    }

    /*private boolean export() {
        try {
            final Map<String, List<SlimefunItem>> sortedSlimefunItems = getSortedSlimefunItems();
            final JsonObject root = new JsonObject();
            
            // First fill all the items and recipes
            for (String addonName : sortedSlimefunItems.keySet()) {
                final List<SlimefunItem> slimefunItems = sortedSlimefunItems.get(addonName);
                final JsonObject addonRoot = new JsonObject();
                final JsonObject addonItems = new JsonObject();
                final JsonObject addonRecipes = new JsonObject();
                
                for (SlimefunItem slimefunItem : slimefunItems) {
                    addonItems.add(slimefunItem.getId(), serializeItem(slimefunItem));
                    final JsonObject recipes = fillCategory(slimefunItem);
                    if (recipes.getAsJsonArray("recipes").size() > 0) {
                        addonRecipes.add(slimefunItem.getId(), recipes);
                    }
                }
                addonRoot.add("items", addonItems);
                addonRoot.add("recipes", addonRecipes);
                root.add(addonName, addonRoot);
            }
            
            final JsonArray stacks = new JsonArray();
            final List<String> ids = new ArrayList<>();
            for (SlimefunItem slimefunItem : Slimefun.getRegistry().getAllSlimefunItems()) {
                ids.add(slimefunItem.getId());
            }
            ids.sort(Comparator.naturalOrder());
            for (String id : ids) {
                stacks.add(id);
            }
            
            root.add("stacks", stacks);
            
            
            final BufferedWriter fileWriter = new BufferedWriter(new FileWriter("plugins/RecipeExporter/recipes.json", StandardCharsets.UTF_8));
            GSON.toJson(root, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            Log("Houston, we have a problem");
            e.printStackTrace();
        }

        return true;
    }*/
    
    
    
    public void addWithOptimize(JsonArray recipes, JsonObject recipe1) {
        final Pair<Integer, JsonObject> recipePair = optimizeRecipe(recipes.deepCopy(), recipe1.deepCopy());
        if (recipePair == null) {
            recipes.add(recipe1);
        } else {
            recipes.set(recipePair.getFirstValue(), recipePair.getSecondValue());
        }
    }
    
    private String getAddon(ItemStack toGet) {
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(toGet);
        if (slimefunItem != null) {
            return slimefunItem.getAddon().getName();
        }
        return "Slimefun";
    }
}
