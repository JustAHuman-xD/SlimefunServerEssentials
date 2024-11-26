package me.justahuman.slimefun_server_essentials.api;

import me.justahuman.slimefun_server_essentials.api.display.ComponentType;

import static me.justahuman.slimefun_server_essentials.implementation.core.DefaultComponentTypes.*;

public class OffsetBuilder {
    protected Offset xOffset;
    protected Offset yOffset;

    public OffsetBuilder(int maxX, int maxY) {
        this(0, 0, maxX, maxY);
    }

    public OffsetBuilder(int x, int y, int maxX, int maxY) {
        this.xOffset = new Offset(x, maxX, false);
        this.yOffset = new Offset(y, maxY, true);
    }

    public Offset x() {
        return this.xOffset;
    }

    public Offset setX(int x) {
        this.xOffset.set(x);
        return this.xOffset;
    }

    public int getX() {
        return this.xOffset.get();
    }

    public Offset y() {
        return this.yOffset;
    }

    public Offset setY(int y) {
        this.yOffset.set(y);
        return this.yOffset;
    }

    public int getY() {
        return this.yOffset.get();
    }

    public int centeredY(ComponentType component) {
        return (this.y().max() - component.height()) / 2;
    }

    public static class Offset {
        int value;
        int max;
        boolean y;

        public Offset(int value, int max, boolean y) {
            this.value = value;
            this.max = max;
            this.y = y;
        }

        public int get() {
            return this.value;
        }

        public int max() {
            return this.max;
        }

        public Offset set(int offset) {
            this.value = offset;
            return this;
        }

        public Offset add(int add) {
            this.value += add;
            return this;
        }

        public Offset subtract(int subtract) {
            this.value -= subtract;
            return this;
        }

        public Offset addLabel() {
            return addLabel(true);
        }

        public Offset addLabel(boolean padding) {
            this.value += 14 + (padding ? PADDING : 0);
            return this;
        }

        public Offset addEnergy() {
            return addEnergy(true);
        }

        public Offset addEnergy(boolean padding) {
            this.value += ENERGY.size(this.y) + (padding ? PADDING : 0);
            return this;
        }

        public Offset addSlot() {
            return addSlot(true);
        }

        public Offset addSlot(boolean padding) {
            this.value += SLOT.size(this.y) + (padding ? PADDING : 0);
            return this;
        }

        public Offset addArrow() {
            return addArrow(true);
        }

        public Offset addArrow(boolean padding) {
            this.value += ARROW_LEFT.size(this.y) + (padding ? PADDING : 0);
            return this;
        }

        public Offset addLargeSlot() {
            return addLargeSlot(true);
        }

        public Offset addLargeSlot(boolean padding) {
            this.value += LARGE_SLOT.width() + (padding ? PADDING : 0);
            return this;
        }

        public Offset addPadding() {
            this.value += PADDING;
            return this;
        }
    }
}