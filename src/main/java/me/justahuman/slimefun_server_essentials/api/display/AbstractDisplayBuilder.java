package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static me.justahuman.slimefun_server_essentials.implementation.core.DefaultComponentTypes.*;

public abstract class AbstractDisplayBuilder<B extends AbstractDisplayBuilder<B>> {
    protected int width = -1;
    protected int height = -1;
    protected final List<ComponentBuilder> components = new ArrayList<>();
    
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
        components.add(builder);
        return (B) this;
    }

    public void toBytes(ByteArrayDataOutput output) {
        if (width == -1 || height == -1) {
            throw new IllegalArgumentException("Width and height must be set before serialization.");
        }

        output.writeInt(width);
        output.writeInt(height);

        output.writeInt(components.size());
        for (ComponentBuilder component : components) {
            component.toBytes(output);
        }
    }

    public JsonObject toJson() {
        if (width == -1 || height == -1) {
            throw new IllegalArgumentException("Width and height must be set before building the JSON object.");
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("width", width);
        jsonObject.addProperty("height", height);

        JsonArray components = new JsonArray();
        for (ComponentBuilder component : this.components) {
            components.add(component.toJson());
        }
        jsonObject.add("components", components);

        return jsonObject;
    }
}