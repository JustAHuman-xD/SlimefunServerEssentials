package me.justahuman.slimefun_server_essentials.implementation.core;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.justahuman.slimefun_server_essentials.api.display.AbstractDisplayBuilder;
import me.justahuman.slimefun_server_essentials.api.display.GridDisplayBuilder;
import me.justahuman.slimefun_server_essentials.api.display.RecipeDisplayBuilder;
import me.justahuman.slimefun_server_essentials.implementation.RecipeDisplays;

import java.util.function.Supplier;

public enum DefaultDisplays {
    ANCIENT_ALTAR("ancient_altar", () -> new RecipeDisplayBuilder()),
    GRID_3X3("grid3", () -> new GridDisplayBuilder(3, 3)),
    REACTOR("reactor", () -> new RecipeDisplayBuilder()),
    SMELTERY("smeltery", () -> new GridDisplayBuilder(3, 2));

    private final @Getter String type;
    private final @Getter JsonObject display;

    <B extends AbstractDisplayBuilder<B>> DefaultDisplays(String type, Supplier<B> displayProvider) {
        this.type = type;
        this.display = displayProvider.get().build(type);
    }

    public static void register() {
        for (DefaultDisplays display : values()) {
            RecipeDisplays.register(display.type, display.display);
        }
    }
}
