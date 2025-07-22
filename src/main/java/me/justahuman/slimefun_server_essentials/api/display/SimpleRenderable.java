package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.util.DataUtils;

public record SimpleRenderable(
        String identifier,
        int width, int height,
        int u, int v,
        int textureWidth, int textureHeight,
        String... tooltip
) implements CustomRenderable {
    public static final String WIDGETS = "slimefun_essentials:textures/gui/widgets.png";
    public static final String WIDGETS_DARK = "slimefun_essentials:textures/gui/widgets_dark.png";
    public static final SimpleRenderable DISPENSER_SLOT = new SimpleRenderable(WIDGETS, 18, 18, 0, 0);
    public static final SimpleRenderable ENCHANTING_TABLE_SLOT = new SimpleRenderable(WIDGETS, 18, 18, 18, 0);

    public SimpleRenderable(String identifier, int width, int height, int u, int v, String... tooltip) {
        this(identifier, width, height, u, v, 256, 256, tooltip);
    }

    @Override
    public void toBytes(ByteArrayDataOutput output) {
        output.writeBoolean(false);
        DataUtils.add(output, identifier, WIDGETS);
        output.writeInt(width);
        output.writeInt(height);
        DataUtils.add(output, u, 0);
        DataUtils.add(output, v, 0);
        DataUtils.add(output, textureWidth, 256);
        DataUtils.add(output, textureHeight, 256);
        DataUtils.addTooltip(output, tooltip);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!identifier.equals(WIDGETS)) {
            json.addProperty("identifier", identifier);
        }
        json.addProperty("width", width);
        json.addProperty("height", height);
        if (u != 0) {
            json.addProperty("u", u);
        }
        if (v != 0) {
            json.addProperty("v", v);
        }
        if (textureWidth != 256) {
            json.addProperty("texture_width", textureWidth);
        }
        if (textureHeight != 256) {
            json.addProperty("texture_height", textureHeight);
        }
        if (tooltip.length > 0) {
            JsonArray tooltipArray = new JsonArray();
            for (String line : tooltip) {
                tooltipArray.add(line);
            }
            json.add("tooltip", tooltipArray);
        }
        return json;
    }

    @Override
    public SimpleRenderable withTooltip(String... tooltip) {
        return new SimpleRenderable(identifier, width, height, u, v, textureWidth, textureHeight, tooltip);
    }
}
