package me.justahuman.slimefun_server_essentials.implementation.core;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Biome;

public enum DefaultLabels {
    REQUIRES_DAY("day"),
    REQUIRES_NIGHT("night"),;

    private final @Getter String label;

    DefaultLabels(String label) {
        this.label = label;
    }

    public static String label(World.Environment environment) {
        return switch (environment) {
            case NORMAL, CUSTOM -> "overworld";
            case NETHER -> "the_nether";
            case THE_END -> "the_end";
        };
    }

    public static String label(Biome biome) {
        return "biome:" + biome.getKey().getKey();
    }
}
