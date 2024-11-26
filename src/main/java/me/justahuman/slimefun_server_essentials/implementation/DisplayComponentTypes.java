package me.justahuman.slimefun_server_essentials.implementation;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class DisplayComponentTypes {
    private static final Map<String, JsonObject> DISPLAY_COMPONENTS = new HashMap<>();

    public static void register(String id, JsonObject display) {
        // TODO: How to handle ids with existing labels?
        DISPLAY_COMPONENTS.put(id, display);
    }

    public static Map<String, JsonObject> getComponentTypes() {
        return DISPLAY_COMPONENTS;
    }
}
