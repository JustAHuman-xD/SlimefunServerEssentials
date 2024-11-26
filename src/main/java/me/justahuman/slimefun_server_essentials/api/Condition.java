package me.justahuman.slimefun_server_essentials.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public record Condition(String property, String condition, String value) {
    public static final String PROPERTY_TICKS = "%sf_ticks%";
    public static final String PROPERTY_ENERGY = "%energy%";
    public static final String PROPERTY_INPUTS = "%inputs%";
    public static final String PROPERTY_OUTPUTS = "%outputs%";

    public static final String CONDITION_GREATER = ">";
    public static final String CONDITION_GREATER_EQUALS = ">=";
    public static final String CONDITION_LESS = "<";
    public static final String CONDITION_LESS_EQUALS = "<=";
    public static final String CONDITION_EQUALS = "=";
    public static final String CONDITION_NOT_EQUALS = "!=";

    public static final Condition TRUE = new Condition("", "", "");
    public static final Condition FALSE = new Condition("", "", "");
    public static final Condition HAS_ENERGY = new Condition(PROPERTY_ENERGY, CONDITION_NOT_EQUALS, "0");
    public static final Condition HAS_TICKS = new Condition(PROPERTY_TICKS, CONDITION_GREATER, "0");

    public Condition(String property, String condition, Number value) {
        this(property, condition, String.valueOf(value));
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
