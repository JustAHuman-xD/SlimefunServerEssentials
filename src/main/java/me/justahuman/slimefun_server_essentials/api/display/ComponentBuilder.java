package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.OffsetBuilder;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {
    protected ComponentType type = null;
    protected boolean output = false;
    protected int index = -1;
    protected int x = -1;
    protected int y = -1;
    protected int height = -1;
    protected int width = -1;
    protected boolean dynamic = true;
    protected final List<String> tooltip = new ArrayList<>();
    protected Texture texture = null;

    public ComponentBuilder type(ComponentType type) {
        this.type = type;
        this.width = type.width();
        this.height = type.height();
        return this;
    }

    public ComponentBuilder output() {
        this.output = true;
        return this;
    }

    public ComponentBuilder index(int index) {
        this.index = index;
        return this;
    }

    public ComponentBuilder pos(OffsetBuilder offsets) {
        return x(offsets).y(offsets);
    }

    public ComponentBuilder x(OffsetBuilder offsets) {
        return x(offsets.getX());
    }

    public ComponentBuilder x(int x) {
        this.x = x;
        return this;
    }

    public ComponentBuilder y(OffsetBuilder offsets) {
        return y(offsets.getY());
    }

    public ComponentBuilder y(int y) {
        this.y = y;
        return this;
    }

    public ComponentBuilder centeredY(OffsetBuilder offsets) {
        return y(offsets.centeredY(this.type));
    }

    public ComponentBuilder tooltip(String... tooltip) {
        this.tooltip.addAll(List.of(tooltip));
        return this;
    }

    public ComponentBuilder texture(Texture texture) {
        this.texture = texture;
        return this;
    }

    public ComponentBuilder height(int height) {
        this.height = height;
        return this;
    }

    public ComponentBuilder width(int width) {
        this.width = width;
        return this;
    }

    public ComponentBuilder dynamic(boolean dynamic) {
        this.dynamic = dynamic;
        return this;
    }

    public JsonObject build() {
        JsonObject component = new JsonObject();
        if (type == null || x == -1 || y == -1) {
            throw new IllegalArgumentException("Missing one or more required properties! (type: %s, x: %s, y: %s)".formatted(type, x, y));
        }

        component.addProperty("type", type.name());
        component.addProperty("output", true);
        if (index != -1) {
            component.addProperty("index", index);
        }

        component.addProperty("x", x);
        component.addProperty("y", y);

        if (!tooltip.isEmpty()) {
            JsonArray tooltip = new JsonArray();
            for (String tooltipLine : this.tooltip) {
                tooltip.add(tooltipLine);
            }
            component.add("tooltip", tooltip);
        }

        if (type == ComponentType.CUSTOM) {
            if (texture == null || height == -1 || width == -1) {
                throw new IllegalArgumentException("Missing one or more required properties for Type.CUSTOM! (texture: %s, height: %s, width: %s)".formatted(texture, height, width));
            }
            component.add("texture", texture.toJson());
            component.addProperty("height", height);
            component.addProperty("width", width);
        }

        return component;
    }
}
