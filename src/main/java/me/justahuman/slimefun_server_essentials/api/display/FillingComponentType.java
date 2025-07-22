package me.justahuman.slimefun_server_essentials.api.display;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import me.justahuman.slimefun_server_essentials.api.Condition;

public final class FillingComponentType extends ComponentType {
    private final CustomRenderable lightFill;
    private final CustomRenderable darkFill;
    private final String timeToFill;
    private final boolean horizontal;
    private final Condition emptyToFull;
    private final Condition startToEnd;

    public FillingComponentType(String id, CustomRenderable light, CustomRenderable lightFill, CustomRenderable dark, CustomRenderable darkFill, int width, int height, int millis, boolean horizontal) {
        this(id, light, lightFill, dark, darkFill, width, height, String.valueOf(millis), horizontal, Condition.TRUE, Condition.TRUE);
    }

    public FillingComponentType(String id, CustomRenderable light, CustomRenderable lightFill, CustomRenderable dark, CustomRenderable darkFill, int width, int height, String timeToFill, boolean horizontal, Condition emptyToFull, Condition startToEnd) {
        super(id, light, dark);
        this.lightFill = lightFill;
        this.darkFill = darkFill;
        this.timeToFill = timeToFill;
        this.horizontal = horizontal;
        this.emptyToFull = emptyToFull;
        this.startToEnd = startToEnd;
    }

    @Override
    public void toBytes(ByteArrayDataOutput output) {
        output.writeUTF(this.id);
        this.light.toBytes(output);
        this.dark.toBytes(output);
        output.writeBoolean(true);
        this.lightFill.toBytes(output);
        this.darkFill.toBytes(output);
        output.writeUTF(this.timeToFill);
        output.writeBoolean(this.horizontal);
        this.emptyToFull.toBytes(output);
        this.startToEnd.toBytes(output);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("light_fill", this.lightFill.toJson());
        json.add("dark_fill", this.darkFill.toJson());
        json.addProperty("time_to_fill", this.timeToFill);
        json.addProperty("horizontal", this.horizontal);
        json.add("empty_to_full", this.emptyToFull.toJson());
        json.add("start_to_end", this.startToEnd.toJson());
        return json;
    }
}
