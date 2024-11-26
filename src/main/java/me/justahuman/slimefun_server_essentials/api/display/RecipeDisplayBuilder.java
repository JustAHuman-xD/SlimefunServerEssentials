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
}
