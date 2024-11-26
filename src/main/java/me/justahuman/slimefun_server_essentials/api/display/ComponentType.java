package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.implementation.DisplayComponentTypes;

import java.util.ArrayList;
import java.util.List;

public class ComponentType {
    protected final String id;
    protected final CustomRenderable light;
    protected final CustomRenderable dark;
    protected final List<String> tooltip = new ArrayList<>();
    private boolean registered = false;

    public ComponentType(String id, CustomRenderable light, CustomRenderable dark) {
        this.id = id;
        this.light = light;
        this.dark = dark;
    }

    public String id() {
        return this.id;
    }

    public int size() {
        return width();
    }

    public int size(boolean y) {
        return y ? height() : width();
    }

    public int width() {
        return Math.max(this.light.width(), this.dark.width());
    }

    public int height() {
        return Math.max(this.light.height(), this.dark.height());
    }

    public void addTooltip(String tooltip) {
        this.tooltip.add(tooltip);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.add("light", this.light.toJson());
        json.add("dark", this.dark.toJson());
        if (!this.tooltip.isEmpty()) {
            JsonArray tooltip = new JsonArray();
            this.tooltip.forEach(tooltip::add);
            json.add("tooltip", tooltip);
        }
        return json;
    }

    public ComponentType register() {
        if (!this.registered) {
            DisplayComponentTypes.register(this.id, this.toJson());
            this.registered = true;
        }
        return this;
    }
}
