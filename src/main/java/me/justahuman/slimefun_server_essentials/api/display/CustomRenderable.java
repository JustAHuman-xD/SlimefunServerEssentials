package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public interface CustomRenderable {
    int width();
    int height();
    JsonObject toJson();

    CustomRenderable withTooltip(String... tooltip);

    default ConditionalRenderable conditionalTooltip(Condition condition, String... tooltip) {
        return new ConditionalRenderable(withTooltip(tooltip), this, condition);
    }

    default OptionalRenderable optional(Condition condition) {
        return new OptionalRenderable(this, condition);
    }
}
