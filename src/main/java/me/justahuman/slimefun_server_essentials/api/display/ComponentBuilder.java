package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_server_essentials.util.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {
    protected ComponentType type = null;
    protected boolean output = false;
    protected int index = -1;
    protected int x = -1;
    protected int y = -1;
    protected final List<String> tooltip = new ArrayList<>();
    protected SimpleRenderable renderable = null;

    public ComponentBuilder type(ComponentType type) {
        this.type = type;
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

    public ComponentBuilder renderable(SimpleRenderable renderable) {
        this.renderable = renderable;
        return this;
    }

    public void toBytes(ByteArrayDataOutput output) {
        if (type == null || x == -1 || y == -1) {
            throw new IllegalArgumentException("Missing one or more required properties! (type: %s, x: %s, y: %s)".formatted(type, x, y));
        }

        output.writeUTF(type.id());
        output.writeInt(x);
        output.writeInt(y);

        DataUtils.add(output, index, -1);
        output.writeBoolean(this.output);
        output.writeBoolean(renderable != null);
        if (renderable != null) {
            renderable.toBytes(output);
        }
        DataUtils.addArray(output, tooltip);
    }

    public JsonObject toJson() {
        JsonObject component = new JsonObject();
        if (type == null || x == -1 || y == -1) {
            throw new IllegalArgumentException("Missing one or more required properties! (type: %s, x: %s, y: %s)".formatted(type, x, y));
        }

        component.addProperty("type", type.id());
        component.addProperty("x", x);
        component.addProperty("y", y);

        if (index != -1) {
            component.addProperty("index", index);
        }
        if (output) {
            component.addProperty("output", true);
        }

        if (renderable != null) {
            component.add("renderable", renderable.toJson());
        }

        if (!tooltip.isEmpty()) {
            JsonArray tooltip = new JsonArray();
            this.tooltip.forEach(tooltip::add);
            component.add("tooltip", tooltip);
        }

        return component;
    }
}
