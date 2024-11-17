package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.ComponentBuilder;

import java.util.function.Consumer;

import static me.justahuman.slimefun_server_essentials.api.ComponentBuilder.Type.*;

public abstract class AbstractDisplayBuilder<B extends AbstractDisplayBuilder<B>> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final JsonArray components = new JsonArray();
    protected int padding = 4;
    
    public B energy(Consumer<ComponentBuilder> properties) {
        return component(properties.andThen(builder -> builder.type(ENERGY)));
    }

    public B input(Consumer<ComponentBuilder> properties) {
        return component(properties.andThen(builder -> builder.type(INPUT)));
    }
    
    public B output(Consumer<ComponentBuilder> properties) {
        return component(properties.andThen(builder -> builder.type(OUTPUT)));
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
        ComponentBuilder.Type type = left ? (filling ? FILLING_ARROW_LEFT : ARROW_LEFT) : (filling ? ComponentBuilder.Type.FILLING_ARROW_RIGHT : ComponentBuilder.Type.ARROW_RIGHT);
        return component(properties.andThen(builder -> builder.type(type)));
    }

    public B component(Consumer<ComponentBuilder> properties) {
        ComponentBuilder builder = new ComponentBuilder();
        properties.accept(builder);
        components.add(builder.build());
        return (B) this;
    }

    public B setPadding(int padding) {
        this.padding = padding;
        return (B) this;
    }

    public JsonObject build(String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.add("components", components);
        return jsonObject;
    }
}