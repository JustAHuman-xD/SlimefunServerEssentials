package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

import static me.justahuman.slimefun_server_essentials.api.display.ComponentType.*;

public abstract class AbstractDisplayBuilder<B extends AbstractDisplayBuilder<B>> {
    protected final JsonArray components = new JsonArray();
    protected boolean fixedX = false;
    protected boolean fixedY = false;
    protected int width = -1;
    protected int height = -1;
    
    public B energy(Consumer<ComponentBuilder> properties) {
        return component(properties, ENERGY);
    }

    public B slot(Consumer<ComponentBuilder> properties) {
        return component(properties, SLOT);
    }
    
    public B largeSlot(Consumer<ComponentBuilder> properties) {
        return component(properties, LARGE_SLOT);
    }
    
    public B arrowRight(Consumer<ComponentBuilder> properties) {
        return arrow(false, false, properties);
    }

    public B fillingArrowRight(Consumer<ComponentBuilder> properties) {
        return arrow(false, true, properties);
    }

    public B arrowLeft(Consumer<ComponentBuilder> properties) {
        return arrow(true, false, properties);
    }

    public B fillingArrowLeft(Consumer<ComponentBuilder> properties) {
        return arrow(true, true, properties);
    }
    
    protected B arrow(boolean left, boolean filling, Consumer<ComponentBuilder> properties) {
        ComponentType type = left ? (filling ? FILLING_ARROW_LEFT : ARROW_LEFT) : (filling ? FILLING_ARROW_RIGHT : ARROW_RIGHT);
        return component(properties, type);
    }

    protected B component(Consumer<ComponentBuilder> properties, ComponentType type) {
        return component(((Consumer<ComponentBuilder>) (builder -> builder.type(type))).andThen(properties));
    }

    public B component(Consumer<ComponentBuilder> properties) {
        ComponentBuilder builder = new ComponentBuilder();
        properties.accept(builder);
        components.add(builder.build());
        return (B) this;
    }

    public JsonObject build(String type) {
        if (type.isBlank() || width == -1 || height == -1) {
            throw new IllegalArgumentException();
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.add("components", components);
        jsonObject.addProperty("width", width);
        jsonObject.addProperty("height", height);

        if (fixedX) {
            jsonObject.addProperty("fixed_x", true);
        }

        if (fixedY) {
            jsonObject.addProperty("fixed_y", true);
        }

        return jsonObject;
    }
}