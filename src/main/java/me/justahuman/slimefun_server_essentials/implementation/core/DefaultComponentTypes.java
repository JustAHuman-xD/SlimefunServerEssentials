package me.justahuman.slimefun_server_essentials.implementation.core;

import me.justahuman.slimefun_server_essentials.api.Condition;
import me.justahuman.slimefun_server_essentials.api.Property;
import me.justahuman.slimefun_server_essentials.api.display.ComponentType;
import me.justahuman.slimefun_server_essentials.api.display.ConditionalRenderable;
import me.justahuman.slimefun_server_essentials.api.display.FillingComponentType;
import me.justahuman.slimefun_server_essentials.api.display.SimpleRenderable;

public class DefaultComponentTypes {
    public static final int PADDING = 4;
    public static final ComponentType ENERGY = new FillingComponentType(
            "energy",
            new SimpleRenderable(SimpleRenderable.WIDGETS, 7, 9, 36, 0).optional(Condition.HAS_ENERGY),
            new ConditionalRenderable(
                    new SimpleRenderable(SimpleRenderable.WIDGETS, 7, 9, 43, 0, Property.ABS_TOTAL_ENERGY + "J Generated"),
                    new SimpleRenderable(SimpleRenderable.WIDGETS, 7, 9, 50, 0, Property.ABS_TOTAL_ENERGY + "J Consumed"),
                    new Condition(Property.ENERGY, Condition.CONDITION_GREATER_EQUALS, 0)
            ).optional(Condition.HAS_ENERGY),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 7, 9, 36, 0).optional(Condition.HAS_ENERGY),
            new ConditionalRenderable(
                    new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 7, 9, 43, 0, Property.ABS_TOTAL_ENERGY + "J Generated"),
                    new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 7, 9, 50, 0, Property.ABS_TOTAL_ENERGY + "J Consumed"),
                    new Condition(Property.ENERGY, Condition.CONDITION_GREATER_EQUALS, 0)
            ).optional(Condition.HAS_ENERGY),
            7, 9, 500, false
    );
    public static final ComponentType SLOT = simple("slot", 18, 18, 0, 238);
    public static final ComponentType LARGE_SLOT = simple("large_slot", 26, 26, 18, 230);
    public static final ComponentType ARROW_RIGHT = simple("arrow_right", 24, 17, 44, 222);
    public static final ComponentType ARROW_LEFT = simple("arrow_left", 24, 17, 68, 222);
    private static final String TIME_TOOLTIP = Property.SECONDS + " Seconds (" + Property.TICKS + " Ticks)";
    public static final ComponentType FILLING_ARROW_RIGHT = new FillingComponentType(
            "filling_arrow_right",
            ARROW_RIGHT.light(),
            new SimpleRenderable(SimpleRenderable.WIDGETS, 24, 17, 44, 239, TIME_TOOLTIP).optional(Condition.HAS_TICKS),
            ARROW_RIGHT.dark(),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 24, 17, 44, 239, TIME_TOOLTIP).optional(Condition.HAS_TICKS),
            24, 17, Property.MILLIS,
            true, Condition.TRUE, Condition.TRUE
    );
    public static final ComponentType FILLING_ARROW_LEFT = new FillingComponentType(
            "filling_arrow_left",
            ARROW_LEFT.light(),
            new SimpleRenderable(SimpleRenderable.WIDGETS, 24, 17, 67, 239, TIME_TOOLTIP).optional(Condition.HAS_TICKS),
            ARROW_LEFT.dark(),
            new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, 24, 17, 67, 239, TIME_TOOLTIP).optional(Condition.HAS_TICKS),
            24, 17, Property.MILLIS,
            true, Condition.TRUE, Condition.FALSE
    );
    public static final ComponentType REQUIRES_DAY = simple("day", 13, 13, 57, 0);
    public static final ComponentType REQUIRES_NIGHT = simple("night", 13, 13, 70, 0);

    private static ComponentType simple(String id, int width, int height, int u, int v) {
        return new ComponentType(
                id,
                new SimpleRenderable(SimpleRenderable.WIDGETS, width, height, u, v),
                new SimpleRenderable(SimpleRenderable.WIDGETS_DARK, width, height, u, v)
        );
    }
}
