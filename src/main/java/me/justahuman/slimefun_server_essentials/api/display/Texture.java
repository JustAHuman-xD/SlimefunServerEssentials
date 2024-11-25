package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonObject;

public record Texture(String identifier, int width, int height, int u, int v, int textureWidth, int textureHeight) {
    public static final String WIDGETS = "slimefun_essentials:textures/gui/widgets.png";
    public static final String WIDGETS_DARK = "slimefun_essentials:textures/gui/widgets_dark.png";
    public static final Texture DISPENSER_SLOT = new Texture(WIDGETS, 18, 18, 0, 0);
    public static final Texture ENCHANTING_TABLE_SLOT = new Texture(WIDGETS, 18, 18, 18, 0);

    public Texture(String identifier, int width, int height, int u, int v) {
        this(identifier, width, height, u, v, 256, 256);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!this.identifier.equals(WIDGETS)) {
            json.addProperty("identifier", this.identifier);
        }
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        if (this.u != 0) {
            json.addProperty("u", this.u);
        }
        if (this.v != 0) {
            json.addProperty("v", this.v);
        }
        if (this.textureWidth != 256) {
            json.addProperty("texture_width", this.textureWidth);
        }
        if (this.textureHeight != 256) {
            json.addProperty("texture_height", this.textureHeight);
        }
        return json;
    }
}
