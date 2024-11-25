package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.OffsetBuilder;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {
    // All Component properties
    protected ComponentType type = null;
    protected int x = -1;
    protected int y = -1;
    protected int height = -1;
    protected int width = -1;
    protected boolean dynamic = true;
    protected final List<String> tooltip = new ArrayList<>();
    // Label & Custom Properties
    protected String texture = null;

    public ComponentBuilder type(ComponentType type) {
        this.type = type;
        this.width = type.width();
        this.height = type.height();
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

    public ComponentBuilder texture(String texture) {
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
        component.addProperty("x", x);
        component.addProperty("y", y);

        if (!tooltip.isEmpty()) {
            JsonArray tooltip = new JsonArray();
            for (String tooltipLine : this.tooltip) {
                tooltip.add(tooltipLine);
            }
            component.add("tooltip", tooltip);
        }

        if (type == ComponentType.LABEL) {
            if (texture == null || texture.isBlank()) {
                throw new IllegalArgumentException("Missing textures! (required for Type.LABEL)");
            }
            component.addProperty("texture", texture);
        } else if (type == ComponentType.CUSTOM) {
            if (texture == null || texture.isBlank() || height == -1 || width == -1) {
                throw new IllegalArgumentException("Missing one or more required properties for Type.CUSTOM! (texture: %s, height: %s, width: %s)".formatted(texture, height, width));
            }
            component.addProperty("texture", texture);
            component.addProperty("height", height);
            component.addProperty("width", width);
        }

        return component;
    }
}
