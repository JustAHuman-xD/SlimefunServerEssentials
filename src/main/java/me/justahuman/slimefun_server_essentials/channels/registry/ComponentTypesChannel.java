package me.justahuman.slimefun_server_essentials.channels.registry;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.justahuman.slimefun_server_essentials.api.display.ComponentType;
import me.justahuman.slimefun_server_essentials.channels.AbstractChannel;
import me.justahuman.slimefun_server_essentials.implementation.DisplayComponentTypes;
import me.justahuman.slimefun_server_essentials.util.JsonUtils;

import java.util.Map;

public class ComponentTypesChannel extends AbstractChannel {
    @Override
    public void load() {
        if (!messages.isEmpty()) {
            return;
        }

        for (Map.Entry<String, ComponentType> entry : DisplayComponentTypes.getComponentTypes().entrySet()) {
            JsonUtils.generated("component_type/" + format(entry.getKey()), entry.getValue().toJson());
        }

        for (Map.Entry<String, ComponentType> entry : DisplayComponentTypes.getComponentTypes().entrySet()) {
            ByteArrayDataOutput displayPacket = ByteStreams.newDataOutput();
            entry.getValue().toBytes(displayPacket);
            messages.addAll(splitMessage(displayPacket.toByteArray()));
        }
    }

    @Override
    public String getChannel() {
        return "slimefun_server_essentials:component_types";
    }
}
