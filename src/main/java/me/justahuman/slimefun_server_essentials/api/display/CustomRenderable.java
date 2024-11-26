package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonObject;

public interface CustomRenderable {
    int width();
    int height();
    JsonObject toJson();
}
