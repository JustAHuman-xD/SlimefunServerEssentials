package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public sealed interface CustomRenderable permits SimpleRenderable, ConditionalRenderable, OptionalRenderable {
    int width();
    int height();

    void toBytes(ByteArrayDataOutput output);
    JsonObject toJson();

    CustomRenderable withTooltip(String... tooltip);

    default ConditionalRenderable conditionalTooltip(Condition condition, String... tooltip) {
        return new ConditionalRenderable(withTooltip(tooltip), this, condition);
    }

    default OptionalRenderable optional(Condition condition) {
        return new OptionalRenderable(this, condition);
    }
}
