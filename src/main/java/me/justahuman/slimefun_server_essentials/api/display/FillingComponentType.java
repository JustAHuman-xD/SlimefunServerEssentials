package me.justahuman.slimefun_server_essentials.api.display;

import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public class FillingComponentType extends ComponentType {
    public static final String RECIPE_TIME = "%time_millis%";

    protected final CustomRenderable lightFill;
    protected final CustomRenderable darkFill;
    protected final String timeToFill;
    protected final Condition emptyToFull;
    protected final Condition startToEnd;

    public FillingComponentType(String id, CustomRenderable light, CustomRenderable lightFill, CustomRenderable dark, CustomRenderable darkFill, int width, int height, int millis) {
        this(id, light, lightFill, dark, darkFill, width, height, String.valueOf(millis), Condition.TRUE, Condition.TRUE);
    }

    public FillingComponentType(String id, CustomRenderable light, CustomRenderable lightFill, CustomRenderable dark, CustomRenderable darkFill, int width, int height, String timeToFill, Condition emptyToFull, Condition startToEnd) {
        super(id, light, dark);
        this.lightFill = lightFill;
        this.darkFill = darkFill;
        this.timeToFill = timeToFill;
        this.emptyToFull = emptyToFull;
        this.startToEnd = startToEnd;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("light_fill", this.lightFill.toJson());
        json.add("dark_fill", this.darkFill.toJson());
        json.addProperty("time_to_fill", this.timeToFill);
        json.add("empty_to_full", this.emptyToFull.toJson());
        json.add("start_to_end", this.startToEnd.toJson());
        return json;
    }
}
