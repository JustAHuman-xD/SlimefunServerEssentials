package me.justahuman.slimefun_server_essentials.api.display;

import java.util.function.Consumer;

public class RecipeDisplayBuilder extends AbstractDisplayBuilder<RecipeDisplayBuilder> {
    protected boolean dynamic = true;

    @Override
    public RecipeDisplayBuilder component(Consumer<ComponentBuilder> properties) {
        return super.component(properties.andThen(builder -> builder.dynamic(this.dynamic)));
    }

    public RecipeDisplayBuilder width(int width) {
        this.width = width;
        return this;
    }

    public RecipeDisplayBuilder height(int height) {
        this.height = height;
        return this;
    }

    public RecipeDisplayBuilder fixedX(boolean fixedX) {
        this.fixedX = fixedX;
        return this;
    }

    public RecipeDisplayBuilder fixedY(boolean fixedY) {
        this.fixedY = fixedY;
        return this;
    }

    public RecipeDisplayBuilder dynamic(boolean dynamic) {
        this.dynamic = dynamic;
        return this;
    }
}
