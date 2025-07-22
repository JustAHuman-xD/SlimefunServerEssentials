package me.justahuman.slimefun_server_essentials.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.justahuman.slimefun_server_essentials.util.DataUtils;

public record Condition(String property, String condition, String value) {
    public static final String CONDITION_GREATER = ">";
    public static final String CONDITION_GREATER_EQUALS = ">=";
    public static final String CONDITION_LESS = "<";
    public static final String CONDITION_LESS_EQUALS = "<=";
    public static final String CONDITION_EQUALS = "=";
    public static final String CONDITION_NOT_EQUALS = "!=";
    public static final String CONDITION_EXISTS = "?";

    public static final Condition TRUE = new Condition("", "", "");
    public static final Condition FALSE = new Condition("", "", "");
    public static final Condition HAS_ENERGY = new Condition(Property.ENERGY, CONDITION_NOT_EQUALS, "0");
    public static final Condition HAS_TICKS = new Condition(Property.TICKS, CONDITION_EXISTS, "0.0");

    public Condition(String property, String condition, Number value) {
        this(property, condition, String.valueOf(value));
    }

    public void toBytes(ByteArrayDataOutput output) {
        if (this == TRUE) {
            output.writeBoolean(false);
            output.writeBoolean(true);
        } else if (this == FALSE) {
            output.writeBoolean(false);
            output.writeBoolean(false);
        } else {
            output.writeBoolean(true);
            DataUtils.add(output, this.property, "0.0");
            DataUtils.add(output, this.condition, "");
            DataUtils.add(output, this.value, "0.0");
        }
    }

    public JsonElement toJson() {
        if (this == TRUE) {
            return new JsonPrimitive(true);
        } else if (this == FALSE) {
            return new JsonPrimitive(false);
        }

        JsonObject json = new JsonObject();
        json.addProperty("property", this.property);
        json.addProperty("condition", this.condition);
        json.addProperty("value", this.value);
        return json;
    }
}
