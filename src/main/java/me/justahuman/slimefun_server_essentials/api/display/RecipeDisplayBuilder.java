package me.justahuman.slimefun_server_essentials.api.display;

public class RecipeDisplayBuilder extends AbstractDisplayBuilder<RecipeDisplayBuilder> {
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
}
