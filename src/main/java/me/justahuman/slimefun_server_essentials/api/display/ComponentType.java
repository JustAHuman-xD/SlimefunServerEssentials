package me.justahuman.slimefun_server_essentials.api.display;

public enum ComponentType {
    ENERGY(7, 9),
    SLOT(18),
    LARGE_SLOT(26),
    ARROW_RIGHT(24, 17),
    ARROW_LEFT(24, 17),
    FILLING_ARROW_RIGHT(24, 17),
    FILLING_ARROW_LEFT(24, 17),
    LABEL,
    CUSTOM;

    public static final int PADDING = 4;

    private final int width;
    private final int height;

    ComponentType(int width, int height) {
        this.width = width;
        this.height = height;
    }

    ComponentType(int size) {
        this(size, size);
    }

    ComponentType() {
        this(-1, -1);
    }

    public int size() {
        return size(false);
    }

    public int size(boolean y) {
        return y ? height : width;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
