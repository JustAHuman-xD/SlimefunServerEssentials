package me.justahuman.slimefun_server_essentials.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.AbstractList;
import java.util.List;

final class JsonArrayList extends AbstractList<JsonElement> {
    
    private final JsonArray jsonArray;
    
    private JsonArrayList(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }
    
    static List<JsonElement> of(JsonArray jsonArray) {
        return new JsonArrayList(jsonArray);
    }
    
    // This method is required when implementing AbstractList
    @Override
    public JsonElement get(int index) {
        return jsonArray.get(index);
    }
    
    // This method is required when implementing AbstractList as well
    @Override
    public int size() {
        return jsonArray.size();
    }
    
    // And this one is required to make the list implementation modifiable
    @Override
    public JsonElement set(int index, JsonElement element) {
        return jsonArray.set(index, element);
    }
}