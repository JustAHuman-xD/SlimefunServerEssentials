package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public record OptionalRenderable(CustomRenderable renderable, Condition condition) implements CustomRenderable {
    @Override
    public int width() {
        return this.renderable.width();
    }

    @Override
    public int height() {
        return this.renderable.height();
    }

    @Override
    public void toBytes(ByteArrayDataOutput output) {
        output.writeBoolean(true);
        output.writeBoolean(false);
        this.renderable.toBytes(output);
        this.condition.toBytes(output);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("renderable", this.renderable.toJson());
        json.add("condition", this.condition.toJson());
        return json;
    }

    @Override
    public CustomRenderable withTooltip(String... tooltip) {
        return new OptionalRenderable(this.renderable.withTooltip(tooltip), this.condition);
    }
}
