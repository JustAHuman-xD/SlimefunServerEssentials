package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public record ConditionalRenderable(
        CustomRenderable passedRenderable,
        CustomRenderable failedRenderable,
        Condition condition
) implements CustomRenderable {
    @Override
    public int width() {
        return Math.max(this.passedRenderable.width(), this.failedRenderable.width());
    }

    @Override
    public int height() {
        return Math.max(this.passedRenderable.height(), this.failedRenderable.height());
    }

    @Override
    public void toBytes(ByteArrayDataOutput output) {
        output.writeBoolean(true);
        output.writeBoolean(true);
        this.passedRenderable.toBytes(output);
        this.failedRenderable.toBytes(output);
        this.condition.toBytes(output);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("passed", this.passedRenderable.toJson());
        json.add("failed", this.failedRenderable.toJson());
        json.add("condition", this.condition.toJson());
        return json;
    }

    @Override
    public CustomRenderable withTooltip(String... tooltip) {
        return new ConditionalRenderable(this.passedRenderable.withTooltip(tooltip), this.failedRenderable.withTooltip(tooltip), this.condition);
    }
}
