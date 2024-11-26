package me.justahuman.slimefun_server_essentials.implementation.core;

import me.justahuman.slimefun_server_essentials.api.Condition;
import me.justahuman.slimefun_server_essentials.api.display.ComponentType;
import me.justahuman.slimefun_server_essentials.api.display.ConditionalRenderable;
import me.justahuman.slimefun_server_essentials.api.display.FillingComponentType;
import me.justahuman.slimefun_server_essentials.api.display.SimpleRenderable;

public class DefaultComponentTypes {
    public static final int PADDING = 4;
    public static final ComponentType ENERGY = new FillingComponentType(
            "energy",
            new SimpleRenderable(SimpleRenderable.WIDGETS, 7, 9, 36, 0),
            new ConditionalRenderable(
                    new SimpleRenderable(SimpleRenderable.WIDGETS, 7, 9, 43, 0, "%energy%J Generated"),
                    new SimpleRenderable(SimpleRenderable.WIDGETS, 7, 9, 50, 0, "%energy%J Consumed"),
                    new Condition(Condition.PROPERTY_ENERGY, Condition.CONDITION_GREATER_EQUALS, 0)
            ),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 7, 9, 36, 0),
            new ConditionalRenderable(
                    new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 7, 9, 43, 0, "%energy%J Generated"),
                    new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 7, 9, 50, 0, "%energy%J Consumed"),
                    new Condition(Condition.PROPERTY_ENERGY, Condition.CONDITION_GREATER_EQUALS, 0)
            ),
            7, 9, 500, false
    ).register();
    public static final ComponentType SLOT = simple("slot", 18, 18, 0, 0);
    public static final ComponentType LARGE_SLOT = simple("large_slot", 26, 26, 0, 0);
    public static final ComponentType ARROW_RIGHT = simple("arrow_right", 24, 17, 0, 0);
    public static final ComponentType ARROW_LEFT = simple("arrow_left", 24, 17, 24, 0);
    public static final ComponentType FILLING_ARROW_RIGHT = new FillingComponentType(
            "filling_arrow_right",
            new SimpleRenderable(SimpleRenderable.WIDGETS, 24, 17, 44, 222, "%time_seconds% Seconds (%time% Ticks)"),
            new SimpleRenderable(SimpleRenderable.WIDGETS, 24, 17, 44, 239),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 24, 17, 44, 222, "%time_seconds% Seconds (%time% Ticks)"),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 24, 17, 44, 239),
            24, 17, FillingComponentType.RECIPE_TIME,
            true, Condition.TRUE, Condition.TRUE
    ).register();
    public static final ComponentType FILLING_ARROW_LEFT = new FillingComponentType(
            "filling_arrow_left",
            new SimpleRenderable(SimpleRenderable.WIDGETS, 24, 17, 67, 222, "%time_seconds% Seconds (%time% Ticks)"),
            new SimpleRenderable(SimpleRenderable.WIDGETS, 24, 17, 67, 239),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 24, 17, 67, 222, "%time_seconds% Seconds (%time% Ticks)"),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 24, 17, 67, 239),
            24, 17, FillingComponentType.RECIPE_TIME,
            true, Condition.TRUE, Condition.FALSE
    ).register();
    public static final ComponentType REQUIRES_DAY = simple("day", 13, 13, 57, 0);
    public static final ComponentType REQUIRES_NIGHT = simple("night", 13, 13, 70, 0);

    private static ComponentType simple(String id, int width, int height, int u, int v) {
        return new ComponentType(
                id,
                new SimpleRenderable(SimpleRenderable.WIDGETS, width, height, u, v),
                new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, width, height, u, v)
        ).register();
    }
}
