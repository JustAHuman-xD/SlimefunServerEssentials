package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public record ConditionalRenderable(CustomRenderable passedRenderable, CustomRenderable failedRenderable, Condition condition) implements CustomRenderable {
    @Override
    public int width() {
        return Math.max(this.passedRenderable.width(), this.failedRenderable.width());
    }

    @Override
    public int height() {
        return Math.max(this.passedRenderable.height(), this.failedRenderable.height());
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("passed", this.passedRenderable.toJson());
        json.add("failed", this.failedRenderable.toJson());
        json.add("condition", this.condition.toJson());
        return json;
    }
}
