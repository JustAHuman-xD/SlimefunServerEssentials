package me.justahuman.slimefun_server_essentials.implementation.core;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.display.Texture;
import me.justahuman.slimefun_server_essentials.implementation.RecipeLabels;
import org.bukkit.World;
import org.bukkit.block.Biome;

public enum DefaultLabels {
    REQUIRES_DAY("day", new Texture(Texture.WIDGETS, 13, 13, 57, 0), new Texture(Texture.WIDGETS_DARK, 13, 13, 57, 0)),
    REQUIRES_NIGHT("night", new Texture(Texture.WIDGETS, 13, 13, 70, 0), new Texture(Texture.WIDGETS_DARK, 13, 13, 70, 0));

    private static boolean registered = false;

    private final String id;
    private final Texture light;
    private final Texture dark;

    DefaultLabels(String id, Texture light, Texture dark) {
        this.id = id;
        this.light = light;
        this.dark = dark;
    }

    public String id() {
        return this.id;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.add("light", this.light.toJson());
        json.add("dark", this.dark.toJson());
        return json;
    }

    public static String label(World.Environment environment) {
        return switch (environment) {
            case NORMAL, CUSTOM -> "overworld";
            case NETHER -> "the_nether";
            case THE_END -> "the_end";
        };
    }

    public static String label(Biome biome) {
        return "biome:" + biome.getKey().getKey();
    }

    public static void register() {
        if (!registered) {
            for (DefaultLabels label : values()) {
                RecipeLabels.register(label.id(), label.toJson());
            }
            registered = true;
        }
    }
}
