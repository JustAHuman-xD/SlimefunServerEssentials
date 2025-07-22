package me.justahuman.slimefun_server_essentials.implementation;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.display.ComponentType;

import java.util.HashMap;
import java.util.Map;

public class DisplayComponentTypes {
    private static final Map<String, ComponentType> DISPLAY_COMPONENTS = new HashMap<>();

    public static void register(String id, ComponentType display) {
        // TODO: How to handle ids with existing labels?
        DISPLAY_COMPONENTS.put(id, display);
    }

    public static Map<String, ComponentType> getComponentTypes() {
        return DISPLAY_COMPONENTS;
    }

    public static void clear() {
        DISPLAY_COMPONENTS.clear();
    }
}
