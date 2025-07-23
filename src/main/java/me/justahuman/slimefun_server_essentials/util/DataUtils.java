package me.justahuman.slimefun_server_essentials.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@UtilityClass
public class DataUtils {
    public static final int DATA_VERSION = 1;

    public void writeVersion(ByteArrayDataOutput output) {
        output.writeInt(DATA_VERSION);
    }

    public byte[] bytes(ItemStack itemStack) {
        if (itemStack == null) {
            itemStack = new ItemStack(Material.AIR);
        }

        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        final ReadWriteNBT itemNBT = NBT.itemStackToNBT(itemStack);
        final ReadWriteNBT components = itemNBT.getCompound("components");

        output.writeUTF(itemNBT.getString("id"));
        output.writeInt(itemStack.getAmount());
        if (components != null) {
            output.writeBoolean(true);
            output.writeUTF(components.toString());
        } else {
            output.writeBoolean(false);
        }
        return output.toByteArray();
    }

    public void add(ByteArrayDataOutput output, String value, String unless) {
        if (value != null && !value.equals(unless)) {
            output.writeBoolean(true);
            output.writeUTF(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public void add(ByteArrayDataOutput output, Integer value, int unless) {
        if (value != null && value != unless) {
            output.writeBoolean(true);
            output.writeInt(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public void add(ByteArrayDataOutput output, Long value, long unless) {
        if (value != null && value != unless) {
            output.writeBoolean(true);
            output.writeLong(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public void add(ByteArrayDataOutput output, Double value, double unless) {
        if (value != null && value != unless) {
            output.writeBoolean(true);
            output.writeDouble(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public void add(ByteArrayDataOutput output, Float value, float unless) {
        if (value != null && value != unless) {
            output.writeBoolean(true);
            output.writeFloat(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public static void addPositive(ByteArrayDataOutput output, Integer value) {
        if (value != null && value > 0) {
            output.writeBoolean(true);
            output.writeInt(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public void add(ByteArrayDataOutput output, boolean value, boolean should) {
        if (should) {
            output.writeBoolean(true);
            output.writeBoolean(value);
        } else {
            output.writeBoolean(false);
        }
    }

    public static void addArray(ByteArrayDataOutput output, List<String> array) {
        output.writeInt(array.size());
        for (String input : array) {
            output.writeUTF(input);
        }
    }

    public static void addTooltip(ByteArrayDataOutput output, String[] tooltip) {
        output.writeInt(tooltip.length);
        for (String line : tooltip) {
            output.writeUTF(line);
        }
    }
}
