package me.justahuman.slimefun_server_essentials.implementation.core;

import com.google.gson.JsonObject;

import me.justahuman.slimefun_server_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_server_essentials.api.display.AbstractDisplayBuilder;
import me.justahuman.slimefun_server_essentials.api.display.GridDisplayBuilder;
import me.justahuman.slimefun_server_essentials.api.display.RecipeDisplayBuilder;
import me.justahuman.slimefun_server_essentials.api.display.SimpleRenderable;

import java.util.function.Supplier;

import static me.justahuman.slimefun_server_essentials.implementation.core.DefaultComponentTypes.*;

public enum DefaultDisplays {
    ANCIENT_ALTAR("ancient_altar", () -> {
        RecipeDisplayBuilder builder = new RecipeDisplayBuilder();
        OffsetBuilder offsets = new OffsetBuilder(
                PADDING, PADDING,
                PADDING + (SLOT.size() * 5) + PADDING + ARROW_RIGHT.width() + PADDING + SLOT.size() + PADDING,
                PADDING + SLOT.size() * 5 + PADDING
        );

        builder.width(offsets.x().max()).height(offsets.y().max());
        offsets.y().add(SLOT.size() * 2);
        builder.slot(slot -> slot.index(4).pos(offsets).renderable(SimpleRenderable.DISPENSER_SLOT));
        offsets.x().addSlot(false);
        builder.slot(slot -> slot.index(1).x(offsets).y(offsets.getY() + SLOT.size()).renderable(SimpleRenderable.DISPENSER_SLOT));
        builder.slot(slot -> slot.index(7).x(offsets).y(offsets.getY() - SLOT.size()).renderable(SimpleRenderable.DISPENSER_SLOT));
        offsets.x().addSlot(false);
        builder.slot(slot -> slot.index(2).x(offsets).y(offsets.getY() + SLOT.size() * 2).renderable(SimpleRenderable.DISPENSER_SLOT));
        builder.slot(slot -> slot.index(5).pos(offsets).renderable(SimpleRenderable.ENCHANTING_TABLE_SLOT));
        builder.slot(slot -> slot.index(8).x(offsets).y(offsets.getY() - SLOT.size() * 2).renderable(SimpleRenderable.DISPENSER_SLOT));
        offsets.x().addSlot(false);
        builder.slot(slot -> slot.index(3).x(offsets).y(offsets.getY() + SLOT.size()).renderable(SimpleRenderable.DISPENSER_SLOT));
        builder.slot(slot -> slot.index(9).x(offsets).y(offsets.getY() - SLOT.size()).renderable(SimpleRenderable.DISPENSER_SLOT));
        offsets.x().addSlot(false);
        builder.slot(slot -> slot.index(6).pos(offsets));
        offsets.x().addSlot();

        builder.fillingArrowRight(arrow -> arrow.x(offsets).centeredY(offsets));
        offsets.x().addArrow();
        builder.slot(slot -> slot.x(offsets).centeredY(offsets).index(1).output());

        return builder;
    }),
    GRID_3X3("grid3", () -> new GridDisplayBuilder(3, 3, 1, 1)),
    REACTOR("reactor", () -> {
        RecipeDisplayBuilder builder = new RecipeDisplayBuilder();
        OffsetBuilder offsets = new OffsetBuilder(
                PADDING, PADDING,
                PADDING + SLOT.size() + PADDING + ARROW_RIGHT.width() + PADDING + LARGE_SLOT.size() + PADDING + ARROW_LEFT.size() + PADDING + SLOT.size() + PADDING,
                PADDING + SLOT.size() * 3 + PADDING
        );

        builder.width(offsets.x().max()).height(offsets.y().max());

        builder.slot(slot -> slot.index(1).pos(offsets));
        offsets.y().addSlot(false);
        builder.slot(slot -> slot.index(0).pos(offsets));
        offsets.y().addSlot(false);
        builder.slot(slot -> slot.index(0).pos(offsets));
        offsets.x().addSlot();

        builder.fillingArrowRight(arrow -> arrow.pos(offsets));
        offsets.x().addArrow();

        offsets.y().subtract(LARGE_SLOT.size() - SLOT.size());
        builder.energy(energy -> energy.x(offsets.getX() + (LARGE_SLOT.size() - ENERGY.width()) / 2).y(offsets.getY() - ENERGY.height() - PADDING));
        builder.largeSlot(slot -> slot.pos(offsets).output());
        offsets.x().addLargeSlot();
        offsets.y().add(LARGE_SLOT.size() - SLOT.size());

        builder.fillingArrowLeft(arrow -> arrow.pos(offsets));
        offsets.x().addArrow();

        offsets.y().subtract(SLOT.size() * 2);
        builder.slot(slot -> slot.index(2).pos(offsets));
        offsets.y().addSlot(false);
        builder.slot(slot -> slot.index(3).pos(offsets));
        offsets.y().addSlot(false);
        builder.slot(slot -> slot.index(4).pos(offsets));

        return builder;
    }),
    SMELTERY("smeltery", () -> new GridDisplayBuilder(3, 2, 1, 1));

    private final String id;
    private final AbstractDisplayBuilder<?> display;

    <B extends AbstractDisplayBuilder<B>> DefaultDisplays(String id, Supplier<B> displayProvider) {
        this.id = id;
        this.display = displayProvider.get();
    }

    public String id() {
        return this.id;
    }

    public AbstractDisplayBuilder<?> display() {
        return this.display;
    }
}
