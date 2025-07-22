package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;

public sealed class ComponentType permits FillingComponentType {
    protected final String id;
    protected final CustomRenderable light;
    protected final CustomRenderable dark;

    public ComponentType(String id, CustomRenderable light, CustomRenderable dark) {
        this.id = id;
        this.light = light;
        this.dark = dark;
    }

    public String id() {
        return this.id;
    }

    public CustomRenderable light() {
        return this.light;
    }

    public CustomRenderable dark() {
        return this.dark;
    }

    public int size() {
        return width();
    }

    public int size(boolean y) {
        return y ? height() : width();
    }

    public int width() {
        return Math.max(this.light.width(), this.dark.width());
    }

    public int height() {
        return Math.max(this.light.height(), this.dark.height());
    }

    public void toBytes(ByteArrayDataOutput output) {
        output.writeUTF(this.id);
        this.light.toBytes(output);
        this.dark.toBytes(output);
        output.writeBoolean(false);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.add("light", this.light.toJson());
        json.add("dark", this.dark.toJson());
        return json;
    }
}
